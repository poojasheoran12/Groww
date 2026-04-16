package com.example.groww.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.groww.domain.model.FundCategory
import com.example.groww.presentation.details.DetailsScreen
import com.example.groww.presentation.details.DetailsViewModel
import com.example.groww.presentation.explore.ExploreScreen
import com.example.groww.presentation.explore.ExploreViewModel
import com.example.groww.presentation.search.SearchScreen
import com.example.groww.presentation.search.SearchViewModel
import com.example.groww.presentation.viewAll.ViewAllScreen
import com.example.groww.presentation.viewAll.ViewAllViewModel
import com.example.groww.presentation.watchlist.WatchlistScreen
import com.example.groww.presentation.watchlist.WatchlistViewModel
import com.example.groww.presentation.watchlistDetail.WatchlistDetailScreen

@Composable
fun MainScreen(navController: NavHostController = rememberNavController()) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomBarItems = listOf(
        BottomNavItem("Explore", Screen.Explore.route, Icons.Default.Search),
        BottomNavItem("Watchlist", Screen.Watchlist.route, Icons.Default.List)
    )

    val showBottomBar = bottomBarItems.any { it.route == currentDestination?.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                GrowwBottomBar(navController, currentDestination, bottomBarItems)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Graph.EXPLORE,
            modifier = androidx.compose.ui.Modifier.padding(innerPadding)
        ) {
            exploreNavGraph(navController)
            watchlistNavGraph(navController)
            
           
            composable(
                route = Screen.Details.route,
                arguments = listOf(navArgument(Screen.ARG_FUND_ID) { type = NavType.IntType })
            ) {
                DetailsScreen(
                    viewModel = hiltViewModel<DetailsViewModel>(),
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(route = Screen.Search.route) {
                SearchScreen(
                    viewModel = hiltViewModel<SearchViewModel>(),
                    onBackClick = { navController.popBackStack() },
                    onFundClick = { id -> navController.navigate(Screen.Details.createRoute(id)) }
                )
            }
        }
    }
}


fun NavGraphBuilder.exploreNavGraph(navController: NavHostController) {
    navigation(startDestination = Screen.Explore.route, route = Graph.EXPLORE) {
        composable(Screen.Explore.route) {
            ExploreScreen(
                viewModel = hiltViewModel<ExploreViewModel>(),
                onViewAllClick = { categoryName ->
                    val category = FundCategory.fromDisplayName(categoryName)
                    navController.navigate(Screen.ViewAll.createRoute(category.name))
                },
                onFundClick = { id -> navController.navigate(Screen.Details.createRoute(id)) },
                onSearchClick = { navController.navigate(Screen.Search.route) }
            )
        }

        composable(
            route = Screen.ViewAll.route,
            arguments = listOf(navArgument(Screen.ARG_CATEGORY) { type = NavType.StringType })
        ) { entry ->
            val categoryName = entry.arguments?.getString(Screen.ARG_CATEGORY) ?: FundCategory.ALL.name
            ViewAllScreen(
                category = FundCategory.valueOf(categoryName).displayName,
                viewModel = hiltViewModel<ViewAllViewModel>(),
                onBackClick = { navController.popBackStack() },
                onFundClick = { id -> navController.navigate(Screen.Details.createRoute(id)) }
            )
        }
    }
}


fun NavGraphBuilder.watchlistNavGraph(navController: NavHostController) {
    navigation(startDestination = Screen.Watchlist.route, route = Graph.WATCHLIST) {
        composable(Screen.Watchlist.route) {
            WatchlistScreen(
                viewModel = hiltViewModel<WatchlistViewModel>(),
                onWatchlistClick = { id -> navController.navigate(Screen.WatchlistDetail.createRoute(id)) }
            )
        }

        composable(
            route = Screen.WatchlistDetail.route,
            arguments = listOf(navArgument(Screen.ARG_WATCHLIST_ID) { type = NavType.LongType })
        ) {
            WatchlistDetailScreen(
                onBackClick = { navController.popBackStack() },
                onFundClick = { id -> navController.navigate(Screen.Details.createRoute(id)) },
                onExploreClick = { navController.navigate(Screen.Explore.route) }
            )
        }
    }
}

@Composable
private fun GrowwBottomBar(
    navController: NavHostController,
    currentDestination: NavDestination?,
    items: List<BottomNavItem>
) {
    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                label = { Text(item.name) },
                icon = { Icon(item.icon, contentDescription = item.name) },
                selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

private data class BottomNavItem(val name: String, val route: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)
