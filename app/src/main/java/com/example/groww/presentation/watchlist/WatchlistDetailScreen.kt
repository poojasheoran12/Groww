package com.example.groww.presentation.watchlist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.*
import com.example.groww.presentation.explore.FundCard
import com.example.groww.ui.theme.PrimaryGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchlistDetailScreen(
    watchlistId: Long,
    watchlistName: String,
    onBackClick: () -> Unit,
    onFundClick: (Int) -> Unit,
    onExploreClick: () -> Unit
) {
    val viewModel: WatchlistDetailViewModel = hiltViewModel()
    val watchlist by viewModel.watchlist.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = watchlistName,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            watchlist?.let { data ->
                if (data.funds.isEmpty()) {
                    EmptyFolderState(
                        onExploreClick = onExploreClick
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(data.funds) { fund ->
                            FundCard(
                                fund = fund,
                                onClick = { onFundClick(fund.id) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            } ?: CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}

@Composable
fun EmptyFolderState(onExploreClick: () -> Unit) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Url("https://lottie.host/7905a5a1-7786-4f9e-abab-327c5b6b1988/d7F8uYtN1l.json")
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(modifier = Modifier.size(240.dp), contentAlignment = Alignment.Center) {
            if (composition == null) {
                CircularProgressIndicator(color = PrimaryGreen.copy(alpha = 0.5f))
            } else {
                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "No funds added yet.",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = "Explore the market to save funds into\nthis portfolio.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            fontStyle = FontStyle.Italic,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 22.sp
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        OutlinedButton(
            onClick = onExploreClick,
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(56.dp),
            shape = MaterialTheme.shapes.medium,
            border = androidx.compose.foundation.BorderStroke(2.dp, Color.Black)
        ) {
            Text(
                text = "Explore Funds",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                color = Color.Black
            )
        }
    }
}
