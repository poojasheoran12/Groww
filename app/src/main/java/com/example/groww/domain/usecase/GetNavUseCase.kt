package com.example.groww.domain.usecase

import com.example.groww.domain.repository.FundRepository
import javax.inject.Inject

class GetNavUseCase @Inject constructor(
    private val repository: FundRepository
) {
    suspend operator fun invoke(schemeCode: Int): Result<String?> {
        return repository.getFundNav(schemeCode)
    }
}
