package com.example.groww.data.local.entity

import androidx.room.*

@Entity(tableName = "watchlists")
data class WatchlistEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String
)

@Entity(
    tableName = "watchlist_fund_cross_ref",
    primaryKeys = ["watchlistId", "fundId"],
    indices = [Index("fundId")]
)
data class WatchlistFundCrossRef(
    val watchlistId: Long,
    val fundId: Int
)

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
