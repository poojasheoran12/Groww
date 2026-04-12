package com.example.groww.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.groww.data.local.entity.FundEntity

@Database(entities = [FundEntity::class], version = 1, exportSchema = false)
abstract class GrowwDatabase : RoomDatabase() {
    abstract fun fundDao(): FundDao
}
