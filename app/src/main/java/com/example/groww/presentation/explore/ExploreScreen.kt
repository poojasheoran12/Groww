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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.groww.domain.model.Fund
import com.example.groww.util.Resource

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
                title = { Text("Explore", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val resource = state) {
                is Resource.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is Resource.Error -> {
                    ErrorContent(message = resource.message, onRetry = { viewModel.fetchExploreData() })
                }
                is Resource.Success -> {
                    if (resource.data.isEmpty()) {
                        EmptyContent()
                    } else {
                        ExploreList(
                            categories = resource.data,
                            onViewAllClick = onViewAllClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExploreList(
    categories: Map<String, List<Fund>>,
    onViewAllClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        items(categories.keys.toList()) { categoryName ->
            CategorySection(
                title = categoryName,
                funds = categories[categoryName] ?: emptyList(),
                onViewAll = { onViewAllClick(categoryName) }
            )
        }
    }
}

@Composable
fun CategorySection(
    title: String,
    funds: List<Fund>,
    onViewAll: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = onViewAll) {
                Text("View All", color = MaterialTheme.colorScheme.primary)
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))

        // Grid (2 columns, take 4)
        val visibleFunds = funds.take(4)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            for (i in 0 until (visibleFunds.size + 1) / 2) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val firstIndex = i * 2
                    val secondIndex = firstIndex + 1
                    
                    FundCard(
                        modifier = Modifier.weight(1f),
                        fund = visibleFunds[firstIndex]
                    )
                    
                    if (secondIndex < visibleFunds.size) {
                        FundCard(
                            modifier = Modifier.weight(1f),
                            fund = visibleFunds[secondIndex]
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun FundCard(
    modifier: Modifier = Modifier,
    fund: Fund
) {
    Card(
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = fund.schemeName,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Column {
                Text(
                    text = "NAV",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                when (val nav = fund.navState) {
                    is NavState.Loading -> {
                        LinearProgressIndicator(
                            modifier = Modifier.width(40.dp).height(2.dp).padding(top = 4.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    is NavState.Success -> {
                        Text(
                            text = "₹${nav.nav}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    else -> {
                        Text("---", fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ErrorContent(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(message, color = MaterialTheme.colorScheme.error)
        Button(onClick = onRetry) { Text("Retry") }
    }
}

@Composable
fun EmptyContent() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("No funds found.")
    }
}
