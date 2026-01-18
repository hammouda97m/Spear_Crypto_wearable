package com.spear.cryptowearable.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.spear.cryptowearable.data.model.ChartDataPoint
import com.spear.cryptowearable.data.model.Cryptocurrency
import com.spear.cryptowearable.data.repository.CryptoRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel for cryptocurrency detail and chart view
 */
class CryptoDetailViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = CryptoRepository(application)
    
    private val _uiState = MutableStateFlow<CryptoDetailUiState>(CryptoDetailUiState.Loading)
    val uiState: StateFlow<CryptoDetailUiState> = _uiState.asStateFlow()
    
    private val _chartData = MutableStateFlow<List<ChartDataPoint>>(emptyList())
    val chartData: StateFlow<List<ChartDataPoint>> = _chartData.asStateFlow()
    
    private val _selectedDays = MutableStateFlow(7)
    val selectedDays: StateFlow<Int> = _selectedDays.asStateFlow()
    
    fun loadCryptoDetail(cryptoId: String) {
        viewModelScope.launch {
            _uiState.value = CryptoDetailUiState.Loading
            
            val result = repository.getMarketData(listOf(cryptoId), forceRefresh = true)
            
            if (result.isSuccess) {
                val crypto = result.getOrNull()?.firstOrNull()
                if (crypto != null) {
                    _uiState.value = CryptoDetailUiState.Success(crypto)
                    loadChartData(cryptoId, _selectedDays.value)
                } else {
                    _uiState.value = CryptoDetailUiState.Error("Cryptocurrency not found")
                }
            } else {
                _uiState.value = CryptoDetailUiState.Error(
                    result.exceptionOrNull()?.message ?: "Unknown error"
                )
            }
        }
    }
    
    fun loadChartData(cryptoId: String, days: Int) {
        viewModelScope.launch {
            _selectedDays.value = days
            val result = repository.getMarketChart(cryptoId, days)
            
            if (result.isSuccess) {
                _chartData.value = result.getOrNull() ?: emptyList()
            }
        }
    }
}

sealed class CryptoDetailUiState {
    object Loading : CryptoDetailUiState()
    data class Success(val crypto: Cryptocurrency) : CryptoDetailUiState()
    data class Error(val message: String) : CryptoDetailUiState()
}
