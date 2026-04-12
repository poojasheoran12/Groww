package com.example.groww.domain.usecase

import com.example.groww.domain.model.Fund
import com.example.groww.domain.model.FundCategory
import com.example.groww.domain.repository.FundRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFundsByCategoryUseCase @Inject constructor(
    private val repository: FundRepository
) {
    operator fun invoke(category: FundCategory): Flow<List<Fund>> = 
        repository.getFundsByCategory(category)
}
