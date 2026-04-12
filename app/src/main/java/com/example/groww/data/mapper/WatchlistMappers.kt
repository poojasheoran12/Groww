package com.example.groww.data.mapper

import com.example.groww.data.local.entity.WatchlistEntity
import com.example.groww.data.local.entity.WatchlistFundCrossRef
import com.example.groww.data.local.entity.WatchlistWithFunds
import com.example.groww.domain.model.Watchlist

fun WatchlistEntity.toDomain(): Watchlist {
    return Watchlist(
        id = id,
        name = name
    )
}

fun WatchlistWithFunds.toDomain(): Watchlist {
    return Watchlist(
        id = watchlist.id,
        name = watchlist.name,
        funds = funds.map { it.toDomain() }
    )
}
