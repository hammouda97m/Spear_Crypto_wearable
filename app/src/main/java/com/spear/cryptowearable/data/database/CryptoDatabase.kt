package com.spear.cryptowearable.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.spear.cryptowearable.data.model.PortfolioItem
import com.spear.cryptowearable.data.model.PriceAlert
import com.spear.cryptowearable.data.model.WatchlistItem

/**
 * Room database for local data storage
 */
@Database(
    entities = [
        WatchlistItem::class,
        PortfolioItem::class,
        PriceAlert::class
    ],
    version = 1,
    exportSchema = false
)
abstract class CryptoDatabase : RoomDatabase() {
    
    abstract fun watchlistDao(): WatchlistDao
    abstract fun portfolioDao(): PortfolioDao
    abstract fun priceAlertDao(): PriceAlertDao
    
    companion object {
        @Volatile
        private var INSTANCE: CryptoDatabase? = null
        
        fun getDatabase(context: Context): CryptoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CryptoDatabase::class.java,
                    "crypto_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
