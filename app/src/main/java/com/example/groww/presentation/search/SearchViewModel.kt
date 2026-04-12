package com.example.groww.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groww.domain.model.Fund
import com.example.groww.domain.usecase.GetNavUseCase
import com.example.groww.domain.usecase.SearchFundsUseCase
import com.example.groww.presentation.common.UiState
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

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _searchState = MutableStateFlow<UiState<List<Fund>>>(UiState.Idle)
    val searchState = _searchState.asStateFlow()

    init {
        _searchQuery
            .debounce(300L)
            .distinctUntilChanged()
            .onEach { query ->
                if (query.trim().length >= 2) {
                    performSearch(query.trim())
                } else {
                    _searchState.value = UiState.Idle
                }
            }
            .launchIn(viewModelScope)
    }

    fun onQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    private fun performSearch(query: String) {
        viewModelScope.launch {
            _searchState.value = UiState.Loading
            searchFundsUseCase(query)
                .onEach { results ->
                    if (results.isNotEmpty()) {
                        _searchState.value = UiState.Success(results)
                    }
                }
                .catch { e ->
                    _searchState.value = UiState.Error(e.message ?: "Search failed")
                }
                .collect()
        }
    }

    fun onItemVisible(id: Int) {
        viewModelScope.launch {
            getNavUseCase(id)
        }
    }
}
