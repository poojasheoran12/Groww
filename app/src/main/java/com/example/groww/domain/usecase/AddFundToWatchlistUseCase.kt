package com.example.groww.domain.usecase

import com.example.groww.domain.repository.WatchlistRepository
import javax.inject.Inject

class AddFundToWatchlistUseCase @Inject constructor(
    private val repository: WatchlistRepository
) {
    suspend operator fun invoke(fundId: Int, watchlistId: Long) = 
        repository.addFundToWatchlist(fundId, watchlistId)
}
