package com.example.groww.di

import android.content.Context
import androidx.room.Room
import com.example.groww.data.local.GrowwDatabase
import com.example.groww.data.local.dao.FundDao
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
    fun provideGrowwDatabase(@ApplicationContext context: Context): GrowwDatabase {
        return Room.databaseBuilder(
            context,
            GrowwDatabase::class.java,
            GrowwDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideFundDao(db: GrowwDatabase): FundDao = db.fundDao
}
