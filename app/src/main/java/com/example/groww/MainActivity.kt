package com.example.groww

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.groww.presentation.explore.ExploreScreen
import com.example.groww.presentation.explore.ExploreViewModel
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
                    val viewModel: ExploreViewModel = hiltViewModel()
                    ExploreScreen(
                        viewModel = viewModel,
                        onViewAllClick = { category ->
                            // TODO: Navigate to Category Detail Screen
                        }
                    )
                }
            }
        }
    }
}