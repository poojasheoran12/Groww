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
    private val getAllWatchlistsUseCase: GetAllWatchlistsUseCase,
    private val createWatchlistUseCase: CreateWatchlistUseCase,
    private val deleteWatchlistUseCase: DeleteWatchlistUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(WatchlistUiState())
    val uiState: StateFlow<WatchlistUiState> = _uiState.asStateFlow()

    init {
        observeWatchlists()
    }

    private fun observeWatchlists() {
        getAllWatchlistsUseCase()
            .onEach { list ->
                _uiState.update { it.copy(watchlists = list, isLoading = false) }
            }
            .launchIn(viewModelScope)
    }

    fun onAddClick() {
        _uiState.update { it.copy(showAddDialog = true) }
    }

    fun onDismissDialog() {
        _uiState.update { it.copy(showAddDialog = false, newWatchlistName = "") }
    }

    fun onNameChange(name: String) {
        _uiState.update { it.copy(newWatchlistName = name) }
    }

    fun createWatchlist() {
        val name = _uiState.value.newWatchlistName
        if (name.isBlank()) return

        viewModelScope.launch {
            try {
                createWatchlistUseCase(name)
                onDismissDialog()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Failed to create portfolio") }
            }
        }
    }

    fun deleteWatchlist(id: Long) {
        viewModelScope.launch {
            deleteWatchlistUseCase(id)
        }
    }
}
