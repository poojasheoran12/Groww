package com.example.groww.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchResultDto(
    @SerialName("schemeCode") val schemeCode: Int,
    @SerialName("schemeName") val schemeName: String
)
