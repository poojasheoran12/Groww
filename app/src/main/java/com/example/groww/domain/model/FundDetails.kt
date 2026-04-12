package com.example.groww.domain.model

data class FundDetails(
    val id: Int,
    val name: String,
    val fundHouse: String,
    val category: String,
    val type: String,
    val latestNav: String,
    val navHistory: List<NavPoint>
)