package com.example.groww.di

import com.example.groww.data.repository.FundRepositoryImpl
import com.example.groww.domain.repository.FundRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindFundRepository(
        fundRepositoryImpl: FundRepositoryImpl
    ): FundRepository
}
