package com.spear.cryptowearable.worker

import android.content.Context
import androidx.work.*
import com.spear.cryptowearable.data.repository.CryptoRepository
import com.spear.cryptowearable.notification.NotificationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

/**
 * Worker for periodic price updates and alert monitoring
 */
class PriceUpdateWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    private val repository = CryptoRepository(context)
    private val notificationHelper = NotificationHelper(context)
    
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // Get active alerts
            val alerts = repository.getActiveAlerts()
            
            if (alerts.isNotEmpty()) {
                // Get unique crypto IDs from alerts
                val cryptoIds = alerts.map { it.cryptoId }.distinct()
                
                // Fetch current prices
                val marketResult = repository.getMarketData(cryptoIds, forceRefresh = true)
                
                if (marketResult.isSuccess) {
                    val cryptoMap = marketResult.getOrNull()?.associateBy { it.id } ?: emptyMap()
                    
                    // Check each alert
                    alerts.forEach { alert ->
                        val crypto = cryptoMap[alert.cryptoId]
                        if (crypto != null) {
                            val currentPrice = crypto.currentPrice
                            val shouldTrigger = if (alert.isAbove) {
                                currentPrice >= alert.targetPrice
                            } else {
                                currentPrice <= alert.targetPrice
                            }
                            
                            if (shouldTrigger && !alert.isTriggered) {
                                // Trigger notification
                                notificationHelper.sendPriceAlert(
                                    alert = alert,
                                    currentPrice = currentPrice
                                )
                                // Mark as triggered
                                repository.setAlertTriggered(alert.id, true)
                            }
                        }
                    }
                }
            }
            
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
    
    companion object {
        private const val WORK_NAME = "PriceUpdateWork"
        
        /**
         * Schedule periodic work for price updates
         */
        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            
            val workRequest = PeriodicWorkRequestBuilder<PriceUpdateWorker>(
                15, TimeUnit.MINUTES,
                5, TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.LINEAR,
                    WorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .build()
            
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }
        
        /**
         * Cancel scheduled work
         */
        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }
}
