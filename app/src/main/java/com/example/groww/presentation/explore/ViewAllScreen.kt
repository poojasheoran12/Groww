package com.example.groww.presentation.explore

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.groww.domain.model.Fund

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewAllScreen(
    category: String,
    viewModel: ViewAllViewModel,
    onBackClick: () -> Unit,
    onFundClick: (Int) -> Unit
) {
    val uiState by viewModel.state.collectAsState()
    val listState = rememberLazyListState()

    // Pagination detection
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastIndex ->
                if (lastIndex != null && lastIndex >= uiState.visibleFunds.size - 5) {
                    viewModel.loadNextPage()
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(category) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.visibleFunds, key = { it.id }) { fund ->
                    ViewAllFundItem(
                        fund = fund,
                        onAppear = { viewModel.onItemVisible(fund.id) },
                        onClick = { onFundClick(fund.id) }
                    )
                }

                if (uiState.isLoadingMore) {
                    item {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .wrapContentWidth(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ViewAllFundItem(
    fund: Fund,
    onAppear: () -> Unit,
    onClick: () -> Unit
) {
    // Only fetch if missing
    LaunchedEffect(fund.id) {
        if (fund.latestNav == null) {
            onAppear()
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = fund.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = fund.latestNav?.let { "₹$it" } ?: "Fetching...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
