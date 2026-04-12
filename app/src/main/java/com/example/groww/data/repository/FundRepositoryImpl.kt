package com.example.groww.data.repository

import com.example.groww.data.local.dao.FundDao
import com.example.groww.data.local.entity.toDomain
import com.example.groww.data.local.entity.toEntity
import com.example.groww.data.remote.api.ApiService
import com.example.groww.data.mapper.toDomain
import com.example.groww.domain.model.Fund
import com.example.groww.domain.model.FundDetail
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
    private val api: ApiService,
    private val dao: FundDao
) : FundRepository {


    private val navMemoryCache = mutableMapOf<Int, String>()

    private val CACHE_EXPIRATION_MS = 24 * 60 * 60 * 1000L // 24 Hours

    override fun getFundsByCategory(category: String): Flow<List<Fund>> {
        return dao.getFundsByCategory(category).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun refreshExploreFunds(): Result<Unit> = coroutineScope {
        try {
            val categories = listOf("Index", "Bluechip", "Tax", "Large")
            

            val deferredList = categories.map { category ->
                async {
                    val searchResults = api.searchFunds(category)
                    category to searchResults.take(4)
                }
            }
            
            val results = deferredList.awaitAll()
            

            val allItems = results.flatMap { it.second }

            val detailsFlow = allItems.map { fundDto ->
                async {
                    try {
                        val details = api.getFundDetails(fundDto.schemeCode)
                        val latestNav = details.data.firstOrNull()?.nav
                        fundDto to latestNav
                    } catch (e: Exception) {
                        fundDto to null
                    }
                }
            }
            
            val fundWithNavList = detailsFlow.awaitAll()
            

            results.forEach { (category, top4) ->
                val entities = top4.map { dto ->
                    val nav = fundWithNavList.find { it.first.schemeCode == dto.schemeCode }?.second
                    Fund(
                        schemeCode = dto.schemeCode,
                        schemeName = dto.schemeName,
                        category = category,
                        latestNav = nav
                    ).toEntity()
                }
                dao.deleteByCategory(category)
                dao.insertFunds(entities)
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun checkAndRefreshNav(schemeCode: Int) {

        if (navMemoryCache.containsKey(schemeCode)) return


        val localFund = dao.getFundByCode(schemeCode)
        if (localFund?.latestNav != null && !isCacheExpired(localFund.lastUpdated)) {
            navMemoryCache[schemeCode] = localFund.latestNav
            return
        }


        try {
            val response = api.getFundDetails(schemeCode)
            val latestNav = response.data.firstOrNull()?.nav
            
            if (latestNav != null) {
                val updatedEntity = localFund?.copy(
                    latestNav = latestNav,
                    lastUpdated = System.currentTimeMillis()
                ) ?: Fund(
                    schemeCode = schemeCode,
                    schemeName = response.meta.scheme_name,
                    latestNav = latestNav
                ).toEntity()
                
                dao.insertFund(updatedEntity)
                navMemoryCache[schemeCode] = latestNav
            }
        } catch (e: Exception) {

        }
    }

    override suspend fun getFundDetails(schemeCode: Int): Result<FundDetail> {
        return try {
            val response = api.getFundDetails(schemeCode)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchFunds(query: String): Result<List<Fund>> {
        return try {
            val response = api.searchFunds(query)
            Result.success(response.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun cleanupOldData() {
        val threshold = System.currentTimeMillis() - CACHE_EXPIRATION_MS
        dao.deleteOldFunds(threshold)
    }

    private fun isCacheExpired(lastUpdated: Long): Boolean {
        return (System.currentTimeMillis() - lastUpdated) > CACHE_EXPIRATION_MS
    }
}
