package com.example.groww.data.mapper

import com.example.groww.data.local.entity.FundEntity
import com.example.groww.data.remote.dto.SearchResultDto
import com.example.groww.domain.model.Fund

fun SearchResultDto.toEntity(category: String): FundEntity {
    return FundEntity(
        id = schemeCode,
        name = schemeName,
        category = category
    )
}

fun SearchResultDto.toDomain(category: String): Fund {
    return Fund(
        id = schemeCode,
        name = schemeName,
        category = category
    )
}
