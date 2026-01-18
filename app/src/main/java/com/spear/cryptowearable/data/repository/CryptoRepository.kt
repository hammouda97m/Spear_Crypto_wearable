package com.spear.cryptowearable.data.repository

import android.content.Context
import com.spear.cryptowearable.data.api.ApiClient
import com.spear.cryptowearable.data.database.CryptoDatabase
import com.spear.cryptowearable.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException

/**
 * Repository for cryptocurrency data
 * Single source of truth for all crypto-related data
 */
class CryptoRepository(context: Context) {
    
    private val api = ApiClient.coinGeckoService
    private val database = CryptoDatabase.getDatabase(context)
    private val watchlistDao = database.watchlistDao()
    private val portfolioDao = database.portfolioDao()
    private val alertDao = database.priceAlertDao()
    
    // Cache for cryptocurrency prices (in-memory)
    private val priceCache = mutableMapOf<String, Cryptocurrency>()
    private var lastCacheUpdate = 0L
    private val CACHE_DURATION = 60_000L // 1 minute
    
    /**
     * Get cryptocurrency market data
     */
    suspend fun getMarketData(
        ids: List<String>? = null,
        forceRefresh: Boolean = false
    ): Result<List<Cryptocurrency>> {
        return try {
            // Check cache first
            val now = System.currentTimeMillis()
            if (!forceRefresh && ids != null && 
                (now - lastCacheUpdate) < CACHE_DURATION && 
                ids.all { priceCache.containsKey(it) }) {
                return Result.success(ids.mapNotNull { priceCache[it] })
            }
            
            val idsString = ids?.joinToString(",")
            val response = api.getMarkets(ids = idsString, perPage = 250)
            
            if (response.isSuccessful && response.body() != null) {
                val cryptos = response.body()!!
                // Update cache
                cryptos.forEach { priceCache[it.id] = it }
                lastCacheUpdate = now
                Result.success(cryptos)
            } else {
                Result.failure(Exception("API Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Network error: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(Exception("Error fetching market data: ${e.message}"))
        }
    }
    
    /**
     * Get price history chart data
     */
    suspend fun getMarketChart(
        cryptoId: String,
        days: Int = 7
    ): Result<List<ChartDataPoint>> {
        return try {
            val response = api.getMarketChart(cryptoId, days = days)
            
            if (response.isSuccessful && response.body() != null) {
                val chartData = response.body()!!.prices.map { 
                    ChartDataPoint(
                        timestamp = it[0].toLong(),
                        price = it[1]
                    )
                }
                Result.success(chartData)
            } else {
                Result.failure(Exception("API Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error fetching chart data: ${e.message}"))
        }
    }
    
    /**
     * Search for cryptocurrencies
     */
    suspend fun searchCoins(query: String): Result<List<CoinSearchItem>> {
        return try {
            val response = api.searchCoins(query)
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.coins)
            } else {
                Result.failure(Exception("Search failed"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error searching: ${e.message}"))
        }
    }
    
    // Watchlist operations
    fun getWatchlist(): Flow<List<WatchlistItem>> = watchlistDao.getAllWatchlistItems()
    
    suspend fun addToWatchlist(item: WatchlistItem) = watchlistDao.insertWatchlistItem(item)
    
    suspend fun removeFromWatchlist(cryptoId: String) = watchlistDao.deleteWatchlistItemById(cryptoId)
    
    suspend fun isInWatchlist(cryptoId: String): Boolean = watchlistDao.getWatchlistItem(cryptoId) != null
    
    suspend fun getWatchlistIds(): List<String> = watchlistDao.getAllWatchlistIds()
    
    /**
     * Get watchlist with current prices
     */
    fun getWatchlistWithPrices(): Flow<Result<List<Pair<WatchlistItem, Cryptocurrency?>>>> = flow {
        watchlistDao.getAllWatchlistItems().collect { watchlistItems ->
            if (watchlistItems.isEmpty()) {
                emit(Result.success(emptyList()))
            } else {
                val ids = watchlistItems.map { it.cryptoId }
                val marketResult = getMarketData(ids)
                
                if (marketResult.isSuccess) {
                    val cryptoMap = marketResult.getOrNull()?.associateBy { it.id } ?: emptyMap()
                    val pairs = watchlistItems.map { it to cryptoMap[it.cryptoId] }
                    emit(Result.success(pairs))
                } else {
                    emit(Result.failure(marketResult.exceptionOrNull() ?: Exception("Unknown error")))
                }
            }
        }
    }
    
    // Portfolio operations
    fun getPortfolio(): Flow<List<PortfolioItem>> = portfolioDao.getAllPortfolioItems()
    
    suspend fun addToPortfolio(item: PortfolioItem) = portfolioDao.insertPortfolioItem(item)
    
    suspend fun updatePortfolio(item: PortfolioItem) = portfolioDao.updatePortfolioItem(item)
    
    suspend fun removeFromPortfolio(cryptoId: String) = portfolioDao.deletePortfolioItemById(cryptoId)
    
    suspend fun getPortfolioItem(cryptoId: String): PortfolioItem? = portfolioDao.getPortfolioItem(cryptoId)
    
    // Price alert operations
    fun getAllAlerts(): Flow<List<PriceAlert>> = alertDao.getAllAlerts()
    
    suspend fun getActiveAlerts(): List<PriceAlert> = alertDao.getActiveAlerts()
    
    suspend fun addAlert(alert: PriceAlert): Long = alertDao.insertAlert(alert)
    
    suspend fun updateAlert(alert: PriceAlert) = alertDao.updateAlert(alert)
    
    suspend fun deleteAlert(alert: PriceAlert) = alertDao.deleteAlert(alert)
    
    suspend fun setAlertEnabled(id: Long, enabled: Boolean) = alertDao.setAlertEnabled(id, enabled)
    
    suspend fun setAlertTriggered(id: Long, triggered: Boolean) = alertDao.setAlertTriggered(id, triggered)
    
    /**
     * Clear price cache
     */
    fun clearCache() {
        priceCache.clear()
        lastCacheUpdate = 0L
    }
}
