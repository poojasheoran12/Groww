package com.example.groww.presentation.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groww.domain.usecase.*
import com.example.groww.presentation.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val getFundDetailsUseCase: GetFundDetailsUseCase,
    private val isFundInWatchlistUseCase: IsFundInWatchlistUseCase,
    private val addFundToWatchlistUseCase: AddFundToWatchlistUseCase,
    private val getAllWatchlistsUseCase: GetAllWatchlistsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val fundId: Int = checkNotNull(savedStateHandle[Screen.ARG_FUND_ID])

    private val _uiState = MutableStateFlow(DetailsUiState())
    val uiState: StateFlow<DetailsUiState> = _uiState.asStateFlow()

    init {
        loadDetails()
        observeWatchlistStatus()
        observeAvailableWatchlists()
    }

    private fun loadDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching {
                getFundDetailsUseCase(fundId)
            }.onSuccess { details ->
                _uiState.update { it.copy(details = details, isLoading = false) }
            }.onFailure { error ->
                _uiState.update { it.copy(error = error.message ?: "Failed to load details", isLoading = false) }
            }
        }
    }

    private fun observeWatchlistStatus() {
        isFundInWatchlistUseCase(fundId)
            .onEach { isBookmarked ->
                _uiState.update { it.copy(isBookmarked = isBookmarked) }
            }
            .launchIn(viewModelScope)
    }

    private fun observeAvailableWatchlists() {
        getAllWatchlistsUseCase()
            .onEach { watchlists ->
                _uiState.update { it.copy(availableWatchlists = watchlists) }
            }
            .launchIn(viewModelScope)
    }

    fun onBookmarkClick() {
        _uiState.update { it.copy(showWatchlistDialog = true) }
    }

    fun onDismissDialog() {
        _uiState.update { it.copy(showWatchlistDialog = false) }
    }

    fun addToWatchlist(watchlistId: Long) {
        viewModelScope.launch {
            addFundToWatchlistUseCase(fundId, watchlistId)
            onDismissDialog()
        }
    }

    fun retry() {
        loadDetails()
    }
}
