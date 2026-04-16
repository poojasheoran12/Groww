package com.example.groww.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groww.domain.usecase.GetNavUseCase
import com.example.groww.domain.usecase.SearchFundsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchFundsUseCase: SearchFundsUseCase,
    private val getNavUseCase: GetNavUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val queryFlow = MutableStateFlow("")

    init {
        queryFlow
            .debounce(300L)
            .distinctUntilChanged()
            .onEach { query ->
                if (query.length >= 2) {
                    performSearch(query)
                } else {
                    _uiState.update { it.copy(results = emptyList(), isIdle = true, isLoading = false) }
                }
            }
            .launchIn(viewModelScope)
    }

    fun onQueryChange(newQuery: String) {
        queryFlow.value = newQuery
        _uiState.update { it.copy(query = newQuery, error = null) }
    }

    private fun performSearch(query: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, isIdle = false) }
            searchFundsUseCase(query)
                .catch { e ->
                    _uiState.update { it.copy(error = e.message ?: "Search failed", isLoading = false) }
                }
                .onEach { results ->
                    _uiState.update { it.copy(results = results, isLoading = false) }
                }
                .launchIn(viewModelScope)
        }
    }

    fun onItemVisible(id: Int) {
        viewModelScope.launch {
            getNavUseCase(id)
        }
    }
}
