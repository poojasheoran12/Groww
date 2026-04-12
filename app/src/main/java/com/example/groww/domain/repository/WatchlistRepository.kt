package com.example.groww.domain.repository

import com.example.groww.domain.model.Watchlist
import kotlinx.coroutines.flow.Flow

interface WatchlistRepository {
    fun getAllWatchlists(): Flow<List<Watchlist>>
    fun getWatchlistWithFunds(id: Long): Flow<Watchlist>
    suspend fun createWatchlist(name: String): Long
    suspend fun deleteWatchlist(id: Long)
    suspend fun addFundToWatchlist(fundId: Int, watchlistId: Long)
    suspend fun removeFundFromWatchlist(fundId: Int, watchlistId: Long)
    fun isFundInAnyWatchlist(fundId: Int): Flow<Boolean>
    fun getWatchlistsForFund(fundId: Int): Flow<List<Long>>
}
