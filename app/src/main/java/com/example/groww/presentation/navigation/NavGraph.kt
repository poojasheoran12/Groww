package com.example.groww.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.example.groww.presentation.details.DetailsScreen
import com.example.groww.presentation.details.DetailsViewModel
import com.example.groww.presentation.explore.ExploreScreen
import com.example.groww.presentation.explore.ExploreViewModel
import com.example.groww.presentation.explore.ViewAllScreen
import com.example.groww.presentation.explore.ViewAllViewModel
import com.example.groww.presentation.search.SearchScreen
import com.example.groww.presentation.search.SearchViewModel

@Composable
fun GrowwNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = EXPLORE_GRAPH_ROUTE
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

        // Details
        composable(
            route = Screen.Details.route,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { entry ->
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
