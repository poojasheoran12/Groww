package com.example.groww.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "watchlist_fund_cross_ref",
    primaryKeys = ["watchlistId", "fundId"],
    indices = [Index("fundId")],
    foreignKeys = [
        ForeignKey(
            entity = WatchlistEntity::class,
            parentColumns = ["id"],
            childColumns = ["watchlistId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = FundEntity::class,
            parentColumns = ["id"],
            childColumns = ["fundId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class WatchlistFundCrossRef(
    val watchlistId: Long,
    val fundId: Int
)
