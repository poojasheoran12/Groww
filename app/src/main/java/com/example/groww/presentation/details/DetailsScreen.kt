package com.example.groww.presentation.details

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.groww.domain.model.FundDetails
import com.example.groww.domain.model.NavPoint
import com.example.groww.presentation.common.UiState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    viewModel: DetailsViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.detailsState.collectAsState()
    val isFavorite by viewModel.isInCategoryWatchlist.collectAsState()
    val allWatchlists by viewModel.allWatchlists.collectAsState()
    val activeWatchlistIds by viewModel.activeWatchlistIds.collectAsState()
    
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Fund Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showBottomSheet = true }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Watchlist",
                            tint = if (isFavorite) Color.Red else LocalContentColor.current
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val currentState = state) {
                is UiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is UiState.Error -> {
                    Text(
                        text = currentState.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is UiState.Success -> {
                    val fund = currentState.data
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Text(text = fund.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                            Text(text = fund.fundHouse, style = MaterialTheme.typography.bodyLarge)
                            Spacer(modifier = Modifier.height(8.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                SuggestionChip(
                                    onClick = {}, 
                                    label = { Text(fund.category) },
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                SuggestionChip(
                                    onClick = {}, 
                                    label = { Text(fund.type) }
                                )
                            }
                        }

                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(text = "Latest NAV", style = MaterialTheme.typography.labelLarge)
                                    Text(text = "₹${fund.latestNav}", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        item {
                            Text(text = "NAV History", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(16.dp))
                            LineChart(
                                navPoints = fund.navHistory,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(250.dp) // Slightly taller for axis labels
                                    .padding(vertical = 16.dp)
                            )
                        }
                    }
                }
                else -> {}
            }
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState
            ) {
                WatchlistBottomSheetContent(
                    allWatchlists = allWatchlists,
                    activeIds = activeWatchlistIds,
                    onToggle = { id, isSelected -> viewModel.toggleWatchlist(id, isSelected) },
                    onCreateNew = { viewModel.createAndAddToWatchlist(it) }
                )
            }
        }
    }
}

@Composable
fun WatchlistBottomSheetContent(
    allWatchlists: List<com.example.groww.domain.model.Watchlist>,
    activeIds: List<Long>,
    onToggle: (Long, Boolean) -> Unit,
    onCreateNew: (String) -> Unit
) {
    var newWatchlistName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .padding(bottom = 32.dp)
    ) {
        Text(text = "Add to Portfolio", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = newWatchlistName,
                onValueChange = { newWatchlistName = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("New Portfolio Name...") },
                singleLine = true
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (newWatchlistName.isNotBlank()) {
                        onCreateNew(newWatchlistName)
                        newWatchlistName = ""
                    }
                },
                enabled = newWatchlistName.isNotBlank()
            ) {
                Text("Add")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        allWatchlists.forEach { watchlist ->
            val isSelected = activeIds.contains(watchlist.id)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle(watchlist.id, !isSelected) }
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(checked = isSelected, onCheckedChange = { onToggle(watchlist.id, it) })
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = watchlist.name, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}
