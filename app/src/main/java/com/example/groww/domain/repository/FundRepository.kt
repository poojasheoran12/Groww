package com.example.groww.domain.repository

import com.example.groww.domain.model.Fund
import com.example.groww.domain.model.FundCategory
import com.example.groww.domain.model.FundDetails
import kotlinx.coroutines.flow.Flow

interface FundRepository {
    // For Explore (Pure Network)
    suspend fun fetchExploreData(): Map<FundCategory, List<Fund>>
    
    // For View All (Room as SSOT)
    fun getFundsByCategory(category: FundCategory): Flow<List<Fund>>
    suspend fun syncCategoryFunds(category: FundCategory)
    
    suspend fun getFundDetails(id: Int, forceRefresh: Boolean = false): FundDetails
    suspend fun searchFunds(query: String): List<Fund>
    suspend fun cleanupExpiredData()
    suspend fun lazyFetchNav(id: Int)
}
