package com.example.groww.presentation.explore.model

import com.example.groww.domain.model.Fund

/**
 * Presentation Model: Wraps the domain model and adds UI-specific states.
 * This ensures the domain layer remains clean and unaware of UI concerns.
 */
data class ExploreFundUiState(
    val fund: Fund,
    val loadingNav: Boolean = false,
    val error: String? = null
)
