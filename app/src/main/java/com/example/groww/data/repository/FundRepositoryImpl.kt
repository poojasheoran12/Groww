package com.example.groww.data.repository

import com.example.groww.data.local.db.FundDao
import com.example.groww.data.local.entity.WatchlistEntity
import com.example.groww.data.local.entity.WatchlistFundCrossRef
import com.example.groww.data.mapper.toDomain
import com.example.groww.data.mapper.toEntity
import com.example.groww.data.remote.api.MutualFundApi
import com.example.groww.domain.model.Fund
import com.example.groww.domain.model.FundCategory
import com.example.groww.domain.model.FundDetails
import com.example.groww.domain.model.Watchlist
import com.example.groww.domain.repository.FundRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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

    override suspend fun fetchExploreData(): Map<FundCategory, List<Fund>> = coroutineScope {
        val categories = FundCategory.entries.filter { it != FundCategory.SEARCH }
        
        categories.associateWith { category ->
            async {
                try {
                    val searchResults = api.searchFunds(category.apiQuery).take(4)
                    searchResults.map { searchResult ->
                        async {
                            val detailsDto = api.getFundDetails(searchResult.schemeCode)
                            val domainDetails = detailsDto.toDomain()
                            
                            synchronized(memoryCache) {
                                memoryCache[searchResult.schemeCode] = domainDetails
                            }

                            searchResult.toDomain(category.displayName).copy(
                                latestNav = domainDetails.latestNav
                            )
                        }
                    }.awaitAll()
                } catch (e: Exception) {
                    emptyList()
                }
            }
        }.mapValues { it.value.await() }
    }

    override fun getFundsByCategory(category: FundCategory): Flow<List<Fund>> {
        return dao.getFundsByCategory(category.displayName).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun syncCategoryFunds(category: FundCategory) {
        try {
            val networkResults = api.searchFunds(category.apiQuery)
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
        } catch (e: Exception) { }
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

    override suspend fun searchFunds(query: String): List<Fund> {
        return try {
            api.searchFunds(query).map { it.toDomain(FundCategory.SEARCH.displayName) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun cleanupExpiredData() {
        val threshold = System.currentTimeMillis() - CACHE_EXPIRY_MS
        dao.deleteOldFunds(threshold)
    }

    // Watchlist Implementation
    override fun getAllWatchlists(): Flow<List<Watchlist>> {
        return dao.getAllWatchlists().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getWatchlistWithFunds(id: Long): Flow<Watchlist> {
        return dao.getWatchlistWithFunds(id).map { it.toDomain() }
    }

    override suspend fun createWatchlist(name: String): Long {
        return dao.insertWatchlist(WatchlistEntity(name = name))
    }

    override suspend fun deleteWatchlist(id: Long) {
        dao.deleteWatchlist(WatchlistEntity(id = id, name = ""))
    }

    override suspend fun addFundToWatchlist(fundId: Int, watchlistId: Long) {
        dao.insertFundToWatchlist(WatchlistFundCrossRef(watchlistId, fundId))
    }

    override suspend fun removeFundFromWatchlist(fundId: Int, watchlistId: Long) {
        dao.removeFundFromWatchlist(WatchlistFundCrossRef(watchlistId, fundId))
    }

    override fun isFundInAnyWatchlist(fundId: Int): Flow<Boolean> {
        return dao.isFundInAnyWatchlist(fundId)
    }

    override fun getWatchlistsForFund(fundId: Int): Flow<List<Long>> {
        return dao.getWatchlistsForFund(fundId)
    }

    private fun isExpired(lastUpdated: Long): Boolean {
        return System.currentTimeMillis() - lastUpdated > CACHE_EXPIRY_MS
    }
}
