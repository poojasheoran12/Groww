package com.example.groww.presentation.watchlist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groww.domain.model.Watchlist
import com.example.groww.domain.repository.FundRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class WatchlistDetailViewModel @Inject constructor(
    private val repository: FundRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val watchlistId: Long = savedStateHandle.get<Long>("id") ?: -1L

    val watchlist: StateFlow<Watchlist?> = repository.getWatchlistWithFunds(watchlistId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}
