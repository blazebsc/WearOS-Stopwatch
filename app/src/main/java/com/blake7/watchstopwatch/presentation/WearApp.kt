package com.blake7.watchstopwatch.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.min
import kotlin.math.abs
import androidx.wear.compose.material.*
import androidx.wear.tooling.preview.devices.WearDevices
import com.blake7.watchstopwatch.presentation.theme.WatchStopWatchTheme

@Composable
fun WearApp(viewModel: StopwatchViewModel) {
    val stopwatchState = viewModel.stopwatchState

    WatchStopWatchTheme {
        Scaffold(
            timeText = { TimeText() },
            vignette = { Vignette(vignettePosition = VignettePosition.TopAndBottom) }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background)
            ) {
                StopwatchScreen(
                    currentTime = viewModel.formatTime(stopwatchState.currentTimeMillis),
                    currentTimeMillis = stopwatchState.currentTimeMillis,
                    isRunning = stopwatchState.isRunning,
                    lapTimes = stopwatchState.lapTimes,
                    lastLapTime = stopwatchState.lastLapTime,
                    onStartClick = { viewModel.start() },
                    onStopClick = { viewModel.stop() },
                    onResetClick = { viewModel.reset() },
                    onLapClick = { viewModel.lap() },
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
fun StopwatchScreen(
    currentTime: String,
    currentTimeMillis: Long,
    isRunning: Boolean,
    lapTimes: List<Long>,
    lastLapTime: Long,
    onStartClick: () -> Unit,
    onStopClick: () -> Unit,
    onResetClick: () -> Unit,
    onLapClick: () -> Unit,
    viewModel: StopwatchViewModel
) {
    var showLaps by remember { mutableStateOf(false) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures { _, dragAmount ->
                    // Vertical swipe gestures
                    if (abs(dragAmount.y) > abs(dragAmount.x) && abs(dragAmount.y) > 15) {
                        // Swipe up to show laps
                        if (dragAmount.y < -30 && lapTimes.isNotEmpty() && !showLaps) {
                            showLaps = true
                        }
                        // Swipe down to close laps
                        else if (dragAmount.y > 30 && showLaps) {
                            showLaps = false
                        }
                    }
                }
            }
    ) {
        // Always show the circular progress (background layer)
        val timeSinceLastLap = currentTimeMillis - lastLapTime
        val secondsProgress = ((timeSinceLastLap % 60000) / 60000f)
        
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = (min(size.width, size.height) / 2f) - 8.dp.toPx()
            val strokeWidth = 6.dp.toPx()
            
            drawCircle(
                color = Color.White.copy(alpha = 0.15f),
                radius = radius,
                center = center,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            
            if (secondsProgress > 0) {
                drawArc(
                    color = if (isRunning) Color(0xFF4CAF50) else Color(0xFF2196F3),
                    startAngle = -90f,
                    sweepAngle = secondsProgress * 360f,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }
        }
        
        if (showLaps && lapTimes.isNotEmpty()) {
            // Lap view with background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background.copy(alpha = 0.95f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "All Laps (${lapTimes.size})",
                        style = MaterialTheme.typography.title3,
                        color = MaterialTheme.colors.primary,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(3.dp),
                        reverseLayout = true
                    ) {
                        itemsIndexed(lapTimes) { index, lapTime ->
                            val lapDuration = if (index == 0) {
                                lapTime
                            } else {
                                lapTime - lapTimes[index - 1]
                            }
                            LapRow(
                                lapNumber = index + 1,
                                lapTime = viewModel.formatTime(lapDuration),
                                totalTime = viewModel.formatTime(lapTime),
                                isLatest = index == lapTimes.size - 1
                            )
                        }
                    }
                    
                    Text(
                        text = "Swipe Down To Return",
                        fontSize = 9.sp,
                        color = MaterialTheme.colors.onBackground.copy(alpha = 0.6f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        } else {
            // Main stopwatch view
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .offset(y = (-28).dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier.height(40.dp))
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = currentTime,
                        fontSize = if (currentTime.length > 5) 26.sp else 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colors.onBackground,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    if (lapTimes.isNotEmpty()) {
                        Text(
                            text = "Lap ${lapTimes.size + 1}: ${viewModel.formatCurrentLapTime()}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colors.primary.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp)
                        )
                    }
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    if (isRunning) {
                        Button(
                            onClick = onStopClick,
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFD32F2F)),
                            modifier = Modifier.size(48.dp)
                        ) {
                            Text("⏸", fontSize = 16.sp, color = Color.White)
                        }
                        
                        Button(
                            onClick = onLapClick,
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = if (isRunning) MaterialTheme.colors.secondary 
                                                else MaterialTheme.colors.secondary.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier.size(48.dp)
                        ) {
                            Text(
                                "⏱", 
                                fontSize = 14.sp, 
                                color = if (isRunning) MaterialTheme.colors.onSecondary 
                                       else MaterialTheme.colors.onSecondary.copy(alpha = 0.5f)
                            )
                        }
                    } else {
                        Button(
                            onClick = onStartClick,
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = if (currentTimeMillis > 0) Color(0xFF388E3C) else Color(0xFF4CAF50)
                            ),
                            modifier = Modifier.size(48.dp)
                        ) {
                            Text("▶", fontSize = 16.sp, color = Color.White)
                        }
                        
                        Button(
                            onClick = onResetClick,
                            enabled = currentTimeMillis > 0,
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = MaterialTheme.colors.surface,
                                disabledBackgroundColor = MaterialTheme.colors.surface.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier.size(48.dp)
                        ) {
                            Text(
                                "↻",
                                fontSize = 16.sp,
                                color = if (currentTimeMillis > 0) MaterialTheme.colors.onSurface 
                                       else MaterialTheme.colors.onSurface.copy(alpha = 0.3f)
                            )
                        }
                    }
                }
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (lapTimes.isNotEmpty()) {
                        Text(
                            text = "Swipe Up For Laps (${lapTimes.size})",
                            fontSize = 9.sp,
                            color = MaterialTheme.colors.onBackground.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LapRow(
    lapNumber: Int,
    lapTime: String,
    totalTime: String,
    isLatest: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(
                if (isLatest) MaterialTheme.colors.primary.copy(alpha = 0.15f)
                else MaterialTheme.colors.surface.copy(alpha = 0.1f)
            )
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Lap  $lapNumber",
                fontSize = 12.sp,
                color = MaterialTheme.colors.onBackground.copy(alpha = 0.8f),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            
            Text(
                text = lapTime,
                fontSize = 12.sp,
                color = if (isLatest) MaterialTheme.colors.primary else MaterialTheme.colors.onBackground,
                fontWeight = if (isLatest) FontWeight.Bold else FontWeight.Normal,
                textAlign = TextAlign.End,
                modifier = Modifier.weight(1f)
            )
        }
        
        // Show total time in smaller text
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "Total: $totalTime",
                fontSize = 9.sp,
                color = MaterialTheme.colors.onBackground.copy(alpha = 0.5f),
                textAlign = TextAlign.End
            )
        }
    }
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    val previewViewModel = StopwatchViewModel()
    WearApp(previewViewModel)
}