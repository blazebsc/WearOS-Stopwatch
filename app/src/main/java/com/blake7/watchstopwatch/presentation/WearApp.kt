package com.blake7.watchstopwatch.presentation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.ui.graphics.graphicsLayer
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
// Icons
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay

@Composable
fun WearApp(viewModel: StopwatchViewModel) {
    val stopwatchState = viewModel.stopwatchState

    WatchStopWatchTheme {
        Scaffold(
            timeText = {  TimeText() },
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
                        if (dragAmount.y < -30 && !showLaps) {
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

         Crossfade(targetState = showLaps, animationSpec = tween(200)) { isShowingLaps ->
            if (isShowingLaps) {
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
                            modifier = Modifier.padding(vertical = 10.dp)
                        )

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            verticalArrangement = Arrangement.spacedBy(3.dp),
                            reverseLayout = true
                        ) {
                            itemsIndexed(lapTimes, key = { _, lapTime -> lapTime }) { index, lapTime ->
                                val lapDuration = if (index == 0) {
                                    lapTime
                                } else {
                                    lapTime - lapTimes[index - 1]
                                }

                                LapRow(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    lapNumber = index + 1,
                                    lapTime = viewModel.formatTime(lapDuration),
                                    totalTime = viewModel.formatTime(lapTime),
                                    isLatest = index == lapTimes.size - 1
                                )
                            }
                        }

                        Text(
                            text = "Swipe down to return",
                            fontSize = 9.sp,
                            color = MaterialTheme.colors.onBackground.copy(alpha = 0.6f),
                            modifier = Modifier.padding(top = 4.dp, bottom = 10.dp)
                        )
                    }
                }
            } else {
                // Main stopwatch view
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Time and current lap
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                        val pulse by infiniteTransition.animateFloat(
                            initialValue = 1f,
                            targetValue = 1.01f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1500, easing = FastOutSlowInEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "timerPulse"
                        )

                        Text(
                            text = currentTime,
                            fontSize = 32.sp, // constant now that we always show ss.cc
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colors.onBackground,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .graphicsLayer {
                                    scaleX = if (isRunning) pulse else 1f
                                    scaleY = if (isRunning) pulse else 1f
                                },
                            maxLines = 1,
                            softWrap = false
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

                    AnimatedContent(
                        targetState = isRunning,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(200, easing = LinearOutSlowInEasing)) togetherWith
                                    fadeOut(animationSpec = tween(200, easing = FastOutLinearInEasing))
                        },
                        label = "button-transition"
                    ) { running ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            if (running) {
                                AnimatedButton(
                                    onClick = onStopClick,
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = Color(0xFFFF0000), // pure red
                                        contentColor = Color.White
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Pause,
                                        contentDescription = "Pause",
                                        tint = Color.White
                                    )
                                }

                                AnimatedButton(
                                    onClick = onLapClick,
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = MaterialTheme.colors.secondary
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Flag,
                                        contentDescription = "Lap",
                                        tint = MaterialTheme.colors.onSecondary
                                    )
                                }
                            } else {
                                AnimatedButton(
                                    onClick = onStartClick,
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = if (currentTimeMillis > 0) Color(0xFF388E3C) else Color(0xFF4CAF50),
                                        contentColor = Color.White
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.PlayArrow,
                                        contentDescription = "Start",
                                        tint = Color.White
                                    )
                                }

                                AnimatedButton(
                                    onClick = onResetClick,
                                    enabled = currentTimeMillis > 0,
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = MaterialTheme.colors.surface,
                                        disabledBackgroundColor = MaterialTheme.colors.surface.copy(alpha = 0.3f)
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Replay,
                                        contentDescription = "Reset",
                                        tint = if (currentTimeMillis > 0) MaterialTheme.colors.onSurface
                                               else MaterialTheme.colors.onSurface.copy(alpha = 0.3f)
                                    )
                                }
                            }
                        }
                    }

                    // Always-visible swipe hint under buttons
                    Text(
                        text = "↑ Swipe up for laps (${lapTimes.size})",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colors.onBackground,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AnimatedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.primaryButtonColors(),
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "buttonScale"
    )

    Button(
        onClick = onClick,
        modifier = modifier
            .size(48.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        enabled = enabled,
        colors = colors,
        interactionSource = interactionSource,
        content = content
    )
}

@Composable
fun LapRow(
    modifier: Modifier = Modifier,
    lapNumber: Int,
    lapTime: String,
    totalTime: String,
    isLatest: Boolean
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colors.surface.copy(alpha = 0.1f))
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Lap  $lapNumber",
                fontSize = 11.sp,
                color = if (isLatest) MaterialTheme.colors.primary else MaterialTheme.colors.onBackground.copy(alpha = 0.8f),
                fontWeight = if (isLatest) FontWeight.Bold else FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = lapTime,
                fontSize = 11.sp,
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