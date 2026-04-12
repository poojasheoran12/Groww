package com.example.groww.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groww.domain.model.Fund
import com.example.groww.domain.usecase.SearchFundsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SearchUiState {
    object Idle : SearchUiState()
    object Loading : SearchUiState()
    data class Success(val results: List<Fund>) : SearchUiState()
    data class Error(val message: String) : SearchUiState()
}

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchFundsUseCase: SearchFundsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    private var searchJob: Job? = null

    fun onSearchUpdate(query: String) {
        _query.value = query
        searchJob?.cancel()
        
        if (query.isEmpty()) {
            _uiState.value = SearchUiState.Idle
            return
        }

        searchJob = viewModelScope.launch {
            delay(500) // Debounce 500ms
            _uiState.value = SearchUiState.Loading
            searchFundsUseCase(query).fold(
                onSuccess = { _uiState.value = SearchUiState.Success(it) },
                onFailure = { _uiState.value = SearchUiState.Error(it.message ?: "Search failed") }
            )
        }
    }
}
