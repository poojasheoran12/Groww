package com.example.groww.data.mapper

import com.example.groww.data.local.entity.FundEntity
import com.example.groww.data.remote.dto.FundDetailDto
import com.example.groww.data.remote.dto.SearchResultDto
import com.example.groww.domain.model.Fund
import com.example.groww.domain.model.FundDetails
import com.example.groww.domain.model.NavPoint

fun SearchResultDto.toEntity(category: String): FundEntity {
    return FundEntity(
        id = schemeCode,
        name = schemeName,
        category = category
    )
}

fun FundEntity.toDomain(): Fund {
    return Fund(
        id = id,
        name = name,
        category = category,
        latestNav = latestNav,
        lastUpdated = lastUpdated
    )
}

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

fun FundDetails.toEntity(): FundEntity {
    return FundEntity(
        id = id,
        name = name,
        category = category,
        latestNav = latestNav,
        lastUpdated = System.currentTimeMillis()
    )
}

fun SearchResultDto.toDomain(category: String): Fund {
    return Fund(
        id = schemeCode,
        name = schemeName,
        category = category
    )
}
