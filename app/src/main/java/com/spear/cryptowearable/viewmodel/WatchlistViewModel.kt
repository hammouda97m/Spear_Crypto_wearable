package com.spear.cryptowearable.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.spear.cryptowearable.data.model.*
import com.spear.cryptowearable.data.repository.CryptoRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel for the main screen displaying the watchlist
 */
class WatchlistViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = CryptoRepository(application)
    
    private val _uiState = MutableStateFlow<WatchlistUiState>(WatchlistUiState.Loading)
    val uiState: StateFlow<WatchlistUiState> = _uiState.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _searchResults = MutableStateFlow<List<CoinSearchItem>>(emptyList())
    val searchResults: StateFlow<List<CoinSearchItem>> = _searchResults.asStateFlow()
    
    init {
        loadWatchlist()
    }
    
    fun loadWatchlist(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.value = WatchlistUiState.Loading
            
            repository.getWatchlist().collect { watchlistItems ->
                if (watchlistItems.isEmpty()) {
                    _uiState.value = WatchlistUiState.Empty
                } else {
                    // Fetch prices for watchlist items
                    val ids = watchlistItems.map { it.cryptoId }
                    val result = repository.getMarketData(ids, forceRefresh)
                    
                    if (result.isSuccess) {
                        val cryptoMap = result.getOrNull()?.associateBy { it.id } ?: emptyMap()
                        val items = watchlistItems.mapNotNull { watchlistItem ->
                            cryptoMap[watchlistItem.cryptoId]?.let { crypto ->
                                WatchlistItemWithPrice(watchlistItem, crypto)
                            }
                        }
                        _uiState.value = WatchlistUiState.Success(items)
                    } else {
                        _uiState.value = WatchlistUiState.Error(
                            result.exceptionOrNull()?.message ?: "Unknown error"
                        )
                    }
                }
            }
        }
    }
    
    fun removeFromWatchlist(cryptoId: String) {
        viewModelScope.launch {
            repository.removeFromWatchlist(cryptoId)
            loadWatchlist()
        }
    }
    
    fun addToWatchlist(coinSearchItem: CoinSearchItem) {
        viewModelScope.launch {
            val watchlistItem = WatchlistItem(
                cryptoId = coinSearchItem.id,
                symbol = coinSearchItem.symbol.uppercase(),
                name = coinSearchItem.name
            )
            repository.addToWatchlist(watchlistItem)
            _searchQuery.value = ""
            _searchResults.value = emptyList()
            loadWatchlist()
        }
    }
    
    fun searchCoins(query: String) {
        _searchQuery.value = query
        if (query.length < 2) {
            _searchResults.value = emptyList()
            return
        }
        
        viewModelScope.launch {
            val result = repository.searchCoins(query)
            if (result.isSuccess) {
                _searchResults.value = result.getOrNull() ?: emptyList()
            }
        }
    }
    
    fun clearSearch() {
        _searchQuery.value = ""
        _searchResults.value = emptyList()
    }
}

data class WatchlistItemWithPrice(
    val watchlistItem: WatchlistItem,
    val crypto: Cryptocurrency
)

sealed class WatchlistUiState {
    object Loading : WatchlistUiState()
    object Empty : WatchlistUiState()
    data class Success(val items: List<WatchlistItemWithPrice>) : WatchlistUiState()
    data class Error(val message: String) : WatchlistUiState()
}
