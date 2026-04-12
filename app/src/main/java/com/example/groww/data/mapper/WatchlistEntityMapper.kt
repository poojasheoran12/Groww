package com.example.groww.data.mapper

import com.example.groww.data.local.entity.WatchlistEntity
import com.example.groww.domain.model.Watchlist

fun WatchlistEntity.toDomain(): Watchlist {
    return Watchlist(
        id = id,
        name = name
    )
}
