package com.example.groww.domain.repository

import com.example.groww.domain.model.Fund
import com.example.groww.domain.model.FundCategory
import com.example.groww.domain.model.FundDetails
import com.example.groww.domain.model.Watchlist
import kotlinx.coroutines.flow.Flow

interface FundRepository {
    // Explore & Funds
    suspend fun fetchExploreData(): Map<FundCategory, List<Fund>>
    fun getFundsByCategory(category: FundCategory): Flow<List<Fund>>
    suspend fun syncCategoryFunds(category: FundCategory)
    suspend fun getFundDetails(id: Int, forceRefresh: Boolean = false): FundDetails
    suspend fun searchFunds(query: String): List<Fund>
    suspend fun cleanupExpiredData()
    suspend fun lazyFetchNav(id: Int)

    // Watchlist
    fun getAllWatchlists(): Flow<List<Watchlist>>
    fun getWatchlistWithFunds(id: Long): Flow<Watchlist>
    suspend fun createWatchlist(name: String): Long
    suspend fun deleteWatchlist(id: Long)
    suspend fun addFundToWatchlist(fundId: Int, watchlistId: Long)
    suspend fun removeFundFromWatchlist(fundId: Int, watchlistId: Long)
    fun isFundInAnyWatchlist(fundId: Int): Flow<Boolean>
    fun getWatchlistsForFund(fundId: Int): Flow<List<Long>>
}
