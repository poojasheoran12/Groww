package com.example.groww.data.mapper

import com.example.groww.data.remote.dto.FundDetailDto
import com.example.groww.domain.model.FundDetails
import com.example.groww.domain.model.NavPoint

fun FundDetailDto.toDomain(): FundDetails {
    return FundDetails(
        id = meta.schemeCode,
        name = meta.schemeName,
        fundHouse = meta.fundHouse,
        category = meta.schemeCategory,
        type = meta.schemeType,
        latestNav = data.firstOrNull()?.nav ?: "0.0",
        navHistory = data.map {
            NavPoint(
                date = it.date,
                nav = it.nav.toDoubleOrNull() ?: 0.0
            )
        }.reversed()
    )
}
