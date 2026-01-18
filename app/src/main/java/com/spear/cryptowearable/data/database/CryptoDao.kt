package com.spear.cryptowearable.data.database

import androidx.room.*
import com.spear.cryptowearable.data.model.PortfolioItem
import com.spear.cryptowearable.data.model.PriceAlert
import com.spear.cryptowearable.data.model.WatchlistItem
import kotlinx.coroutines.flow.Flow

/**
 * DAO for Watchlist operations
 */
@Dao
interface WatchlistDao {
    @Query("SELECT * FROM watchlist ORDER BY sortOrder ASC, addedAt DESC")
    fun getAllWatchlistItems(): Flow<List<WatchlistItem>>
    
    @Query("SELECT * FROM watchlist WHERE cryptoId = :cryptoId")
    suspend fun getWatchlistItem(cryptoId: String): WatchlistItem?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatchlistItem(item: WatchlistItem)
    
    @Delete
    suspend fun deleteWatchlistItem(item: WatchlistItem)
    
    @Query("DELETE FROM watchlist WHERE cryptoId = :cryptoId")
    suspend fun deleteWatchlistItemById(cryptoId: String)
    
    @Query("SELECT cryptoId FROM watchlist")
    suspend fun getAllWatchlistIds(): List<String>
}

/**
 * DAO for Portfolio operations
 */
@Dao
interface PortfolioDao {
    @Query("SELECT * FROM portfolio ORDER BY purchaseDate DESC")
    fun getAllPortfolioItems(): Flow<List<PortfolioItem>>
    
    @Query("SELECT * FROM portfolio WHERE cryptoId = :cryptoId")
    suspend fun getPortfolioItem(cryptoId: String): PortfolioItem?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPortfolioItem(item: PortfolioItem)
    
    @Update
    suspend fun updatePortfolioItem(item: PortfolioItem)
    
    @Delete
    suspend fun deletePortfolioItem(item: PortfolioItem)
    
    @Query("DELETE FROM portfolio WHERE cryptoId = :cryptoId")
    suspend fun deletePortfolioItemById(cryptoId: String)
}

/**
 * DAO for Price Alerts operations
 */
@Dao
interface PriceAlertDao {
    @Query("SELECT * FROM price_alerts ORDER BY createdAt DESC")
    fun getAllAlerts(): Flow<List<PriceAlert>>
    
    @Query("SELECT * FROM price_alerts WHERE isEnabled = 1 AND isTriggered = 0")
    suspend fun getActiveAlerts(): List<PriceAlert>
    
    @Query("SELECT * FROM price_alerts WHERE id = :id")
    suspend fun getAlertById(id: Long): PriceAlert?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: PriceAlert): Long
    
    @Update
    suspend fun updateAlert(alert: PriceAlert)
    
    @Delete
    suspend fun deleteAlert(alert: PriceAlert)
    
    @Query("UPDATE price_alerts SET isEnabled = :enabled WHERE id = :id")
    suspend fun setAlertEnabled(id: Long, enabled: Boolean)
    
    @Query("UPDATE price_alerts SET isTriggered = :triggered WHERE id = :id")
    suspend fun setAlertTriggered(id: Long, triggered: Boolean)
}
