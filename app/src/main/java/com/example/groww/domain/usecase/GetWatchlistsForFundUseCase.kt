package com.example.groww.domain.usecase

import com.example.groww.domain.repository.WatchlistRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWatchlistsForFundUseCase @Inject constructor(
    private val repository: WatchlistRepository
) {
    operator fun invoke(fundId: Int): Flow<List<Long>> = repository.getWatchlistsForFund(fundId)
}
