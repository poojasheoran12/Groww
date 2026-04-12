package com.example.groww.data.repository

import com.example.groww.data.local.db.FundDao
import com.example.groww.data.mapper.toDomain
import com.example.groww.data.mapper.toEntity
import com.example.groww.data.remote.api.MutualFundApi
import com.example.groww.domain.model.Fund
import com.example.groww.domain.model.FundCategory
import com.example.groww.domain.model.FundDetails
import com.example.groww.domain.repository.FundRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FundRepositoryImpl @Inject constructor(
    private val api: MutualFundApi,
    private val dao: FundDao
) : FundRepository {

    private companion object {
        const val CACHE_EXPIRY_MS = 24 * 60 * 60 * 1000L
        const val MAX_MEMORY_CACHE_SIZE = 50
    }

    private val memoryCache = object : LinkedHashMap<Int, FundDetails>(MAX_MEMORY_CACHE_SIZE, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<Int, FundDetails>?): Boolean {
            return size > MAX_MEMORY_CACHE_SIZE
        }
    }

    override fun getExploreFundsFlow(): Flow<Map<FundCategory, List<Fund>>> {
        val categories = FundCategory.entries.filter { it != FundCategory.SEARCH && it != FundCategory.ALL }
        
        val categoryFlows = categories.map { category ->
            getFundsByCategory(category).map { funds -> category to funds.take(4) }
        }

        return combine(categoryFlows) { categoryPairs ->
            categoryPairs.toMap()
        }
    }

    override suspend fun syncExploreFunds(): Unit = coroutineScope {
        val categories = FundCategory.entries.filter { it != FundCategory.SEARCH && it != FundCategory.ALL }
        
        categories.map { category ->
            async { syncCategoryFunds(category) }
        }.awaitAll()
    }

    override fun getFundsByCategory(category: FundCategory): Flow<List<Fund>> {
        return dao.getFundsByCategory(category.displayName).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun syncCategoryFunds(category: FundCategory) {
        val networkResults = try {
            api.searchFunds(getApiQuery(category))
        } catch (e: Exception) {
            emptyList()
        }

        if (networkResults.isNotEmpty()) {
            val entities = networkResults.map { searchResult ->
                val existingInRoom = dao.getFundById(searchResult.schemeCode)
                val existingInCache = synchronized(memoryCache) { memoryCache[searchResult.schemeCode] }
                
                searchResult.toEntity(category.displayName).copy(
                    latestNav = existingInCache?.latestNav ?: existingInRoom?.latestNav,
                    lastUpdated = if (existingInCache != null || existingInRoom != null) {
                        System.currentTimeMillis()
                    } else {
                        System.currentTimeMillis()
                    }
                )
            }
            dao.upsertFunds(entities)

            // Specifically for the top 4 in this category, fetch NAV if missing in parallel
            coroutineScope {
                networkResults.take(4).map { fund ->
                    async { lazyFetchNav(fund.schemeCode) }
                }.awaitAll()
            }
        }
    }

    override suspend fun lazyFetchNav(id: Int) {
        val existing = dao.getFundById(id)
        if (existing?.latestNav != null && !isExpired(existing.lastUpdated)) return

        val memoryNav = synchronized(memoryCache) { memoryCache[id]?.latestNav }
        if (memoryNav != null) {
            dao.updateNavAndTimestamp(id, memoryNav, System.currentTimeMillis())
            return
        }

        try {
            val response = api.getFundDetails(id)
            val latestNav = response.data.firstOrNull()?.nav
            if (latestNav != null) {
                dao.updateNavAndTimestamp(id, latestNav, System.currentTimeMillis())
            }
        } catch (e: Exception) { }
    }

    override suspend fun getFundDetails(id: Int, forceRefresh: Boolean): FundDetails {
        if (!forceRefresh) {
            synchronized(memoryCache) {
                memoryCache[id]?.let { return it }
            }
        }

        val detailsDto = api.getFundDetails(id)
        val domainDetails = detailsDto.toDomain()

        synchronized(memoryCache) {
            memoryCache[id] = domainDetails
        }

        dao.upsertFunds(listOf(domainDetails.toEntity()))

        return domainDetails
    }

    override fun searchFunds(query: String): Flow<List<Fund>> = flow {
        // 1. Instant Local Search
        val localEntities = dao.searchFundsInRoom("%$query%")
        val localFunds = localEntities.map { it.toDomain() }
        emit(localFunds)

        // 2. Network Enhancement
        try {
            val networkResults = api.searchFunds(query)
            val domainFunds = networkResults.map { searchResult ->
                // Hydrate from Room if possible
                val existing = dao.getFundById(searchResult.schemeCode)
                val domainFund = searchResult.toDomain(FundCategory.SEARCH.displayName)
                
                if (existing?.latestNav != null) {
                    domainFund.copy(latestNav = existing.latestNav)
                } else {
                    domainFund
                }
            }
            
            if (domainFunds.isNotEmpty()) {
                emit(domainFunds)
            }
        } catch (e: Exception) {
            // Keep existing local results if network fails
        }
    }

    override suspend fun cleanupExpiredData() {
        val threshold = System.currentTimeMillis() - CACHE_EXPIRY_MS
        dao.deleteOldFunds(threshold)
    }

    private fun getApiQuery(category: FundCategory): String {
        return when (category) {
            FundCategory.ALL -> "growth"
            FundCategory.INDEX -> "index"
            FundCategory.BLUECHIP -> "bluechip"
            FundCategory.TAX -> "tax"
            FundCategory.LARGE_CAP -> "large"
            else -> ""
        }
    }

    private fun isExpired(lastUpdated: Long): Boolean {
        return System.currentTimeMillis() - lastUpdated > CACHE_EXPIRY_MS
    }
}
