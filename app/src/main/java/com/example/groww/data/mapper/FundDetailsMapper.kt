package com.example.groww.data.mapper

import com.example.groww.data.local.entity.FundEntity
import com.example.groww.domain.model.FundDetails

fun FundDetails.toEntity(): FundEntity {
    return FundEntity(
        id = id,
        name = name,
        category = category,
        latestNav = latestNav,
        lastUpdated = System.currentTimeMillis()
    )
}
