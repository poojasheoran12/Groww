package com.example.groww.presentation.watchlist

import com.example.groww.domain.model.Watchlist

data class WatchlistUiState(
    val watchlists: List<Watchlist> = emptyList(),
    val showAddDialog: Boolean = false,
    val newWatchlistName: String = "",
    val isLoading: Boolean = true,
    val error: String? = null
)
