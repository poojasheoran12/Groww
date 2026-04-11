package com.example.groww.domain.repository

import com.example.groww.domain.model.Fund
import com.example.groww.domain.model.FundDetail

interface FundRepository {
    suspend fun searchFunds(query: String): Result<List<Fund>>
    suspend fun getFundDetails(schemeCode: Int): Result<FundDetail>
}
