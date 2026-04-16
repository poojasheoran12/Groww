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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    viewModel: DetailsViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val isBookmarked = uiState.fundWatchlistIds.isNotEmpty()

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
                            imageVector = if (isBookmarked) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = "Bookmark",
                            tint = if (isBookmarked) PrimaryGreen else MaterialTheme.colorScheme.onSurfaceVariant
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
            WatchlistSelectionSheet(
                watchlists = uiState.availableWatchlists,
                selectedIds = uiState.fundWatchlistIds,
                onToggleWatchlist = { viewModel.toggleWatchlist(it) },
                onCreateNew = { viewModel.createNewWatchlist(it) },
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
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outlineVariant)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchlistSelectionSheet(
    watchlists: List<com.example.groww.domain.model.Watchlist>,
    selectedIds: List<Long>,
    onToggleWatchlist: (Long) -> Unit,
    onCreateNew: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var newWatchlistName by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 48.dp)
        ) {
            Text(
                text = "Save to Portfolio",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // New Portfolio Input field (Always visible)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = newWatchlistName,
                    onValueChange = { newWatchlistName = it },
                    placeholder = { Text("Create new portfolio...") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryGreen,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    )
                )
                Spacer(modifier = Modifier.width(12.dp))
                IconButton(
                    onClick = { 
                        if (newWatchlistName.isNotBlank()) {
                            onCreateNew(newWatchlistName)
                            newWatchlistName = ""
                        }
                    },
                    enabled = newWatchlistName.isNotBlank(),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = if (newWatchlistName.isNotBlank()) PrimaryGreen else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.size(52.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Create")
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = MaterialTheme.colorScheme.outlineVariant)

            // Existing Portfolios List
            LazyColumn(
                modifier = Modifier.heightIn(max = 400.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (watchlists.isEmpty()) {
                    item {
                        Text(
                            text = "No existing portfolios",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }
                } else {
                    items(watchlists) { watchlist ->
                        val isSelected = selectedIds.contains(watchlist.id)
                        ListItem(
                            headlineContent = { Text(watchlist.name, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface) },
                            supportingContent = { Text("${watchlist.funds.size} funds", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                            trailingContent = {
                                Checkbox(
                                    checked = isSelected,
                                    onCheckedChange = { onToggleWatchlist(watchlist.id) },
                                    colors = CheckboxDefaults.colors(checkedColor = PrimaryGreen)
                                )
                            },
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onToggleWatchlist(watchlist.id) }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
            ) {
                Text("Done", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
