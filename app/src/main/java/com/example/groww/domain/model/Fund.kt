package com.example.groww.domain.model

data class Fund(
    val schemeCode: Int,
    val schemeName: String,
    val category: String? = null,
    val latestNav: String? = null
)
