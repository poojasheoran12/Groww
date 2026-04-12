package com.example.groww.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FundDetailDto(
    @SerialName("meta") val meta: FundMetaDto,
    @SerialName("data") val data: List<NavPointDto>
)
