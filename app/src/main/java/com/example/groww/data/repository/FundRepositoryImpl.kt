package com.example.groww.data.repository

import com.example.groww.data.mapper.toDomain
import com.example.groww.data.remote.api.ApiService
import com.example.groww.data.remote.dto.toDomain
import com.example.groww.domain.model.Fund
import com.example.groww.domain.model.FundDetail
import com.example.groww.domain.repository.FundRepository
import javax.inject.Inject

class FundRepositoryImpl @Inject constructor(
    private val api: ApiService
) : FundRepository {

    override suspend fun searchFunds(query: String): Result<List<Fund>> {
        return try {
            val response = api.searchFunds(query)
            Result.success(response.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

//    override suspend fun getFundDetails(schemeCode: Int): Result<FundDetail> {
//        return try {
//            val response = api.getFundDetails(schemeCode)
//            // Use the mapper to convert FundDetailDto to FundDetail
//            Result.success(response.toDomain())
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }
}
