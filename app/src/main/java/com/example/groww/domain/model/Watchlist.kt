package com.example.groww.domain.model

data class Watchlist(
    val id: Long,
    val name: String,
    val funds: List<Fund> = emptyList()
)
