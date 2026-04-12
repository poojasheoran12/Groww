package com.example.groww.domain.usecase

import com.example.groww.domain.model.Fund
import com.example.groww.domain.model.FundCategory
import com.example.groww.domain.repository.FundRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetExploreFundsUseCase @Inject constructor(
    private val repository: FundRepository
) {
    operator fun invoke(): Flow<Map<FundCategory, List<Fund>>> {
        return repository.getExploreFundsFlow()
    }

    suspend fun sync() {
        repository.syncExploreFunds()
    }

    suspend fun cleanup() {
        repository.cleanupExpiredData()
    }
}
