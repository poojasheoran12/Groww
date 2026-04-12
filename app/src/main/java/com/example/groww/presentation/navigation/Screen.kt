package com.example.groww.presentation.navigation

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

sealed class Screen(val route: String) {
    object Explore : Screen("explore_base")
    
    object ViewAll : Screen("view_all/{$ARG_CATEGORY}") {
        fun createRoute(category: String): String {
            val encoded = URLEncoder.encode(category, StandardCharsets.UTF_8.toString())
            return "view_all/$encoded"
        }
    }
    
    object Details : Screen("details/{$ARG_FUND_ID}") {
        fun createRoute(fundId: Int) = "details/$fundId"
    }
    
    object Search : Screen("search_base")
    
    object Watchlist : Screen("watchlist_base")
    
    object WatchlistDetail : Screen("watchlist_detail/{$ARG_WATCHLIST_ID}") {
        fun createRoute(watchlistId: Long) = "watchlist_detail/$watchlistId"
    }

    companion object {
        const val ARG_FUND_ID = "fundId"
        const val ARG_WATCHLIST_ID = "watchlistId"
        const val ARG_CATEGORY = "category"
    }
}

const val ROOT_GRAPH_ROUTE = "root"
const val EXPLORE_GRAPH_ROUTE = "explore_graph"
const val WATCHLIST_GRAPH_ROUTE = "watchlist_graph"
