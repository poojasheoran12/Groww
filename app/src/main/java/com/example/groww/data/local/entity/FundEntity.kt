package com.example.groww.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.groww.domain.model.Fund

@Entity(tableName = "funds")
data class FundEntity(
    @PrimaryKey val schemeCode: Int,
    val schemeName: String,
    val category: String,
    val latestNav: String? = null
)

fun FundEntity.toDomain(): Fund {
    return Fund(
        schemeCode = schemeCode,
        schemeName = schemeName
    )
}

fun Fund.toEntity(category: String): FundEntity {
    return FundEntity(
        schemeCode = schemeCode,
        schemeName = schemeName,
        category = category,
        latestNav = null // NAV is cached in ViewModel as per requirements
    )
}
