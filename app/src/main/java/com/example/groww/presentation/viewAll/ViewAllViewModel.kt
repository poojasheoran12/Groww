package com.example.groww.presentation.viewAll

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groww.domain.model.Fund
import com.example.groww.domain.model.FundCategory
import com.example.groww.domain.usecase.GetFundsByCategoryUseCase
import com.example.groww.domain.usecase.GetNavUseCase
import com.example.groww.domain.usecase.SyncCategoryUseCase
import com.example.groww.presentation.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewAllViewModel @Inject constructor(
    private val syncCategoryUseCase: SyncCategoryUseCase,
    private val getFundsByCategoryUseCase: GetFundsByCategoryUseCase,
    private val getNavUseCase: GetNavUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val PAGE_SIZE = 20
    private val initialCategoryName: String = savedStateHandle[Screen.ARG_CATEGORY] ?: FundCategory.ALL.displayName
    
    private val _currentCategory = MutableStateFlow(FundCategory.fromDisplayName(initialCategoryName))
    val currentCategory: StateFlow<FundCategory> = _currentCategory.asStateFlow()

    private val _uiState = MutableStateFlow(ViewAllUiState())
    val uiState: StateFlow<ViewAllUiState> = _uiState.asStateFlow()

    private var allFunds: List<Fund> = emptyList()
    private var observeJob: Job? = null

    init {
        observeFunds()
        refreshData()
    }

    private fun observeFunds() {
        observeJob?.cancel()
        observeJob = getFundsByCategoryUseCase(_currentCategory.value)
            .onEach { funds ->
                allFunds = funds
                if (funds.isNotEmpty()) {
                    updateVisibleFunds(page = 0)
                } else if (_uiState.value.isLoading.not()) {
                    _uiState.update { it.copy(isLoading = false, visibleFunds = emptyList()) }
                }
            }
            .launchIn(viewModelScope)
    }

    fun selectCategory(category: FundCategory) {
        if (_currentCategory.value == category) return
        _currentCategory.value = category
        _uiState.update { it.copy(isLoading = true, visibleFunds = emptyList(), currentPage = 0, isEndOfList = false) }
        observeFunds()
        refreshData()
    }

    fun refreshData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = allFunds.isEmpty(), error = null) }
            syncCategoryUseCase(_currentCategory.value)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                }
                .onFailure { e ->
                    if (allFunds.isEmpty()) {
                        _uiState.update { it.copy(error = e.message ?: "Failed to load funds", isLoading = false) }
                    } else {
                        _uiState.update { it.copy(isLoading = false) }
                    }
                }
        }
    }

    fun loadNextPage() {
        if (_uiState.value.isLoadingMore || _uiState.value.isEndOfList) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true) }
            val nextPage = _uiState.value.currentPage + 1
            updateVisibleFunds(page = nextPage)
        }
    }

    private fun updateVisibleFunds(page: Int) {
        val end = ((page + 1) * PAGE_SIZE).coerceAtMost(allFunds.size)
        val slice = allFunds.subList(0, end)
        
        _uiState.update { it.copy(
            visibleFunds = slice,
            currentPage = page,
            isLoadingMore = false,
            isEndOfList = end >= allFunds.size,
            isLoading = false
        )}
    }

    fun onItemVisible(fundId: Int) {
        viewModelScope.launch {
            getNavUseCase(fundId)
        }
    }
}
