package com.example.groww.domain.usecase

import com.example.groww.domain.model.Watchlist
import com.example.groww.domain.repository.WatchlistRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWatchlistWithFundsUseCase @Inject constructor(
    private val repository: WatchlistRepository
) {
    operator fun invoke(id: Long): Flow<Watchlist> = repository.getWatchlistWithFunds(id)
}
