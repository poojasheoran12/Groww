package com.example.groww.presentation.details

import com.example.groww.domain.model.FundDetails

data class DetailsUiState(
    val details: FundDetails? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val availableWatchlists: List<com.example.groww.domain.model.Watchlist> = emptyList(),
    val fundWatchlistIds: List<Long> = emptyList(),
    val showWatchlistDialog: Boolean = false
)
