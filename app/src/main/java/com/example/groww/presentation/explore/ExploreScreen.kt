package com.example.groww.presentation.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.groww.domain.model.Fund

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    viewModel: ExploreViewModel,
    onViewAllClick: (String) -> Unit,
    onSearchClick: () -> Unit,
    onFundClick: (Int) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Explore Mutual Funds", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onSearchClick) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (val resource = state) {
                is ExploreUiState.Loading -> {
                   CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is ExploreUiState.Error -> ErrorState(resource.message) { viewModel.refreshData() }
                is ExploreUiState.Success -> {
                    ExploreContent(
                        categories = resource.categories,
                        onViewAllClick = onViewAllClick,
                        onFundClick = onFundClick
                    )
                }
            }
            
            if (isRefreshing && state is ExploreUiState.Success) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
fun ExploreContent(
    categories: Map<String, List<Fund>>,
    onViewAllClick: (String) -> Unit,
    onFundClick: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        items(categories.entries.toList(), key = { it.key }) { entry ->
            CategorySection(
                title = entry.key,
                funds = entry.value,
                onViewAll = { onViewAllClick(entry.key) },
                onFundClick = onFundClick
            )
        }
    }
}

@Composable
fun CategorySection(
    title: String,
    funds: List<Fund>,
    onViewAll: () -> Unit,
    onFundClick: (Int) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = onViewAll) { Text("View All") }
        }
        Spacer(modifier = Modifier.height(8.dp))
        val chunkedFunds = funds.chunked(2)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            chunkedFunds.forEach { rowFunds ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowFunds.forEach { fund ->
                        FundGridCard(
                            modifier = Modifier.weight(1f),
                            fund = fund,
                            onClick = { onFundClick(fund.schemeCode) }
                        )
                    }
                    if (rowFunds.size == 1) Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun FundGridCard(
    modifier: Modifier,
    fund: Fund,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(110.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp).fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = fund.schemeName,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )
            Column {
                Text("NAV", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    text = fund.latestNav?.let { "₹$it" } ?: "---",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(message, color = MaterialTheme.colorScheme.error)
        Button(onClick = onRetry) { Text("Retry") }
    }
}
