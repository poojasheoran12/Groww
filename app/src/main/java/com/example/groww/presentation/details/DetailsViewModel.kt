package com.example.groww.presentation.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groww.domain.model.FundDetails
import com.example.groww.domain.model.Watchlist
import com.example.groww.domain.repository.FundRepository
import com.example.groww.domain.usecase.GetFundDetailsUseCase
import com.example.groww.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val getFundDetailsUseCase: GetFundDetailsUseCase,
    private val repository: FundRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val fundId: Int = savedStateHandle.get<Int>("id") ?: -1

    private val _detailsState = MutableStateFlow<UiState<FundDetails>>(UiState.Loading)
    val detailsState = _detailsState.asStateFlow()

    val isInCategoryWatchlist = repository.isFundInAnyWatchlist(fundId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val allWatchlists = repository.getAllWatchlists()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        
    val activeWatchlistIds = repository.getWatchlistsForFund(fundId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        if (fundId != -1) {
            loadDetails(fundId)
        }
    }

    private fun loadDetails(id: Int) {
        viewModelScope.launch {
            _detailsState.value = UiState.Loading
            try {
                val details = getFundDetailsUseCase(id)
                _detailsState.value = UiState.Success(details)
            } catch (e: Exception) {
                _detailsState.value = UiState.Error(e.message ?: "Failed to load details")
            }
        }
    }

    fun toggleWatchlist(watchlistId: Long, isSelected: Boolean) {
        viewModelScope.launch {
            if (isSelected) {
                repository.addFundToWatchlist(fundId, watchlistId)
            } else {
                repository.removeFundFromWatchlist(fundId, watchlistId)
            }
        }
    }

    fun createAndAddToWatchlist(name: String) {
        viewModelScope.launch {
            val id = repository.createWatchlist(name)
            repository.addFundToWatchlist(fundId, id)
        }
    }
}
