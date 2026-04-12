package com.example.groww.domain.usecase

import com.example.groww.domain.repository.WatchlistRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IsFundInWatchlistUseCase @Inject constructor(
    private val repository: WatchlistRepository
) {
    operator fun invoke(fundId: Int): Flow<Boolean> = repository.isFundInAnyWatchlist(fundId)
}
