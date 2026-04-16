package com.example.groww.presentation.watchlist

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.groww.domain.model.Watchlist
import com.example.groww.presentation.common.EmptyState
import com.example.groww.presentation.common.LoadingState
import com.example.groww.ui.theme.PrimaryGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchlistScreen(
    viewModel: WatchlistViewModel,
    onWatchlistClick: (Long) -> Unit,
    onExploreClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("My Portfolios", fontWeight = FontWeight.Bold) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onAddClick() },
                containerColor = PrimaryGreen,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Watchlist")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (uiState.isLoading) {
                LoadingState()
            } else if (uiState.watchlists.isEmpty()) {
                EmptyState(
                    title = "No portfolios yet",
                    description = "Create custom portfolios to track your favorite mutual funds separately.",
                    actionText = "Explore Funds",
                    onAction = onExploreClick
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.watchlists, key = { it.id }) { watchlist ->
                        WatchlistItem(
                            watchlist = watchlist,
                            onClick = { onWatchlistClick(watchlist.id) },
                            onDelete = { viewModel.deleteWatchlist(watchlist.id) }
                        )
                    }
                }
            }
        }

        if (uiState.showAddDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.onDismissDialog() },
                title = { Text("New Portfolio") },
                text = {
                    TextField(
                        value = uiState.newWatchlistName,
                        onValueChange = { viewModel.onNameChange(it) },
                        placeholder = { Text("e.g. Retirement") },
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    TextButton(onClick = { viewModel.createWatchlist() }) {
                        Text("Add", color = PrimaryGreen, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.onDismissDialog() }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun WatchlistItem(
    watchlist: Watchlist,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = watchlist.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${watchlist.funds.size} funds",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row {
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                    )
                }
                Icon(
                    Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
