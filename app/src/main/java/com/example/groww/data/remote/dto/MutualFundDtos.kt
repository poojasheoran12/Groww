package com.example.groww.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchResultDto(
    @SerialName("schemeCode") val schemeCode: Int,
    @SerialName("schemeName") val schemeName: String
)

@Serializable
data class FundDetailDto(
    @SerialName("meta") val meta: FundMetaDto,
    @SerialName("data") val data: List<NavPointDto>
)

@Serializable
data class FundMetaDto(
    @SerialName("fund_house") val fundHouse: String,
    @SerialName("scheme_type") val schemeType: String,
    @SerialName("scheme_category") val schemeCategory: String,
    @SerialName("scheme_code") val schemeCode: Int,
    @SerialName("scheme_name") val schemeName: String
)

@Serializable
data class NavPointDto(
    @SerialName("date") val date: String,
    @SerialName("nav") val nav: String
)
