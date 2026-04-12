package com.example.groww.domain.usecase

import com.example.groww.domain.model.Fund
import com.example.groww.domain.model.FundCategory
import com.example.groww.domain.repository.FundRepository
import javax.inject.Inject

class GetExploreFundsUseCase @Inject constructor(
    private val repository: FundRepository
) {
    suspend operator fun invoke(): Map<FundCategory, List<Fund>> {
        return repository.fetchExploreData()
    }

    suspend fun cleanup() {
        repository.cleanupExpiredData()
    }
}
