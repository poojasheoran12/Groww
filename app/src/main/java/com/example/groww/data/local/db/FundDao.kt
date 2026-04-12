package com.example.groww.data.local.db

import androidx.room.*
import com.example.groww.data.local.entity.FundEntity
import com.example.groww.data.local.entity.WatchlistEntity
import com.example.groww.data.local.entity.WatchlistFundCrossRef
import com.example.groww.data.local.entity.WatchlistWithFunds
import kotlinx.coroutines.flow.Flow

@Dao
interface FundDao {
    @Query("SELECT * FROM funds WHERE category = :category")
    fun getFundsByCategory(category: String): Flow<List<FundEntity>>

    @Query("SELECT * FROM funds WHERE name LIKE :query OR category LIKE :query")
    suspend fun searchFundsInRoom(query: String): List<FundEntity>

    @Query("SELECT * FROM funds WHERE id = :id")
    suspend fun getFundById(id: Int): FundEntity?

    @Upsert
    suspend fun upsertFunds(funds: List<FundEntity>)

    @Query("UPDATE funds SET latestNav = :nav, lastUpdated = :lastUpdated WHERE id = :id")
    suspend fun updateNavAndTimestamp(id: Int, nav: String, lastUpdated: Long)

    @Query("DELETE FROM funds WHERE lastUpdated < :threshold")
    suspend fun deleteOldFunds(threshold: Long)

    // Watchlist Operations
    @Query("SELECT * FROM watchlists")
    fun getAllWatchlists(): Flow<List<WatchlistEntity>>

    @Transaction
    @Query("SELECT * FROM watchlists WHERE id = :id")
    fun getWatchlistWithFunds(id: Long): Flow<WatchlistWithFunds>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatchlist(watchlist: WatchlistEntity): Long

    @Delete
    suspend fun deleteWatchlist(watchlist: WatchlistEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFundToWatchlist(crossRef: WatchlistFundCrossRef)

    @Delete
    suspend fun removeFundFromWatchlist(crossRef: WatchlistFundCrossRef)

    @Query("SELECT EXISTS(SELECT 1 FROM watchlist_fund_cross_ref WHERE fundId = :fundId)")
    fun isFundInAnyWatchlist(fundId: Int): Flow<Boolean>

    @Query("SELECT watchlistId FROM watchlist_fund_cross_ref WHERE fundId = :fundId")
    fun getWatchlistsForFund(fundId: Int): Flow<List<Long>>
}
