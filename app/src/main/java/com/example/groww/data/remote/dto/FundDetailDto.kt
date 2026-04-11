package com.example.groww.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class FundDetailDto(
    val meta: FundMetaDto,
    val data: List<NavHistoryDto>
)

@Serializable
data class FundMetaDto(
    val fund_house: String,
    val scheme_type: String,
    val scheme_category: String? = null,
    val scheme_code: Int,
    val scheme_name: String,
    val isin_growth: String? = null,
    val isin_div_reinvestment: String? = null
)

@Serializable
data class NavHistoryDto(
    val date: String,
    val nav: String
)