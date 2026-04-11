package com.example.groww.presentation.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groww.domain.model.FundWithNav
import com.example.groww.domain.usecase.GetCategoryFundsUseCase
import com.example.groww.domain.usecase.GetFundDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ExploreUiState {
    object Loading : ExploreUiState()
    data class Success(val categories: Map<String, List<FundWithNav>>) : ExploreUiState()
    data class Error(val message: String) : ExploreUiState()
    object Empty : ExploreUiState()
}

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val getCategoryFundsUseCase: GetCategoryFundsUseCase,
    private val getFundDetailsUseCase: GetFundDetailsUseCase
) : ViewModel() {

    private val categories = listOf(
        "Index Funds",
        "Bluechip Funds",
        "Tax Saver (ELSS)",
        "Large Cap Funds"
    )

    private val _uiState = MutableStateFlow<ExploreUiState>(ExploreUiState.Loading)
    val uiState: StateFlow<ExploreUiState> = _uiState.asStateFlow()

    // Internal cache for NAV values as requested
    private val navCache = mutableMapOf<Int, String>()

    init {
        loadExploreData()
    }

    fun loadExploreData() {
        viewModelScope.launch {
            _uiState.value = ExploreUiState.Loading
            try {
                val discoveryData = coroutineScope {
                    categories.map { category ->
                        async {
                            val fundsResult = getCategoryFundsUseCase(category)
                            val funds = fundsResult.getOrNull()?.take(4) ?: emptyList()
                            
                            // Fetch NAV for these 4 funds in parallel
                            val fundsWithNav = funds.map { fund ->
                                async {
                                    val cachedNav = navCache[fund.schemeCode]
                                    if (cachedNav != null) {
                                        FundWithNav(fund, cachedNav)
                                    } else {
                                        val detailsResult = getFundDetailsUseCase(fund.schemeCode)
                                        val latestNav = detailsResult.getOrNull()?.data?.firstOrNull()?.nav
                                        latestNav?.let { navCache[fund.schemeCode] = it }
                                        FundWithNav(fund, latestNav)
                                    }
                                }
                            }.awaitAll()
                            
                            category to fundsWithNav
                        }
                    }.awaitAll().toMap()
                }

                if (discoveryData.values.all { it.isEmpty() }) {
                    _uiState.value = ExploreUiState.Empty
                } else {
                    _uiState.value = ExploreUiState.Success(discoveryData)
                }
            } catch (e: Exception) {
                _uiState.value = ExploreUiState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }
}
