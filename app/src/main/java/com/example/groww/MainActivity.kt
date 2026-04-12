package com.example.groww

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.groww.presentation.Screen
import com.example.groww.presentation.details.DetailsScreen
import com.example.groww.presentation.details.DetailsViewModel
import com.example.groww.presentation.explore.ExploreScreen
import com.example.groww.presentation.explore.ExploreViewModel
import com.example.groww.presentation.search.SearchScreen
import com.example.groww.presentation.search.SearchViewModel
import com.example.groww.presentation.viewall.ViewAllScreen
import com.example.groww.ui.theme.GrowwTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GrowwTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GrowwApp()
                }
            }
        }
    }
}

@Composable
fun GrowwApp() {
    val navController = rememberNavController()
    
    NavHost(navController = navController, startDestination = "explore_graph") {
        navigation(startDestination = Screen.Explore.route, route = "explore_graph") {
            composable(Screen.Explore.route) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("explore_graph")
                }
                val viewModel: ExploreViewModel = hiltViewModel(parentEntry)
                ExploreScreen(
                    viewModel = viewModel,
                    onViewAllClick = { category ->
                        navController.navigate(Screen.ViewAll.createRoute(category))
                    },
                    onSearchClick = {
                        navController.navigate(Screen.Search.route)
                    },
                    onFundClick = { id ->
                        navController.navigate(Screen.Details.createRoute(id))
                    }
                )
            }
            
            composable(
                route = Screen.ViewAll.route,
                arguments = listOf(navArgument("category") { type = NavType.StringType })
            ) { backStackEntry ->
                val category = backStackEntry.arguments?.getString("category") ?: ""
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("explore_graph")
                }
                val viewModel: ExploreViewModel = hiltViewModel(parentEntry)
                ViewAllScreen(
                    category = category,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onFundClick = { id ->
                        navController.navigate(Screen.Details.createRoute(id))
                    }
                )
            }
            
            composable(
                route = Screen.Details.route,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) {
                val viewModel: DetailsViewModel = hiltViewModel()
                DetailsScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
        
        composable(Screen.Search.route) {
            val viewModel: SearchViewModel = hiltViewModel()
            SearchScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onFundClick = { id ->
                    navController.navigate(Screen.Details.createRoute(id))
                }
            )
        }
    }
}