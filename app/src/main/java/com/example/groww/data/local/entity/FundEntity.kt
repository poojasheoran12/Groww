package com.example.groww.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "funds")
data class FundEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val category: String,
    val latestNav: String? = null,
    val lastUpdated: Long = System.currentTimeMillis()
)
