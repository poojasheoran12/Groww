package com.example.groww.data.local.dao

import androidx.room.*
import com.example.groww.data.local.entity.FundEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FundDao {
    @Query("SELECT * FROM funds WHERE category = :category")
    fun getFundsByCategory(category: String): Flow<List<FundEntity>>

    @Query("SELECT * FROM funds WHERE schemeCode = :schemeCode")
    suspend fun getFundByCode(schemeCode: Int): FundEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFunds(funds: List<FundEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFund(fund: FundEntity)

    @Query("DELETE FROM funds WHERE category = :category")
    suspend fun deleteByCategory(category: String)

    @Query("DELETE FROM funds WHERE lastUpdated < :threshold")
    suspend fun deleteOldFunds(threshold: Long)

    @Query("DELETE FROM funds")
    suspend fun clearAll()
}
