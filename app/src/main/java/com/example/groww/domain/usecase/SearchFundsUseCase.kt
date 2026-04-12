package com.example.groww.domain.usecase

import com.example.groww.domain.model.Fund
import com.example.groww.domain.repository.FundRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchFundsUseCase @Inject constructor(
    private val repository: FundRepository
) {
    operator fun invoke(query: String): Flow<List<Fund>> = repository.searchFunds(query)
}
