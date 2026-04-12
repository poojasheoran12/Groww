package com.example.groww.presentation.navigation

sealed class Screen(val route: String) {
    object Explore : Screen("explore")
    object ViewAll : Screen("view_all/{category}") {
        fun createRoute(category: String) = "view_all/$category"
    }
    object Details : Screen("details/{id}") {
        fun createRoute(id: Int) = "details/$id"
    }
    object Search : Screen("search")
    
    object Watchlist : Screen("watchlist")
    object WatchlistDetail : Screen("watchlist/{id}/{name}") {
        fun createRoute(id: Long, name: String) = "watchlist/$id/$name"
    }
}

const val EXPLORE_GRAPH_ROUTE = "explore_graph"
const val WATCHLIST_GRAPH_ROUTE = "watchlist_graph"
