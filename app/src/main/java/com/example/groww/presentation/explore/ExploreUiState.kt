package com.example.groww.presentation.explore

import com.example.groww.domain.model.Fund

data class ExploreUiState(
    val categories: Map<String, List<Fund>> = emptyMap(),
    val isLoading: Boolean = true,
    val error: String? = null
)
