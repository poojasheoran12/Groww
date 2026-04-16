package com.example.groww.domain.usecase

import com.example.groww.domain.model.FundCategory
import com.example.groww.domain.repository.FundRepository
import javax.inject.Inject

class SyncCategoryUseCase @Inject constructor(
    private val repository: FundRepository
) {
    suspend operator fun invoke(category: FundCategory): Result<Unit> {
        return repository.syncCategoryFunds(category)
    }
}
