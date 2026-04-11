package com.example.groww.data.remote.dto

data class FundDetailsDto(
    val meta: Meta,
    val data: List<NavData>
)

data class Meta(
    val scheme_name: String
)

data class NavData(
    val date: String,
    val nav: String
)