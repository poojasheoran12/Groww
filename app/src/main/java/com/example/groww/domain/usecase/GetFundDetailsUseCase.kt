package com.example.groww.domain.usecase

import com.example.groww.domain.model.FundDetail
import com.example.groww.domain.repository.FundRepository
import javax.inject.Inject

class GetFundDetailsUseCase @Inject constructor(
    private val repository: FundRepository
) {
    suspend operator fun invoke(schemeCode: Int): Result<FundDetail> {
        return repository.getFundDetails(schemeCode)
    }
}
