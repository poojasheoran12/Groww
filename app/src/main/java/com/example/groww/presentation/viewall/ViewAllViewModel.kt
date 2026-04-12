package com.example.groww.presentation.viewall

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groww.domain.model.Fund
import com.example.groww.domain.model.FundWithNav
import com.example.groww.domain.usecase.GetCategoryFundsUseCase
import com.example.groww.domain.usecase.GetNavUseCase
import com.example.groww.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewAllViewModel @Inject constructor(
    private val getCategoryFundsUseCase: GetCategoryFundsUseCase,
    private val getNavUseCase: GetNavUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<FundWithNav>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<FundWithNav>>> = _uiState.asStateFlow()

    private var allFunds = listOf<Fund>()
    private var displayedFunds = mutableListOf<FundWithNav>()
    private val pageSize = 20
    private var currentPage = 0

    // Store NAVs in memory to avoid refetching
    private val navMap = mutableMapOf<Int, String?>()

    fun init(category: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = getCategoryFundsUseCase(category)
            result.onSuccess { funds ->
                allFunds = funds
                loadNextPage()
            }.onFailure {
                _uiState.value = UiState.Error(it.message ?: "Failed to load funds")
            }
        }
    }

    fun loadNextPage() {
        val start = currentPage * pageSize
        if (start >= allFunds.size) return

        val end = minOf(start + pageSize, allFunds.size)
        val nextBatch = allFunds.subList(start, end).map { 
            FundWithNav(it, navMap[it.schemeCode]) 
        }
        
        displayedFunds.addAll(nextBatch)
        _uiState.value = UiState.Success(displayedFunds.toList())
        currentPage++
    }

    /**
     * Lazy NAV loading for visible items. 
     * Called by UI when an item becomes visible if its NAV is null.
     */
    fun onFundVisible(schemeCode: Int) {
        if (navMap.containsKey(schemeCode)) return

        viewModelScope.launch {
            val navResult = getNavUseCase(schemeCode)
            val nav = navResult.getOrNull()
            navMap[schemeCode] = nav
            
            // Update the displayed list with the new NAV
            val updatedList = displayedFunds.map { 
                if (it.fund.schemeCode == schemeCode) it.copy(latestNav = nav) else it 
            }
            displayedFunds = updatedList.toMutableList()
            _uiState.value = UiState.Success(displayedFunds.toList())
        }
    }
}
