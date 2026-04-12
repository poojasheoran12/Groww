package com.example.groww.data.local.db

import androidx.room.*
import com.example.groww.data.local.entity.WatchlistEntity
import com.example.groww.data.local.entity.WatchlistFundCrossRef
import com.example.groww.data.local.entity.WatchlistWithFunds
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchlistDao {
    @Query("SELECT * FROM watchlists")
    fun getAllWatchlists(): Flow<List<WatchlistEntity>>

    @Transaction
    @Query("SELECT * FROM watchlists")
    fun getWatchlistsWithFunds(): Flow<List<WatchlistWithFunds>>

    @Transaction
    @Query("SELECT * FROM watchlists WHERE id = :id")
    fun getWatchlistWithFunds(id: Long): Flow<WatchlistWithFunds>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatchlist(watchlist: WatchlistEntity): Long

    @Query("DELETE FROM watchlists WHERE id = :id")
    suspend fun deleteWatchlistById(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFundToWatchlist(crossRef: WatchlistFundCrossRef)

    @Delete
    suspend fun removeFundFromWatchlist(crossRef: WatchlistFundCrossRef)

    @Query("SELECT EXISTS(SELECT 1 FROM watchlist_fund_cross_ref WHERE fundId = :fundId)")
    fun isFundInAnyWatchlist(fundId: Int): Flow<Boolean>

    @Query("SELECT watchlistId FROM watchlist_fund_cross_ref WHERE fundId = :fundId")
    fun getWatchlistsForFund(fundId: Int): Flow<List<Long>>

    @Query("SELECT EXISTS(SELECT 1 FROM watchlist_fund_cross_ref WHERE watchlistId = :watchlistId AND fundId = :fundId)")
    suspend fun isFundAlreadyInWatchlist(watchlistId: Long, fundId: Int): Boolean
}
