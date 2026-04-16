package com.example.groww.presentation.details

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.groww.domain.model.FundDetails
import com.example.groww.presentation.common.*
import com.example.groww.ui.theme.PrimaryGreen
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.items

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    viewModel: DetailsViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Fund Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                },
                actions = {
                    IconButton(onClick = { viewModel.onBookmarkClick() }) {
                        Icon(
                            imageVector = if (uiState.isBookmarked) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = "Bookmark",
                            tint = if (uiState.isBookmarked) PrimaryGreen else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (uiState.isLoading && uiState.details == null) {
                LoadingState()
            } else if (uiState.error != null && uiState.details == null) {
                ErrorState(message = uiState.error!!, onRetry = { viewModel.retry() })
            } else {
                uiState.details?.let { fund ->
                    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
                        item { FundHeader(fund) }
                        item { Spacer(modifier = Modifier.height(24.dp)) }
                        item { LineChart(navPoints = fund.navHistory, modifier = Modifier.fillMaxWidth().height(250.dp)) }
                        item { Spacer(modifier = Modifier.height(24.dp)) }
                        item { StatsSection(fund) }
                    }
                }
            }
        }

        if (uiState.showWatchlistDialog) {
            WatchlistSelectionDialog(
                watchlists = uiState.availableWatchlists,
                onWatchlistSelected = { viewModel.addToWatchlist(it) },
                onDismiss = { viewModel.onDismissDialog() }
            )
        }
    }
}

@Composable
fun FundHeader(fund: FundDetails) {
    Column {
        Text(text = fund.category.uppercase(), style = MaterialTheme.typography.labelMedium, color = PrimaryGreen, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = fund.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(text = "₹${fund.latestNav}", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "NAV", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 8.dp))
        }
    }
}

@Composable
fun StatsSection(fund: FundDetails) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Text(text = "Fund Information", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            StatRow("Category", fund.category)
            Divider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outlineVariant)
            StatRow("Scheme Code", fund.id.toString())
        }
    }
}

@Composable
fun StatRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun WatchlistSelectionDialog(
    watchlists: List<com.example.groww.domain.model.Watchlist>,
    onWatchlistSelected: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add to Portfolio") },
        text = {
            if (watchlists.isEmpty()) {
                Text("No portfolios found. Please create one in the Watchlist tab.")
            } else {
                LazyColumn {
                    items(watchlists) { watchlist ->
                        ListItem(
                            headlineContent = { Text(watchlist.name, fontWeight = FontWeight.Bold) },
                            supportingContent = { Text("${watchlist.funds.size} funds") },
                            modifier = Modifier.background(Color.Transparent).clickable { onWatchlistSelected(watchlist.id) }
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
