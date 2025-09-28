package com.blake7.watchstopwatch.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.Colors
import androidx.wear.compose.material.MaterialTheme

// Define custom colors for the stopwatch
private val WatchStopWatchColors = Colors(
    primary = Color(0xFF4CAF50),          // Green for start/active
    primaryVariant = Color(0xFF388E3C),   // Dark green
    secondary = Color(0xFF2196F3),        // Blue for secondary actions
    secondaryVariant = Color(0xFF1976D2), // Dark blue
    background = Color(0xFF000000),       // Black background for AMOLED
    surface = Color(0xFF1E1E1E),          // Dark surface
    error = Color(0xFFD32F2F),            // Red for stop/error
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    onError = Color.White
)

@Composable
fun WatchStopWatchTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = WatchStopWatchColors,
        content = content
    )
}