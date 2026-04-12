package com.example.groww.domain.repository

import com.example.groww.domain.model.Fund
import com.example.groww.domain.model.FundDetail
import kotlinx.coroutines.flow.Flow

interface FundRepository {
    fun getFundsByCategory(category: String): Flow<List<Fund>>
    suspend fun refreshExploreFunds(): Result<Unit>
    suspend fun checkAndRefreshNav(schemeCode: Int)
    suspend fun getFundDetails(schemeCode: Int): Result<FundDetail>
    suspend fun searchFunds(query: String): Result<List<Fund>>
    suspend fun cleanupOldData()
}
