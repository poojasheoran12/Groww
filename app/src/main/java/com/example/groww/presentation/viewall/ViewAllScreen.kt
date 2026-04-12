package com.example.groww.presentation.viewall

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.groww.domain.model.Fund
import com.example.groww.presentation.explore.ExploreUiState
import com.example.groww.presentation.explore.ExploreViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewAllScreen(
    category: String,
    viewModel: ExploreViewModel,
    onBack: () -> Unit,
    onFundClick: (Int) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(category) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (val resource = state) {
                is ExploreUiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is ExploreUiState.Error -> Text(resource.message, modifier = Modifier.align(Alignment.Center))
                is ExploreUiState.Success -> {
                    val funds = resource.categories[category] ?: emptyList()
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        itemsIndexed(funds, key = { _, fund -> fund.schemeCode }) { _, fund ->
                            // Lazy NAV loading trigger
                            if (fund.latestNav == null) {
                                LaunchedEffect(fund.schemeCode) {
                                    viewModel.onFundVisible(fund.schemeCode)
                                }
                            }

                            FundListItem(fund = fund, onClick = { onFundClick(fund.schemeCode) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FundListItem(fund: Fund, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = fund.schemeName,
                modifier = Modifier.weight(1f),
                maxLines = 2,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = fund.latestNav?.let { "₹$it" } ?: "Fetching...",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
