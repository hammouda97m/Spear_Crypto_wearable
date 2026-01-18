package com.spear.cryptowearable.tile

import androidx.wear.protolayout.*
import androidx.wear.protolayout.material.Text
import androidx.wear.protolayout.material.layouts.PrimaryLayout
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.ResourceBuilders
import androidx.wear.tiles.TileBuilders
import androidx.wear.tiles.TileService
import androidx.wear.tiles.TimelineBuilders
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.spear.cryptowearable.data.repository.CryptoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.guava.future
import kotlinx.coroutines.launch

/**
 * Tile service for displaying quick crypto info on watch face
 */
class CryptoTileService : TileService() {
    
    private val serviceScope = CoroutineScope(Dispatchers.IO)
    
    override fun onTileRequest(requestParams: RequestBuilders.TileRequest): ListenableFuture<TileBuilders.Tile> {
        return serviceScope.future {
            val repository = CryptoRepository(applicationContext)
            
            // Get first watchlist item for tile display
            val watchlistIds = repository.getWatchlistIds()
            val cryptoText = if (watchlistIds.isNotEmpty()) {
                val result = repository.getMarketData(listOf(watchlistIds.first()))
                if (result.isSuccess) {
                    val crypto = result.getOrNull()?.firstOrNull()
                    if (crypto != null) {
                        "${crypto.symbol.uppercase()}: ${"$%.2f".format(crypto.currentPrice)}"
                    } else {
                        "No data"
                    }
                } else {
                    "Error loading"
                }
            } else {
                "Add cryptos to watchlist"
            }
            
            TileBuilders.Tile.Builder()
                .setResourcesVersion("1")
                .setTimeline(
                    TimelineBuilders.Timeline.Builder()
                        .addTimelineEntry(
                            TimelineBuilders.TimelineEntry.Builder()
                                .setLayout(
                                    LayoutElementBuilders.Layout.Builder()
                                        .setRoot(
                                            createTileLayout(cryptoText)
                                        )
                                        .build()
                                )
                                .build()
                        )
                        .build()
                )
                .build()
        }
    }
    
    private fun createTileLayout(text: String): LayoutElementBuilders.LayoutElement {
        return LayoutElementBuilders.Box.Builder()
            .setWidth(DimensionBuilders.expand())
            .setHeight(DimensionBuilders.expand())
            .addContent(
                LayoutElementBuilders.Text.Builder()
                    .setText(text)
                    .setFontStyle(
                        LayoutElementBuilders.FontStyle.Builder()
                            .build()
                    )
                    .build()
            )
            .build()
    }
    
    override fun onTileResourcesRequest(requestParams: RequestBuilders.ResourcesRequest): ListenableFuture<ResourceBuilders.Resources> {
        return Futures.immediateFuture(
            ResourceBuilders.Resources.Builder()
                .setVersion("1")
                .build()
        )
    }
}
