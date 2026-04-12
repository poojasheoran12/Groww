package com.example.groww.presentation.explore

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groww.domain.model.Fund
import com.example.groww.domain.model.FundCategory
import com.example.groww.domain.usecase.GetFundsByCategoryUseCase
import com.example.groww.domain.usecase.GetNavUseCase
import com.example.groww.domain.usecase.SyncCategoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ViewAllUiState(
    val visibleFunds: List<Fund> = emptyList(),
    val currentPage: Int = 0,
    val isLoadingMore: Boolean = false,
    val isEndOfList: Boolean = false
)

@HiltViewModel
class ViewAllViewModel @Inject constructor(
    private val syncCategoryUseCase: SyncCategoryUseCase,
    private val getFundsByCategoryUseCase: GetFundsByCategoryUseCase,
    private val getNavUseCase: GetNavUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val PAGE_SIZE = 20
    private val categoryName: String = savedStateHandle.get<String>("category") ?: ""
    private val category = FundCategory.fromDisplayName(categoryName)

    private val _state = MutableStateFlow(ViewAllUiState())
    val state = _state.asStateFlow()

    private var allFunds: List<Fund> = emptyList()

    init {
        // First sync with network and save to Room (shells only)
        viewModelScope.launch {
            syncCategoryUseCase(category)
        }
        
        // Then observe Room as Single Source of Truth
        observeFunds()
    }

    private fun observeFunds() {
        getFundsByCategoryUseCase(category)
            .onEach { funds ->
                allFunds = funds
                updateVisibleFunds(page = _state.value.currentPage)
            }
            .launchIn(viewModelScope)
    }

    fun loadNextPage() {
        if (_state.value.isLoadingMore || _state.value.isEndOfList) return

        viewModelScope.launch {
            _state.update { it.copy(isLoadingMore = true) }
            val nextPage = _state.value.currentPage + 1
            updateVisibleFunds(page = nextPage)
        }
    }

    private fun updateVisibleFunds(page: Int) {
        val start = 0
        val end = ((page + 1) * PAGE_SIZE).coerceAtMost(allFunds.size)
        
        if (allFunds.isEmpty()) return

        val slice = allFunds.subList(start, end)
        
        _state.update { 
            it.copy(
                visibleFunds = slice,
                currentPage = page,
                isLoadingMore = false,
                isEndOfList = end >= allFunds.size
            )
        }
    }

    fun onItemVisible(fundId: Int) {
        viewModelScope.launch {
            getNavUseCase(fundId)
        }
    }
}
