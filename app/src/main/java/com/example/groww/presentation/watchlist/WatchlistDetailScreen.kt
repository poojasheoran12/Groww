package com.example.groww.presentation.watchlist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.groww.presentation.explore.FundCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchlistDetailScreen(
    watchlistId: Long,
    watchlistName: String,
    onBackClick: () -> Unit,
    onFundClick: (Int) -> Unit
) {
    val viewModel: WatchlistDetailViewModel = hiltViewModel()
    val watchlist by viewModel.watchlist.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(watchlistName) },
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
                    EmptyWatchlistState(modifier = Modifier.fillMaxSize())
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
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
            } ?: CircularProgressIndicator()
        }
    }
}
