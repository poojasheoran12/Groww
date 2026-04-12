package com.example.groww.domain.usecase

import com.example.groww.domain.repository.WatchlistRepository
import javax.inject.Inject

class DeleteWatchlistUseCase @Inject constructor(
    private val repository: WatchlistRepository
) {
    suspend operator fun invoke(id: Long) = repository.deleteWatchlist(id)
}
