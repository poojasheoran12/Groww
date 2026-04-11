package com.example.groww.data.mapper

import com.example.groww.data.remote.dto.FundDto
import com.example.groww.domain.model.Fund

fun FundDto.toDomain(): Fund {
    return Fund(
        schemeCode = schemeCode,
        schemeName = schemeName,
    )
}