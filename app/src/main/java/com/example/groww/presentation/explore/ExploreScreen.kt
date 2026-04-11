package com.example.groww.presentation.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.groww.domain.model.FundWithNav

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    viewModel: ExploreViewModel,
    onViewAllClick: (String) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Explore Mutual Funds", 
                        fontWeight = FontWeight.Bold 
                    ) 
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
                is ExploreUiState.Error -> {
                    ErrorState(resource.message) { viewModel.loadExploreData() }
                }
                is ExploreUiState.Empty -> {
                    Text("No funds found", modifier = Modifier.align(Alignment.Center))
                }
                is ExploreUiState.Success -> {
                    ExploreContent(
                        categories = resource.categories,
                        onViewAllClick = onViewAllClick
                    )
                }
            }
        }
    }
}

@Composable
fun ExploreContent(
    categories: Map<String, List<FundWithNav>>,
    onViewAllClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        items(categories.entries.toList()) { entry ->
            CategorySection(
                title = entry.key,
                funds = entry.value,
                onViewAll = { onViewAllClick(entry.key) }
            )
        }
    }
}

@Composable
fun CategorySection(
    title: String,
    funds: List<FundWithNav>,
    onViewAll: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                title, 
                fontSize = 18.sp, 
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = onViewAll) {
                Text("View All")
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))

        // Simulated 2-column Grid (Max 4 items)
        val chunkedFunds = funds.chunked(2)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            chunkedFunds.forEach { rowFunds ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowFunds.forEach { fundWithNav ->
                        FundGridCard(
                            modifier = Modifier.weight(1f),
                            fundWithNav = fundWithNav
                        )
                    }
                    if (rowFunds.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun FundGridCard(
    modifier: Modifier,
    fundWithNav: FundWithNav
) {
    Card(
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = fundWithNav.fund.schemeName,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )
            
            Column {
                Text(
                    "NAV", 
                    fontSize = 10.sp, 
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = fundWithNav.latestNav?.let { "₹$it" } ?: "---",
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
