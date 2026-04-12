package com.example.groww.presentation.explore

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.groww.domain.model.Fund
import com.example.groww.domain.model.FundCategory
import com.example.groww.ui.theme.PrimaryGreen
import com.example.groww.util.shimmerEffect

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

    val categories = FundCategory.entries.filter { it != FundCategory.SEARCH && it != FundCategory.ALL }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Search Bar
                item {
                    SearchInput(onClick = onSearchClick)
                }

                // Category Chips
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(end = 16.dp)
                    ) {
                        items(categories) { category ->
                            FilterChip(
                                onClick = { onViewAllClick(category.displayName) },
                                label = { Text(category.displayName) },
                                selected = false,
                                shape = RoundedCornerShape(20.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = false,
                                    borderColor = MaterialTheme.colorScheme.outlineVariant,
                                    selectedBorderColor = PrimaryGreen
                                )
                            )
                        }
                    }
                }

                // Layout with Content or Skeleton
                if (isLoading && exploreData.isEmpty()) {
                    items(3) {
                        SkeletonCategorySection()
                    }
                } else {
                    exploreData.forEach { (category, funds) ->
                        item {
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn() + expandVertically()
                            ) {
                                Column {
                                    SectionHeader(
                                        title = category,
                                        onViewAllClick = { onViewAllClick(category) }
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    CategoryGrid(
                                        funds = funds,
                                        onFundClick = onFundClick
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (isLoading && exploreData.isNotEmpty()) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter),
                    color = PrimaryGreen
                )
            }
        }
    }
}

@Composable
fun SkeletonCategorySection() {
    Column {
        Box(modifier = Modifier.size(120.dp, 24.dp).shimmerEffect().clip(RoundedCornerShape(4.dp)))
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            repeat(2) {
                Box(modifier = Modifier.weight(1f).aspectRatio(1f).shimmerEffect().clip(RoundedCornerShape(12.dp)))
            }
        }
    }
}

@Composable
fun SearchInput(onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Search mutual funds, stocks...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SectionHeader(title: String, onViewAllClick: () -> Unit) {
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
            Text("View All", color = PrimaryGreen, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun CategoryGrid(funds: List<Fund>, onFundClick: (Int) -> Unit) {
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
                if (rowFunds.size == 1) Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun FundCard(fund: Fund, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(170.dp), // Fixed height for uniform grid
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween // Distribute content evenly
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(PrimaryGreen.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = fund.name.take(1),
                    color = PrimaryGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Text(
                text = fund.name,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp,
                modifier = Modifier.weight(1f).padding(top = 8.dp) // Takes up available space
            )

            Column {
                Text(text = "NAV", style = MaterialTheme.typography.labelSmall)
                Text(
                    text = fund.latestNav?.let { "₹$it" } ?: "---",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
