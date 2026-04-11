package com.example.groww.data.remote.dto

import com.example.groww.domain.model.Fund
import kotlinx.serialization.Serializable

@Serializable
data class FundDto(
    val schemeCode: Int,
    val schemeName: String
)

