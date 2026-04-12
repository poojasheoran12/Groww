package com.example.groww.presentation.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groww.domain.model.Watchlist
import com.example.groww.domain.repository.FundRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WatchlistViewModel @Inject constructor(
    private val repository: FundRepository
) : ViewModel() {

    val watchlists = repository.getAllWatchlists()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun createWatchlist(name: String) {
        viewModelScope.launch {
            repository.createWatchlist(name)
        }
    }

    fun deleteWatchlist(id: Long) {
        viewModelScope.launch {
            repository.deleteWatchlist(id)
        }
    }
}
