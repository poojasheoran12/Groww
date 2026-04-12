package com.example.groww.domain.usecase

import com.example.groww.domain.repository.FundRepository
import javax.inject.Inject

class RefreshExploreFundsUseCase @Inject constructor(
    private val repository: FundRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return repository.refreshExploreFunds()
    }
}
