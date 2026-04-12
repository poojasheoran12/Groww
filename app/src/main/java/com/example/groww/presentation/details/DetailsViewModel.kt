package com.example.groww.presentation.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groww.domain.model.FundDetail
import com.example.groww.domain.usecase.GetFundDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class DetailsUiState {
    object Loading : DetailsUiState()
    data class Success(val details: FundDetail) : DetailsUiState()
    data class Error(val message: String) : DetailsUiState()
}

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val getFundDetailsUseCase: GetFundDetailsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailsUiState>(DetailsUiState.Loading)
    val uiState: StateFlow<DetailsUiState> = _uiState.asStateFlow()

    init {
        val schemeCode = savedStateHandle.get<Int>("id")
        if (schemeCode != null) {
            loadDetails(schemeCode)
        } else {
            _uiState.value = DetailsUiState.Error("Invalid scheme code")
        }
    }

    private fun loadDetails(schemeCode: Int) {
        viewModelScope.launch {
            _uiState.value = DetailsUiState.Loading
            getFundDetailsUseCase(schemeCode).fold(
                onSuccess = { _uiState.value = DetailsUiState.Success(it) },
                onFailure = { _uiState.value = DetailsUiState.Error(it.message ?: "Failed to load details") }
            )
        }
    }
}
