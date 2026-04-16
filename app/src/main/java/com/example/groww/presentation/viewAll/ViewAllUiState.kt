package com.example.groww.presentation.viewAll

import com.example.groww.domain.model.Fund

data class ViewAllUiState(
    val visibleFunds: List<Fund> = emptyList(),
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val isEndOfList: Boolean = false,
    val currentPage: Int = 0
)
