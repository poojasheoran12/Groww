package com.example.groww.domain.model

data class Fund(
    val id: Int,
    val name: String,
    val category: String,
    val latestNav: String? = null,
    val lastUpdated: Long = 0L
)
