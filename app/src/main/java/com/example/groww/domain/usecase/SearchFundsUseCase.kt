package com.example.groww.domain.usecase

import com.example.groww.domain.model.Fund
import com.example.groww.domain.repository.FundRepository
import javax.inject.Inject

class SearchFundsUseCase @Inject constructor(
    private val repository: FundRepository
) {
    suspend operator fun invoke(query: String): Result<List<Fund>> {
        return repository.searchFunds(query)
    }
}
