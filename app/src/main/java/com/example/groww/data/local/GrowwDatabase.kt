package com.example.groww.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.groww.data.local.dao.FundDao
import com.example.groww.data.local.entity.FundEntity

@Database(entities = [FundEntity::class], version = 1, exportSchema = false)
abstract class GrowwDatabase : RoomDatabase() {
    abstract val fundDao: FundDao

    companion object {
        const val DATABASE_NAME = "groww_db"
    }
}
