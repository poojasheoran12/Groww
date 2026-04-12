package com.example.groww.data.local.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class WatchlistWithFunds(
    @Embedded val watchlist: WatchlistEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            WatchlistFundCrossRef::class,
            parentColumn = "watchlistId",
            entityColumn = "fundId"
        )
    )
    val funds: List<FundEntity>
)
