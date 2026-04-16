package com.example.groww.di

import android.content.Context
import androidx.room.Room
import com.example.groww.data.local.db.FundDao
import com.example.groww.data.local.db.GrowwDatabase
import com.example.groww.data.local.db.WatchlistDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): GrowwDatabase {
        return Room.databaseBuilder(
            context,
            GrowwDatabase::class.java,
            "groww.db"
        ).addMigrations()
            .build()
    }

    @Provides
    fun provideFundDao(db: GrowwDatabase): FundDao = db.fundDao()

    @Provides
    fun provideWatchlistDao(db: GrowwDatabase): WatchlistDao = db.watchlistDao()
}
