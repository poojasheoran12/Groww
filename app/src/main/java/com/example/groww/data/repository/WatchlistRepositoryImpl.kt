package com.example.groww.data.repository

import com.example.groww.data.local.db.FundDao
import com.example.groww.data.local.entity.WatchlistEntity
import com.example.groww.data.local.entity.WatchlistFundCrossRef
import com.example.groww.data.mapper.toDomain
import com.example.groww.domain.model.Watchlist
import com.example.groww.domain.repository.WatchlistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WatchlistRepositoryImpl @Inject constructor(
    private val dao: FundDao
) : WatchlistRepository {

    override fun getAllWatchlists(): Flow<List<Watchlist>> {
        return dao.getAllWatchlists().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getWatchlistWithFunds(id: Long): Flow<Watchlist> {
        return dao.getWatchlistWithFunds(id).map { it.toDomain() }
    }

    override suspend fun createWatchlist(name: String): Long {
        return dao.insertWatchlist(WatchlistEntity(name = name))
    }

    override suspend fun deleteWatchlist(id: Long) {
        dao.deleteWatchlist(WatchlistEntity(id = id, name = ""))
    }

    override suspend fun addFundToWatchlist(fundId: Int, watchlistId: Long) {
        dao.insertFundToWatchlist(WatchlistFundCrossRef(watchlistId, fundId))
    }

    override suspend fun removeFundFromWatchlist(fundId: Int, watchlistId: Long) {
        dao.removeFundFromWatchlist(WatchlistFundCrossRef(watchlistId, fundId))
    }

    override fun isFundInAnyWatchlist(fundId: Int): Flow<Boolean> {
        return dao.isFundInAnyWatchlist(fundId)
    }

    override fun getWatchlistsForFund(fundId: Int): Flow<List<Long>> {
        return dao.getWatchlistsForFund(fundId)
    }
}
