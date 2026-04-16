package com.example.groww.presentation.search

import com.example.groww.domain.model.Fund

data class SearchUiState(
    val query: String = "",
    val results: List<Fund> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isIdle: Boolean = true
)
