package com.example.groww.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.groww.domain.model.Fund

@Entity(tableName = "funds")
data class FundEntity(
    @PrimaryKey val schemeCode: Int,
    val schemeName: String,
    val category: String? = null,
    val latestNav: String? = null,
    val lastUpdated: Long = System.currentTimeMillis()
)

fun FundEntity.toDomain(): Fund {
    return Fund(
        schemeCode = schemeCode,
        schemeName = schemeName,
        category = category,
        latestNav = latestNav
    )
}

fun Fund.toEntity(overrideCategory: String? = null, overrideNav: String? = null): FundEntity {
    return FundEntity(
        schemeCode = schemeCode,
        schemeName = schemeName,
        category = overrideCategory ?: category,
        latestNav = overrideNav ?: latestNav,
        lastUpdated = System.currentTimeMillis()
    )
}
