package com.example.groww.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.groww.presentation.details.DetailsScreen
import com.example.groww.presentation.details.DetailsViewModel
import com.example.groww.presentation.explore.ExploreScreen
import com.example.groww.presentation.explore.ExploreViewModel
import com.example.groww.presentation.explore.ViewAllScreen
import com.example.groww.presentation.explore.ViewAllViewModel
import com.example.groww.presentation.search.SearchScreen
import com.example.groww.presentation.search.SearchViewModel
import com.example.groww.presentation.watchlist.WatchlistDetailScreen
import com.example.groww.presentation.watchlist.WatchlistScreen
import com.example.groww.presentation.watchlist.WatchlistViewModel

@Composable
fun GrowwNavGraph(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Standard screens that show Bottom Bar
    val bottomBarScreens = listOf(Screen.Explore.route, Screen.Watchlist.route)
    val shouldShowBottomBar = currentDestination?.route in bottomBarScreens

    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Search, contentDescription = "Explore") },
                        label = { Text("Explore") },
                        selected = currentDestination?.hierarchy?.any { it.route == Screen.Explore.route } == true,
                        onClick = {
                            navController.navigate(Screen.Explore.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.List, contentDescription = "Watchlist") },
                        label = { Text("Watchlist") },
                        selected = currentDestination?.hierarchy?.any { it.route == Screen.Watchlist.route } == true,
                        onClick = {
                            navController.navigate(Screen.Watchlist.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = EXPLORE_GRAPH_ROUTE,
            modifier = androidx.compose.ui.Modifier.padding(innerPadding)
        ) {
            // Explore Graph
            navigation(
                startDestination = Screen.Explore.route,
                route = EXPLORE_GRAPH_ROUTE
            ) {
                composable(Screen.Explore.route) { entry ->
                    val viewModel = hiltViewModel<ExploreViewModel>(entry)
                    ExploreScreen(
                        viewModel = viewModel,
                        onViewAllClick = { category ->
                            navController.navigate(Screen.ViewAll.createRoute(category))
                        },
                        onFundClick = { id ->
                            navController.navigate(Screen.Details.createRoute(id))
                        },
                        onSearchClick = {
                            navController.navigate(Screen.Search.route)
                        }
                    )
                }

                composable(
                    route = Screen.ViewAll.route,
                    arguments = listOf(navArgument("category") { type = NavType.StringType })
                ) { entry ->
                    val category = entry.arguments?.getString("category") ?: ""
                    val viewModel = hiltViewModel<ViewAllViewModel>()
                    ViewAllScreen(
                        category = category,
                        viewModel = viewModel,
                        onBackClick = { navController.popBackStack() },
                        onFundClick = { id ->
                            navController.navigate(Screen.Details.createRoute(id))
                        }
                    )
                }
            }

            // Watchlist Graph
            navigation(
                startDestination = Screen.Watchlist.route,
                route = WATCHLIST_GRAPH_ROUTE
            ) {
                composable(Screen.Watchlist.route) {
                    val viewModel = hiltViewModel<WatchlistViewModel>()
                    WatchlistScreen(
                        viewModel = viewModel,
                        onWatchlistClick = { id, name ->
                            navController.navigate(Screen.WatchlistDetail.createRoute(id, name))
                        }
                    )
                }

                composable(
                    route = Screen.WatchlistDetail.route,
                    arguments = listOf(
                        navArgument("id") { type = NavType.LongType },
                        navArgument("name") { type = NavType.StringType }
                    )
                ) { entry ->
                    val id = entry.arguments?.getLong("id") ?: -1L
                    val name = entry.arguments?.getString("name") ?: ""
                    WatchlistDetailScreen(
                        watchlistId = id,
                        watchlistName = name,
                        onBackClick = { navController.popBackStack() },
                        onFundClick = { fundId ->
                            navController.navigate(Screen.Details.createRoute(fundId))
                        }
                    )
                }
            }

            // Details
            composable(
                route = Screen.Details.route,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) {
                val viewModel = hiltViewModel<DetailsViewModel>()
                DetailsScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            // Search
            composable(route = Screen.Search.route) {
                val viewModel = hiltViewModel<SearchViewModel>()
                SearchScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() },
                    onFundClick = { id ->
                        navController.navigate(Screen.Details.createRoute(id))
                    }
                )
            }
        }
    }
}
