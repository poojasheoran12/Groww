package com.example.groww.data.mapper

import com.example.groww.data.local.entity.WatchlistWithFunds
import com.example.groww.domain.model.Watchlist

fun WatchlistWithFunds.toDomain(): Watchlist {
    return Watchlist(
        id = watchlist.id,
        name = watchlist.name,
        funds = funds.map { it.toDomain() }
    )
}
