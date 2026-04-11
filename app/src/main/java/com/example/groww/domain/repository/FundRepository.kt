package com.example.groww.domain.repository

import com.example.groww.domain.model.Fund


interface FundRepository {
    suspend fun searchFunds(query: String): Result<List<Fund>>
}
