package com.example.groww.di

import android.content.Context
import androidx.room.Room
import com.example.groww.data.local.db.FundDao
import com.example.groww.data.local.db.GrowwDatabase
import com.example.groww.data.remote.api.MutualFundApi
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): GrowwDatabase {
        return Room.databaseBuilder(
            context,
            GrowwDatabase::class.java,
            "groww.db"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideFundDao(db: GrowwDatabase): FundDao = db.fundDao()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    @Provides
    @Singleton
    fun provideApi(client: OkHttpClient): MutualFundApi {
        val json = Json { ignoreUnknownKeys = true }
        return Retrofit.Builder()
            .baseUrl(MutualFundApi.BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(MutualFundApi::class.java)
    }
}
