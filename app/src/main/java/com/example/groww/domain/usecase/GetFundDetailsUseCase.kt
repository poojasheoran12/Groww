package com.example.groww.domain.usecase

import com.example.groww.domain.model.FundDetails
import com.example.groww.domain.repository.FundRepository
import javax.inject.Inject

class GetFundDetailsUseCase @Inject constructor(
    private val repository: FundRepository
) {
    suspend operator fun invoke(id: Int, forceRefresh: Boolean = false): Result<FundDetails> {
        return repository.getFundDetails(id, forceRefresh)
    }
}
