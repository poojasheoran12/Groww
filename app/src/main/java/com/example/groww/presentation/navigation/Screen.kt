package com.example.groww.presentation.navigation


object Graph {
    const val ROOT = "root_graph"
    const val EXPLORE = "explore_graph"
    const val WATCHLIST = "watchlist_graph"
}

sealed class Screen(val route: String) {
  
    data object Explore : Screen("explore_home")
    data object Watchlist : Screen("watchlist_home")
    
   
    data object Search : Screen("search_screen")
    
    data object ViewAll : Screen("view_all/{category}") {
        fun createRoute(category: String) = "view_all/$category"
    }
    
    data object Details : Screen("details/{fundId}") {
        fun createRoute(fundId: Int) = "details/$fundId"
    }
    
    data object WatchlistDetail : Screen("watchlist_detail/{watchlistId}") {
        fun createRoute(watchlistId: Long) = "watchlist_detail/$watchlistId"
    }

    companion object {
        const val ARG_FUND_ID = "fundId"
        const val ARG_WATCHLIST_ID = "watchlistId"
        const val ARG_CATEGORY = "category"
    }
}
