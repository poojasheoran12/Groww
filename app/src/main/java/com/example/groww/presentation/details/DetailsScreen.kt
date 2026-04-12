package com.example.groww.presentation.details

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.groww.domain.model.FundDetails
import com.example.groww.domain.model.NavPoint
import com.example.groww.presentation.common.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    viewModel: DetailsViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.detailsState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Fund Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                SuggestionChip(onClick = {}, label = { Text(fund.category) })
                                SuggestionChip(onClick = {}, label = { Text(fund.type) })
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
                            Spacer(modifier = Modifier.height(8.dp))
                            LineChart(
                                navPoints = fund.navHistory,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .padding(vertical = 16.dp)
                            )
                        }
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
fun LineChart(
    navPoints: List<NavPoint>,
    modifier: Modifier = Modifier
) {
    if (navPoints.isEmpty()) return

    val maxNav = navPoints.maxOf { it.nav }
    val minNav = navPoints.minOf { it.nav }
    val range = (maxNav - minNav).coerceAtLeast(0.1)

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val spacing = width / (navPoints.size - 1)

        val path = Path().apply {
            navPoints.forEachIndexed { index, point ->
                val x = index * spacing
                val y = height - ((point.nav - minNav) / range * height).toFloat()
                if (index == 0) moveTo(x, y) else lineTo(x, y)
            }
        }

        drawPath(
            path = path,
            color = Color(0xFF00D09C), // Groww Green
            style = Stroke(width = 3.dp.toPx())
        )
    }
}
