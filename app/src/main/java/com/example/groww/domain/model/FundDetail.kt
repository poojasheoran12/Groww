package com.example.groww.domain.model

data class FundDetail(
    val schemeCode: Int,
    val schemeName: String,
    val fundHouse: String,
    val schemeType: String,
    val schemeCategory: String?,
    val navHistory: List<NavPoint>
)

data class NavPoint(
    val date: String,
    val nav: Double
)
