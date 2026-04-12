package com.example.groww.presentation.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groww.domain.model.Fund
import com.example.groww.domain.usecase.CheckAndRefreshNavUseCase
import com.example.groww.domain.usecase.CleanupOldDataUseCase
import com.example.groww.domain.usecase.GetFundsByCategoryUseCase
import com.example.groww.domain.usecase.RefreshExploreFundsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ExploreUiState {
    object Loading : ExploreUiState()
    data class Success(val categories: Map<String, List<Fund>>) : ExploreUiState()
    data class Error(val message: String) : ExploreUiState()
}

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val getFundsByCategoryUseCase: GetFundsByCategoryUseCase,
    private val refreshExploreFundsUseCase: RefreshExploreFundsUseCase,
    private val checkAndRefreshNavUseCase: CheckAndRefreshNavUseCase,
    private val cleanupOldDataUseCase: CleanupOldDataUseCase
) : ViewModel() {

    private val categories = listOf("Index", "Bluechip", "Tax", "Large")

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    val uiState: StateFlow<ExploreUiState> = combine(
        categories.map { category ->
            getFundsByCategoryUseCase(category)
        }
    ) { categoryData ->
        val dataMap = categories.zip(categoryData.toList()).toMap()
        if (dataMap.values.all { it.isEmpty() }) {
            ExploreUiState.Loading
        } else {
            ExploreUiState.Success(dataMap)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ExploreUiState.Loading
    )

    init {
        viewModelScope.launch {
            cleanupOldDataUseCase()
        }
        refreshData()
    }

    fun refreshData() {
        viewModelScope.launch {
            _isRefreshing.value = true
            refreshExploreFundsUseCase()
            _isRefreshing.value = false
        }
    }

    fun onFundVisible(schemeCode: Int) {
        viewModelScope.launch {
            checkAndRefreshNavUseCase(schemeCode)
        }
    }
}
