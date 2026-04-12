package com.example.groww.presentation.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groww.domain.usecase.CreateWatchlistUseCase
import com.example.groww.domain.usecase.DeleteWatchlistUseCase
import com.example.groww.domain.usecase.GetAllWatchlistsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WatchlistViewModel @Inject constructor(
    getAllWatchlistsUseCase: GetAllWatchlistsUseCase,
    private val createWatchlistUseCase: CreateWatchlistUseCase,
    private val deleteWatchlistUseCase: DeleteWatchlistUseCase
) : ViewModel() {

    val watchlists = getAllWatchlistsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun createWatchlist(name: String) {
        viewModelScope.launch {
            createWatchlistUseCase(name)
        }
    }

    fun deleteWatchlist(id: Long) {
        viewModelScope.launch {
            deleteWatchlistUseCase(id)
        }
    }
}
