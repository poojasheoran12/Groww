package com.example.groww.presentation.watchlist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groww.domain.model.Watchlist
import com.example.groww.domain.usecase.GetWatchlistWithFundsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class WatchlistDetailViewModel @Inject constructor(
    getWatchlistWithFundsUseCase: GetWatchlistWithFundsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val watchlistId: Long = savedStateHandle.get<Long>("id") ?: -1L

    val watchlist: StateFlow<Watchlist?> = getWatchlistWithFundsUseCase(watchlistId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}
