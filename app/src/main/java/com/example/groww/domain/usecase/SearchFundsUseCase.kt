package com.example.groww.domain.usecase

import com.example.groww.domain.repository.FundRepository
import javax.inject.Inject

class SearchFundsUseCase @Inject constructor(
    private val repository: FundRepository
) {
    suspend operator fun invoke(query: String) = repository.searchFunds(query)
}
