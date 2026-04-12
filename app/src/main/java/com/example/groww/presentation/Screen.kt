package com.example.groww.presentation

sealed class Screen(val route: String) {
    object Explore : Screen("explore")
    object ViewAll : Screen("view_all/{category}") {
        fun createRoute(category: String) = "view_all/$category"
    }
    object Details : Screen("details/{id}") {
        fun createRoute(id: Int) = "details/$id"
    }
    object Search : Screen("search")
}
