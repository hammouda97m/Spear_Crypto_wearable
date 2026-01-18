package com.spear.cryptowearable.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.spear.cryptowearable.R
import com.spear.cryptowearable.data.model.PriceAlert
import com.spear.cryptowearable.ui.MainActivity

/**
 * Helper class for managing notifications
 */
class NotificationHelper(private val context: Context) {
    
    private val notificationManager = NotificationManagerCompat.from(context)
    
    init {
        createNotificationChannels()
    }
    
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val priceAlertChannel = NotificationChannel(
                PRICE_ALERT_CHANNEL_ID,
                "Price Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for cryptocurrency price alerts"
                enableVibration(true)
            }
            
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(priceAlertChannel)
        }
    }
    
    /**
     * Send a price alert notification
     */
    fun sendPriceAlert(alert: PriceAlert, currentPrice: Double) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            alert.id.toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val direction = if (alert.isAbove) "above" else "below"
        val title = "${alert.symbol} Price Alert"
        val message = "${alert.name} is now $${"%.2f".format(currentPrice)} ($direction $${"%.2f".format(alert.targetPrice)})"
        
        val notification = NotificationCompat.Builder(context, PRICE_ALERT_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVibrate(longArrayOf(0, 500, 250, 500))
            .build()
        
        notificationManager.notify(alert.id.toInt(), notification)
    }
    
    companion object {
        private const val PRICE_ALERT_CHANNEL_ID = "price_alerts"
    }
}
