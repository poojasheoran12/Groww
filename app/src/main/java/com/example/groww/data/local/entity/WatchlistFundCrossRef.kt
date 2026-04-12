package com.example.groww.data.local.entity

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "watchlist_fund_cross_ref",
    primaryKeys = ["watchlistId", "fundId"],
    indices = [Index("fundId")]
)
data class WatchlistFundCrossRef(
    val watchlistId: Long,
    val fundId: Int
)
