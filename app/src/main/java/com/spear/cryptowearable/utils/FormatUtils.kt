package com.spear.cryptowearable.utils

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Utility functions for formatting
 */
object FormatUtils {
    
    private val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US)
    private val percentFormatter = NumberFormat.getPercentInstance(Locale.US).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }
    
    /**
     * Format price as currency
     */
    fun formatPrice(price: Double): String {
        return currencyFormatter.format(price)
    }
    
    /**
     * Format percentage change
     */
    fun formatPercentage(percentage: Double): String {
        return percentFormatter.format(percentage / 100.0)
    }
    
    /**
     * Format large numbers (market cap, volume)
     */
    fun formatLargeNumber(number: Long): String {
        return when {
            number >= 1_000_000_000_000 -> String.format("$%.2fT", number / 1_000_000_000_000.0)
            number >= 1_000_000_000 -> String.format("$%.2fB", number / 1_000_000_000.0)
            number >= 1_000_000 -> String.format("$%.2fM", number / 1_000_000.0)
            number >= 1_000 -> String.format("$%.2fK", number / 1_000.0)
            else -> String.format("$%d", number)
        }
    }
    
    /**
     * Format timestamp to readable date
     */
    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.US)
        return sdf.format(Date(timestamp))
    }
    
    /**
     * Format timestamp to time
     */
    fun formatTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.US)
        return sdf.format(Date(timestamp))
    }
    
    /**
     * Get relative time string (e.g., "2 hours ago")
     */
    fun getRelativeTime(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        
        return when {
            diff < 60_000 -> "Just now"
            diff < 3_600_000 -> "${diff / 60_000}m ago"
            diff < 86_400_000 -> "${diff / 3_600_000}h ago"
            diff < 604_800_000 -> "${diff / 86_400_000}d ago"
            else -> formatDate(timestamp)
        }
    }
}
