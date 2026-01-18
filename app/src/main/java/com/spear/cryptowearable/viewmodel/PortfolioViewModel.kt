package com.spear.cryptowearable.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.spear.cryptowearable.data.model.Cryptocurrency
import com.spear.cryptowearable.data.model.PortfolioItem
import com.spear.cryptowearable.data.repository.CryptoRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel for portfolio management
 */
class PortfolioViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = CryptoRepository(application)
    
    private val _uiState = MutableStateFlow<PortfolioUiState>(PortfolioUiState.Loading)
    val uiState: StateFlow<PortfolioUiState> = _uiState.asStateFlow()
    
    init {
        loadPortfolio()
    }
    
    fun loadPortfolio(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.value = PortfolioUiState.Loading
            
            repository.getPortfolio().collect { portfolioItems ->
                if (portfolioItems.isEmpty()) {
                    _uiState.value = PortfolioUiState.Empty
                } else {
                    // Fetch current prices
                    val ids = portfolioItems.map { it.cryptoId }
                    val result = repository.getMarketData(ids, forceRefresh)
                    
                    if (result.isSuccess) {
                        val cryptoMap = result.getOrNull()?.associateBy { it.id } ?: emptyMap()
                        val holdings = portfolioItems.mapNotNull { portfolioItem ->
                            cryptoMap[portfolioItem.cryptoId]?.let { crypto ->
                                PortfolioHolding(
                                    item = portfolioItem,
                                    currentPrice = crypto.currentPrice,
                                    currentValue = portfolioItem.amount * crypto.currentPrice,
                                    purchaseValue = portfolioItem.amount * portfolioItem.purchasePrice,
                                    profitLoss = (portfolioItem.amount * crypto.currentPrice) - 
                                                (portfolioItem.amount * portfolioItem.purchasePrice),
                                    profitLossPercentage = ((crypto.currentPrice - portfolioItem.purchasePrice) / 
                                                            portfolioItem.purchasePrice) * 100
                                )
                            }
                        }
                        
                        val totalValue = holdings.sumOf { it.currentValue }
                        val totalProfitLoss = holdings.sumOf { it.profitLoss }
                        
                        _uiState.value = PortfolioUiState.Success(
                            holdings = holdings,
                            totalValue = totalValue,
                            totalProfitLoss = totalProfitLoss
                        )
                    } else {
                        _uiState.value = PortfolioUiState.Error(
                            result.exceptionOrNull()?.message ?: "Unknown error"
                        )
                    }
                }
            }
        }
    }
    
    fun addToPortfolio(cryptoId: String, symbol: String, name: String, amount: Double, purchasePrice: Double) {
        viewModelScope.launch {
            val portfolioItem = PortfolioItem(
                cryptoId = cryptoId,
                symbol = symbol,
                name = name,
                amount = amount,
                purchasePrice = purchasePrice
            )
            repository.addToPortfolio(portfolioItem)
            loadPortfolio()
        }
    }
    
    fun updatePortfolio(item: PortfolioItem) {
        viewModelScope.launch {
            repository.updatePortfolio(item)
            loadPortfolio()
        }
    }
    
    fun removeFromPortfolio(cryptoId: String) {
        viewModelScope.launch {
            repository.removeFromPortfolio(cryptoId)
            loadPortfolio()
        }
    }
}

data class PortfolioHolding(
    val item: PortfolioItem,
    val currentPrice: Double,
    val currentValue: Double,
    val purchaseValue: Double,
    val profitLoss: Double,
    val profitLossPercentage: Double
)

sealed class PortfolioUiState {
    object Loading : PortfolioUiState()
    object Empty : PortfolioUiState()
    data class Success(
        val holdings: List<PortfolioHolding>,
        val totalValue: Double,
        val totalProfitLoss: Double
    ) : PortfolioUiState()
    data class Error(val message: String) : PortfolioUiState()
}
