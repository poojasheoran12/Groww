package com.example.groww.data.local.dao

import androidx.room.*
import com.example.groww.data.local.entity.FundEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FundDao {
    @Query("SELECT * FROM funds WHERE category = :category")
    fun getFundsByCategory(category: String): Flow<List<FundEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFunds(funds: List<FundEntity>)

    @Query("UPDATE funds SET latestNav = :nav WHERE schemeCode = :schemeCode")
    suspend fun updateNav(schemeCode: Int, nav: String)

    @Query("DELETE FROM funds WHERE category = :category")
    suspend fun deleteFundsByCategory(category: String)
}
