package com.example.groww.data.mapper

import com.example.groww.data.remote.dto.FundDetailDto
import com.example.groww.data.remote.dto.FundMetaDto
import com.example.groww.data.remote.dto.NavHistoryDto
import com.example.groww.domain.model.FundDetail
import com.example.groww.domain.model.FundMeta
import com.example.groww.domain.model.NavHistory

fun FundDetailDto.toDomain(): FundDetail {
    return FundDetail(
        meta = meta.toDomain(),
        data = data.map { it.toDomain() }
    )
}

fun FundMetaDto.toDomain(): FundMeta {
    return FundMeta(
        fundHouse = fund_house,
        schemeType = scheme_type,
        schemeCategory = scheme_category,
        schemeCode = scheme_code,
        schemeName = scheme_name,
        isinGrowth = isin_growth,
        isinDivReinvestment = isin_div_reinvestment
    )
}

fun NavHistoryDto.toDomain(): NavHistory {
    return NavHistory(
        date = date,
        nav = nav
    )
}