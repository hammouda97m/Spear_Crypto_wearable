package com.spear.cryptowearable.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.spear.cryptowearable.data.model.PriceAlert
import com.spear.cryptowearable.data.repository.CryptoRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel for price alerts management
 */
class AlertsViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = CryptoRepository(application)
    
    private val _uiState = MutableStateFlow<AlertsUiState>(AlertsUiState.Loading)
    val uiState: StateFlow<AlertsUiState> = _uiState.asStateFlow()
    
    init {
        loadAlerts()
    }
    
    fun loadAlerts() {
        viewModelScope.launch {
            _uiState.value = AlertsUiState.Loading
            
            repository.getAllAlerts().collect { alerts ->
                if (alerts.isEmpty()) {
                    _uiState.value = AlertsUiState.Empty
                } else {
                    _uiState.value = AlertsUiState.Success(alerts)
                }
            }
        }
    }
    
    fun addAlert(
        cryptoId: String,
        symbol: String,
        name: String,
        targetPrice: Double,
        isAbove: Boolean
    ) {
        viewModelScope.launch {
            val alert = PriceAlert(
                cryptoId = cryptoId,
                symbol = symbol,
                name = name,
                targetPrice = targetPrice,
                isAbove = isAbove
            )
            repository.addAlert(alert)
        }
    }
    
    fun toggleAlert(alert: PriceAlert) {
        viewModelScope.launch {
            repository.setAlertEnabled(alert.id, !alert.isEnabled)
        }
    }
    
    fun deleteAlert(alert: PriceAlert) {
        viewModelScope.launch {
            repository.deleteAlert(alert)
        }
    }
    
    fun resetTriggeredAlert(alert: PriceAlert) {
        viewModelScope.launch {
            repository.setAlertTriggered(alert.id, false)
        }
    }
}

sealed class AlertsUiState {
    object Loading : AlertsUiState()
    object Empty : AlertsUiState()
    data class Success(val alerts: List<PriceAlert>) : AlertsUiState()
    data class Error(val message: String) : AlertsUiState()
}
