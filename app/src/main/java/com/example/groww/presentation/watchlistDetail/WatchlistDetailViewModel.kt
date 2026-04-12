package com.example.groww.presentation.watchlistDetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groww.domain.model.Watchlist
import com.example.groww.domain.usecase.GetWatchlistWithFundsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import com.example.groww.presentation.navigation.Screen
import javax.inject.Inject

@HiltViewModel
class WatchlistDetailViewModel @Inject constructor(
    getWatchlistWithFundsUseCase: GetWatchlistWithFundsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val watchlistId: Long = savedStateHandle[Screen.ARG_WATCHLIST_ID] ?: -1L

    val watchlist: StateFlow<Watchlist?> = getWatchlistWithFundsUseCase(watchlistId)
        .stateIn(viewModelScope, SharingStarted.Companion.WhileSubscribed(5000), null)
}