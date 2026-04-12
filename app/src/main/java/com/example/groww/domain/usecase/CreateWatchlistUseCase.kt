package com.example.groww.domain.usecase

import com.example.groww.domain.repository.WatchlistRepository
import javax.inject.Inject

class CreateWatchlistUseCase @Inject constructor(
    private val repository: WatchlistRepository
) {
    suspend operator fun invoke(name: String): Long = repository.createWatchlist(name)
}
