package com.example.groww.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FundMetaDto(
    @SerialName("fund_house") val fundHouse: String,
    @SerialName("scheme_type") val schemeType: String,
    @SerialName("scheme_category") val schemeCategory: String,
    @SerialName("scheme_code") val schemeCode: Int,
    @SerialName("scheme_name") val schemeName: String
)
