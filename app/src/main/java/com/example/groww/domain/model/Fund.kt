package com.example.groww.domain.model

data class Fund(
    val schemeCode: Int,
    val schemeName: String
)

data class FundWithNav(
    val fund: Fund,
    val latestNav: String? = null
)
