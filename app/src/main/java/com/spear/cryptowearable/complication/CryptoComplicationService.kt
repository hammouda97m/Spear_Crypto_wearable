package com.spear.cryptowearable.complication

import android.app.PendingIntent
import android.content.Intent
import androidx.wear.watchface.complications.data.*
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import androidx.wear.watchface.complications.datasource.SuspendingComplicationDataSourceService
import com.spear.cryptowearable.data.repository.CryptoRepository
import com.spear.cryptowearable.ui.MainActivity

/**
 * Complication data source for displaying crypto prices on watch face
 */
class CryptoComplicationService : SuspendingComplicationDataSourceService() {
    
    override fun getPreviewData(type: ComplicationType): ComplicationData {
        return when (type) {
            ComplicationType.SHORT_TEXT -> {
                ShortTextComplicationData.Builder(
                    text = PlainComplicationText.Builder("BTC").build(),
                    contentDescription = PlainComplicationText.Builder("Bitcoin Price").build()
                )
                    .setTitle(PlainComplicationText.Builder("$50,000").build())
                    .build()
            }
            ComplicationType.LONG_TEXT -> {
                LongTextComplicationData.Builder(
                    text = PlainComplicationText.Builder("BTC: $50,000").build(),
                    contentDescription = PlainComplicationText.Builder("Bitcoin Price").build()
                )
                    .build()
            }
            ComplicationType.RANGED_VALUE -> {
                RangedValueComplicationData.Builder(
                    value = 50f,
                    min = 0f,
                    max = 100f,
                    contentDescription = PlainComplicationText.Builder("Bitcoin 24h change").build()
                )
                    .setText(PlainComplicationText.Builder("+5%").build())
                    .build()
            }
            else -> {
                NoDataComplicationData()
            }
        }
    }
    
    override suspend fun onComplicationRequest(request: ComplicationRequest): ComplicationData {
        val repository = CryptoRepository(applicationContext)
        
        // Get first watchlist item
        val watchlistIds = repository.getWatchlistIds()
        
        if (watchlistIds.isEmpty()) {
            return NoDataComplicationData()
        }
        
        val result = repository.getMarketData(listOf(watchlistIds.first()))
        val crypto = result.getOrNull()?.firstOrNull()
        
        if (crypto == null) {
            return NoDataComplicationData()
        }
        
        val tapIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            tapIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        return when (request.complicationType) {
            ComplicationType.SHORT_TEXT -> {
                ShortTextComplicationData.Builder(
                    text = PlainComplicationText.Builder(crypto.symbol.uppercase()).build(),
                    contentDescription = PlainComplicationText.Builder("${crypto.name} Price").build()
                )
                    .setTitle(PlainComplicationText.Builder("${"$%.0f".format(crypto.currentPrice)}").build())
                    .setTapAction(pendingIntent)
                    .build()
            }
            ComplicationType.LONG_TEXT -> {
                LongTextComplicationData.Builder(
                    text = PlainComplicationText.Builder(
                        "${crypto.symbol.uppercase()}: ${"$%.2f".format(crypto.currentPrice)}"
                    ).build(),
                    contentDescription = PlainComplicationText.Builder("${crypto.name} Price").build()
                )
                    .setTapAction(pendingIntent)
                    .build()
            }
            ComplicationType.RANGED_VALUE -> {
                val change = crypto.priceChangePercentage24h ?: 0.0
                val normalizedValue = ((change + 10) / 20 * 100).coerceIn(0.0, 100.0).toFloat()
                
                RangedValueComplicationData.Builder(
                    value = normalizedValue,
                    min = 0f,
                    max = 100f,
                    contentDescription = PlainComplicationText.Builder("${crypto.name} 24h change").build()
                )
                    .setText(PlainComplicationText.Builder("${if (change >= 0) "+" else ""}${"%.1f".format(change)}%").build())
                    .setTapAction(pendingIntent)
                    .build()
            }
            else -> {
                NoDataComplicationData()
            }
        }
    }
}
