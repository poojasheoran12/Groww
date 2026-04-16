package com.example.groww.presentation.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groww.domain.usecase.GetExploreFundsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val getExploreFundsUseCase: GetExploreFundsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExploreUiState())
    val uiState: StateFlow<ExploreUiState> = _uiState.asStateFlow()

    init {
        observeData()
        refreshData()
    }

    private fun observeData() {
        getExploreFundsUseCase()
            .onEach { data ->
                _uiState.update { it.copy(
                    categories = data.mapKeys { entry -> entry.key.displayName },
                    isLoading = false
                )}
            }
            .launchIn(viewModelScope)
    }

    fun refreshData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            getExploreFundsUseCase.sync()
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(
                        error = e.message ?: "Unknown error occurred",
                        isLoading = false
                    )}
                }
        }
    }
}
