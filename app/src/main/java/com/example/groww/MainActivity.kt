package com.example.groww

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.groww.presentation.navigation.GrowwNavGraph
import com.example.groww.ui.theme.GrowwTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeViewModel: com.example.groww.presentation.ThemeViewModel = androidx.hilt.navigation.compose.hiltViewModel()
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsState(initial = androidx.compose.foundation.isSystemInDarkTheme())
            
            GrowwTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    com.example.groww.presentation.navigation.GrowwNavGraph(navController = navController)
                }
            }
        }
    }
}