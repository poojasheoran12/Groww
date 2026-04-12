package com.example.groww.presentation.explore

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.groww.domain.model.Fund

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    viewModel: ExploreViewModel,
    onViewAllClick: (String) -> Unit,
    onFundClick: (Int) -> Unit,
    onSearchClick: () -> Unit
) {
    val exploreData by viewModel.exploreState.collectAsState()
    val isLoading by viewModel.loadingState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mutual Funds") },
                actions = {
                    IconButton(onClick = onSearchClick) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                exploreData.forEach { (category, funds) ->
                    item {
                        CategoryHeader(
                            title = category,
                            onViewAllClick = { onViewAllClick(category) }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        CategoryGrid(
                            funds = funds,
                            onFundClick = onFundClick
                        )
                    }
                }
            }

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
fun CategoryHeader(
    title: String,
    onViewAllClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        TextButton(onClick = onViewAllClick) {
            Text("View All")
        }
    }
}

@Composable
fun CategoryGrid(
    funds: List<Fund>,
    onFundClick: (Int) -> Unit
) {
    // Only 4 items as per requirement
    val displayFunds = funds.take(4)
    
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        displayFunds.chunked(2).forEach { rowFunds ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                rowFunds.forEach { fund ->
                    FundCard(
                        fund = fund,
                        onClick = { onFundClick(fund.id) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowFunds.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun FundCard(
    fund: Fund,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = fund.name,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = fund.latestNav?.let { "NAV: $it" } ?: "Fetching...",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
