package com.example.groww.domain.usecase

import com.example.groww.domain.model.Watchlist
import com.example.groww.domain.repository.WatchlistRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllWatchlistsUseCase @Inject constructor(
    private val repository: WatchlistRepository
) {
    operator fun invoke(): Flow<List<Watchlist>> = repository.getAllWatchlists()
}
