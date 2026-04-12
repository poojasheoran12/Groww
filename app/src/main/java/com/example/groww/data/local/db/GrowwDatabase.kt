package com.example.groww.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.groww.data.local.entity.FundEntity
import com.example.groww.data.local.entity.WatchlistEntity
import com.example.groww.data.local.entity.WatchlistFundCrossRef

@Database(
    entities = [
        FundEntity::class,
        WatchlistEntity::class,
        WatchlistFundCrossRef::class
    ],
    version = 2,
    exportSchema = false
)
abstract class GrowwDatabase : RoomDatabase() {
    abstract fun fundDao(): FundDao
    abstract fun watchlistDao(): WatchlistDao
}
