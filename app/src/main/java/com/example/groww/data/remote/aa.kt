package com.example.groww.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class FundDto(
    val schemeCode: Int,
    val schemeName: String
)
