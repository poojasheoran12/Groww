package com.example.groww.presentation.details

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.groww.domain.model.NavPoint
import kotlin.math.roundToInt

@Composable
fun LineChart(
    navPoints: List<NavPoint>,
    modifier: Modifier = Modifier
) {
    if (navPoints.isEmpty()) return

    val textMeasurer = rememberTextMeasurer()
    val maxNav = navPoints.maxOf { it.nav }
    val minNav = navPoints.minOf { it.nav }
    val range = (maxNav - minNav).coerceAtLeast(0.1)

    // Interactive State
    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    var touchX by remember { mutableStateOf<Float?>(null) }

    val primaryGreen = Color(0xFF00D09C)
    val onSurface = MaterialTheme.colorScheme.onSurface
    val outlineVariant = MaterialTheme.colorScheme.outlineVariant

    Box(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(navPoints) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            val width = size.width - 60.dp.toPx()
                            val spacing = width / (navPoints.size - 1)
                            selectedIndex = (offset.x / spacing).roundToInt().coerceIn(0, navPoints.size - 1)
                            touchX = offset.x
                        },
                        onDrag = { change, _ ->
                            val width = size.width - 60.dp.toPx()
                            val spacing = width / (navPoints.size - 1)
                            selectedIndex = (change.position.x / spacing).roundToInt().coerceIn(0, navPoints.size - 1)
                            touchX = change.position.x
                        },
                        onDragEnd = { selectedIndex = null; touchX = null },
                        onDragCancel = { selectedIndex = null; touchX = null }
                    )
                }
                .pointerInput(navPoints) {
                    detectTapGestures { offset ->
                        val width = size.width - 60.dp.toPx()
                        val spacing = width / (navPoints.size - 1)
                        selectedIndex = (offset.x / spacing).roundToInt().coerceIn(0, navPoints.size - 1)
                        touchX = offset.x
                    }
                }
        ) {
            val axisPaddingLeft = 0f
            val axisPaddingRight = 60.dp.toPx()
            val axisPaddingBottom = 30.dp.toPx()
            
            val chartWidth = size.width - axisPaddingRight
            val chartHeight = size.height - axisPaddingBottom
            val spacing = chartWidth / (navPoints.size - 1)

            val points = navPoints.mapIndexed { index, point ->
                Offset(
                    x = index * spacing,
                    y = chartHeight - ((point.nav - minNav) / range * chartHeight).toFloat()
                )
            }

            // 1. Draw Grid Lines & Y-Axis Labels
            val gridLines = 4
            for (i in 0..gridLines) {
                val y = chartHeight - (i * chartHeight / gridLines)
                val navValue = minNav + (i * range / gridLines)
                
                // Horizontal Grid Line
                drawLine(
                    color = outlineVariant.copy(alpha = 0.3f),
                    start = Offset(0f, y),
                    end = Offset(chartWidth, y),
                    strokeWidth = 1.dp.toPx()
                )

                // Y-Axis Labels (Right)
                drawText(
                    textMeasurer = textMeasurer,
                    text = String.format("%.1f", navValue),
                    topLeft = Offset(chartWidth + 8.dp.toPx(), y - 10.dp.toPx()),
                    style = TextStyle(color = onSurface.copy(alpha = 0.6f), fontSize = 10.sp)
                )
            }

            // 2. Draw Gradient Fill
            val fillPath = Path().apply {
                moveTo(0f, chartHeight)
                points.forEachIndexed { index, point ->
                    if (index == 0) lineTo(point.x, point.y)
                    else {
                        val prevPoint = points[index - 1]
                        cubicTo(
                            (prevPoint.x + point.x) / 2, prevPoint.y,
                            (prevPoint.x + point.x) / 2, point.y,
                            point.x, point.y
                        )
                    }
                }
                lineTo(chartWidth, chartHeight)
                close()
            }
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(primaryGreen.copy(alpha = 0.3f), Color.Transparent),
                    startY = 0f,
                    endY = chartHeight
                )
            )

            // 3. Draw Smooth Curve
            val strokePath = Path().apply {
                points.forEachIndexed { index, point ->
                    if (index == 0) moveTo(point.x, point.y)
                    else {
                        val prevPoint = points[index - 1]
                        cubicTo(
                            (prevPoint.x + point.x) / 2, prevPoint.y,
                            (prevPoint.x + point.x) / 2, point.y,
                            point.x, point.y
                        )
                    }
                }
            }
            drawPath(
                path = strokePath,
                color = primaryGreen,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            // 4. Draw X-Axis Labels (Dates)
            val labelCount = 3
            val labelIndices = listOf(0, navPoints.size / 2, navPoints.size - 1)
            labelIndices.forEach { index ->
                val point = points[index]
                val date = navPoints[index].date
                drawText(
                    textMeasurer = textMeasurer,
                    text = date,
                    topLeft = Offset(point.x - 20.dp.toPx(), chartHeight + 8.dp.toPx()),
                    style = TextStyle(color = onSurface.copy(alpha = 0.6f), fontSize = 10.sp)
                )
            }

            // 5. Interaction UI (Selection)
            selectedIndex?.let { index ->
                val point = points[index]
                
                // Vertical selection line
                drawLine(
                    color = primaryGreen.copy(alpha = 0.5f),
                    start = Offset(point.x, 0f),
                    end = Offset(point.x, chartHeight),
                    strokeWidth = 1.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                )

                // Circle on curve
                drawCircle(
                    color = primaryGreen,
                    radius = 6.dp.toPx(),
                    center = point
                )
                drawCircle(
                    color = Color.White,
                    radius = 3.dp.toPx(),
                    center = point
                )
            }
        }

        // TOOLTIP (Floating Overlay)
        selectedIndex?.let { index ->
            val point = navPoints[index]
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 8.dp)
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                    .border(1.dp, primaryGreen.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "₹${point.nav}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = primaryGreen
                    )
                    Text(
                        text = point.date,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
