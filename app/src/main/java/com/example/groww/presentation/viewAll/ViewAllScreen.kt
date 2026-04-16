package com.example.groww.presentation.viewAll

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.groww.domain.model.Fund
import com.example.groww.presentation.common.*
import com.example.groww.ui.theme.PrimaryGreen
import com.example.groww.util.shimmerEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewAllScreen(
    category: String,
    viewModel: ViewAllViewModel,
    onBackClick: () -> Unit,
    onFundClick: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastIndex ->
                if (lastIndex != null && lastIndex >= uiState.visibleFunds.size - 5) {
                    viewModel.loadNextPage()
                }
            }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(category, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (uiState.isLoading && uiState.visibleFunds.isEmpty()) {
                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(10) { Box(modifier = Modifier.fillMaxWidth().height(80.dp).shimmerEffect().clip(RoundedCornerShape(12.dp))) }
                }
            } else if (uiState.error != null && uiState.visibleFunds.isEmpty()) {
                ErrorState(message = uiState.error!!, onRetry = { viewModel.refreshData() })
            } else if (uiState.visibleFunds.isEmpty()) {
                EmptyState(title = "No funds found", description = "Try a different category.", actionText = "Go Back", onAction = onBackClick)
            } else {
                LazyColumn(state = listState, modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(uiState.visibleFunds, key = { it.id }) { fund ->
                        ViewAllFundItem(fund = fund, onAppear = { viewModel.onItemVisible(fund.id) }, onClick = { onFundClick(fund.id) })
                    }
                    if (uiState.isLoadingMore) {
                        item { LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(16.dp), color = PrimaryGreen) }
                    }
                }
            }
        }
    }
}

@Composable
fun ViewAllFundItem(fund: Fund, onAppear: () -> Unit, onClick: () -> Unit) {
    LaunchedEffect(fund.id) { if (fund.latestNav == null) onAppear() }
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = fund.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 2)
                Text(text = fund.category, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(horizontalAlignment = Alignment.End) {
                Text(text = fund.latestNav?.toDoubleOrNull()?.let { "₹%.2f".format(it) } ?: "---", style = MaterialTheme.typography.bodyLarge, color = PrimaryGreen, fontWeight = FontWeight.Bold)
                Text(text = "NAV", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
