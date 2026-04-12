package com.example.groww.data.local.db

import androidx.room.*
import com.example.groww.data.local.entity.FundEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FundDao {
    @Query("SELECT * FROM funds WHERE category = :category")
    fun getFundsByCategory(category: String): Flow<List<FundEntity>>

    @Query("SELECT * FROM funds WHERE id IN (:ids)")
    fun getFundsByIdsFlow(ids: List<Int>): Flow<List<FundEntity>>

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
}
