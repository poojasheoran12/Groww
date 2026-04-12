package com.example.groww.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NavPointDto(
    @SerialName("date") val date: String,
    @SerialName("nav") val nav: String
)
