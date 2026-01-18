package com.spear.cryptowearable.data.api

import com.spear.cryptowearable.data.model.Cryptocurrency
import com.spear.cryptowearable.data.model.MarketChart
import com.spear.cryptowearable.data.model.SearchResult
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * CoinGecko API service interface
 * API Documentation: https://www.coingecko.com/en/api/documentation
 */
interface CoinGeckoService {
    
    /**
     * Get list of cryptocurrencies with market data
     * @param vsCurrency The target currency (usd, eur, etc.)
     * @param ids Comma-separated crypto ids to filter
     * @param order Sort order (market_cap_desc, volume_desc, etc.)
     * @param perPage Number of results per page
     * @param page Page number
     */
    @GET("coins/markets")
    suspend fun getMarkets(
        @Query("vs_currency") vsCurrency: String = "usd",
        @Query("ids") ids: String? = null,
        @Query("order") order: String = "market_cap_desc",
        @Query("per_page") perPage: Int = 100,
        @Query("page") page: Int = 1,
        @Query("sparkline") sparkline: Boolean = false,
        @Query("price_change_percentage") priceChangePercentage: String = "24h"
    ): Response<List<Cryptocurrency>>
    
    /**
     * Get specific cryptocurrency details
     * @param id Cryptocurrency id (bitcoin, ethereum, etc.)
     */
    @GET("coins/{id}")
    suspend fun getCoinDetails(
        @Path("id") id: String,
        @Query("localization") localization: Boolean = false,
        @Query("tickers") tickers: Boolean = false,
        @Query("market_data") marketData: Boolean = true,
        @Query("community_data") communityData: Boolean = false,
        @Query("developer_data") developerData: Boolean = false
    ): Response<Map<String, Any>>
    
    /**
     * Get historical market data (price chart)
     * @param id Cryptocurrency id
     * @param vsCurrency Target currency
     * @param days Number of days (1, 7, 14, 30, 90, 180, 365, max)
     */
    @GET("coins/{id}/market_chart")
    suspend fun getMarketChart(
        @Path("id") id: String,
        @Query("vs_currency") vsCurrency: String = "usd",
        @Query("days") days: Int = 7
    ): Response<MarketChart>
    
    /**
     * Search for cryptocurrencies
     * @param query Search term
     */
    @GET("search")
    suspend fun searchCoins(
        @Query("query") query: String
    ): Response<SearchResult>
    
    /**
     * Ping API to check if it's online
     */
    @GET("ping")
    suspend fun ping(): Response<Map<String, String>>
}
