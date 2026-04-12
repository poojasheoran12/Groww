package com.example.groww.data.mapper

import com.example.groww.data.local.entity.FundEntity
import com.example.groww.domain.model.Fund

fun FundEntity.toDomain(): Fund {
    return Fund(
        id = id,
        name = name,
        category = category,
        latestNav = latestNav,
        lastUpdated = lastUpdated
    )
}
