package com.example.groww.domain.usecase

import com.example.groww.domain.model.Fund
import com.example.groww.domain.repository.FundRepository
import javax.inject.Inject

class GetCategoryFundsUseCase @Inject constructor(
    private val repository: FundRepository
) {
    suspend operator fun invoke(category: String): Result<List<Fund>> {
        val query = when (category) {
            "Index Funds" -> "index"
            "Bluechip Funds" -> "bluechip"
            "Tax Saver (ELSS)" -> "tax"
            "Large Cap Funds" -> "large"
            else -> category
        }
        return repository.searchFunds(query)
    }
}
