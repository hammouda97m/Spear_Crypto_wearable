package com.spear.cryptowearable.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * Cryptocurrency data model for API response
 */
data class Cryptocurrency(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("symbol")
    val symbol: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("image")
    val image: String?,
    
    @SerializedName("current_price")
    val currentPrice: Double,
    
    @SerializedName("price_change_percentage_24h")
    val priceChangePercentage24h: Double?,
    
    @SerializedName("market_cap")
    val marketCap: Long?,
    
    @SerializedName("total_volume")
    val totalVolume: Long?,
    
    @SerializedName("high_24h")
    val high24h: Double?,
    
    @SerializedName("low_24h")
    val low24h: Double?,
    
    @SerializedName("market_cap_rank")
    val marketCapRank: Int?,
    
    val lastUpdated: Long = System.currentTimeMillis()
)

/**
 * Watchlist entity for Room database
 */
@Entity(tableName = "watchlist")
data class WatchlistItem(
    @PrimaryKey
    val cryptoId: String,
    val symbol: String,
    val name: String,
    val addedAt: Long = System.currentTimeMillis(),
    val sortOrder: Int = 0
)

/**
 * Portfolio entity for Room database
 */
@Entity(tableName = "portfolio")
data class PortfolioItem(
    @PrimaryKey
    val cryptoId: String,
    val symbol: String,
    val name: String,
    val amount: Double,
    val purchasePrice: Double,
    val purchaseDate: Long = System.currentTimeMillis()
)

/**
 * Price alert entity for Room database
 */
@Entity(tableName = "price_alerts")
data class PriceAlert(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val cryptoId: String,
    val symbol: String,
    val name: String,
    val targetPrice: Double,
    val isAbove: Boolean, // true = alert when price goes above, false = alert when below
    val isEnabled: Boolean = true,
    val isTriggered: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Market chart data for displaying price history
 */
data class MarketChart(
    @SerializedName("prices")
    val prices: List<List<Double>>
)

/**
 * Chart data point
 */
data class ChartDataPoint(
    val timestamp: Long,
    val price: Double
)

/**
 * Search result from CoinGecko
 */
data class SearchResult(
    @SerializedName("coins")
    val coins: List<CoinSearchItem>
)

data class CoinSearchItem(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("symbol")
    val symbol: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("thumb")
    val thumb: String?,
    
    @SerializedName("market_cap_rank")
    val marketCapRank: Int?
)
