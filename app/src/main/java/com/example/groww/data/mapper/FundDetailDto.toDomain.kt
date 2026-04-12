package com.example.groww.data.mapper

import com.example.groww.data.remote.dto.FundDetailDto
import com.example.groww.domain.model.FundDetail
import com.example.groww.domain.model.NavPoint

fun FundDetailDto.toDomain(): FundDetail {
    return FundDetail(
        schemeCode = meta.scheme_code,
        schemeName = meta.scheme_name,
        fundHouse = meta.fund_house,
        schemeType = meta.scheme_type,
        schemeCategory = meta.scheme_category,
        navHistory = data.map {
            NavPoint(
                date = it.date,
                nav = it.nav.toDoubleOrNull() ?: 0.0
            )
        },
    )
}