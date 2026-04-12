package com.example.groww.presentation.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groww.domain.model.FundDetails
import com.example.groww.domain.usecase.GetFundDetailsUseCase
import com.example.groww.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val getFundDetailsUseCase: GetFundDetailsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _detailsState = MutableStateFlow<UiState<FundDetails>>(UiState.Loading)
    val detailsState = _detailsState.asStateFlow()

    init {
        val fundId = savedStateHandle.get<Int>("id")
        fundId?.let { loadDetails(it) }
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
}
