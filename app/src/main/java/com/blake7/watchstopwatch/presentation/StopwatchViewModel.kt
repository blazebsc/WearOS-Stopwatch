package com.blake7.watchstopwatch.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class StopwatchViewModel : ViewModel() {
    
    private val _stopwatchState = StopwatchState()
    val stopwatchState: StopwatchState
        get() = _stopwatchState

    private var timerJob: Job? = null
    private var startTimeOffset = 0L

    fun start() {
        if (_stopwatchState.isRunning) return

        _stopwatchState.isRunning = true
        val resumeTime = System.currentTimeMillis()
        startTimeOffset = resumeTime - _stopwatchState.currentTimeMillis
        
        timerJob = viewModelScope.launch {
            while (_stopwatchState.isRunning) {
                _stopwatchState.currentTimeMillis = System.currentTimeMillis() - startTimeOffset
                delay(16L) // ~60 FPS updates for smooth animation
            }
        }
    }

    fun stop() {
        _stopwatchState.isRunning = false
        timerJob?.cancel()
    }

    fun reset() {
        stop()
        _stopwatchState.reset()
        startTimeOffset = 0L
    }

    fun lap() {
        if (!_stopwatchState.isRunning) return
        _stopwatchState.addLap(_stopwatchState.currentTimeMillis)
    }

    // Enhanced time formatting for better display
    fun formatTime(timeMillis: Long): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeMillis)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(timeMillis) % 60
        val centiseconds = (timeMillis % 1000) / 10
        
        return when {
            minutes > 0 -> String.format("%02d:%02d.%02d", minutes, seconds, centiseconds)
            else -> String.format("%02d.%02d", seconds, centiseconds)
        }
    }
    
    fun formatLapTime(timeMillis: Long, lapNumber: Int): String {
        return "Lap $lapNumber: ${formatTime(timeMillis)}"
    }
    
    // Get the time since the last lap (or total time if no laps)
    fun getCurrentLapTime(): Long {
        return _stopwatchState.currentTimeMillis - _stopwatchState.lastLapTime
    }
    
    // Format the current lap time for display
    fun formatCurrentLapTime(): String {
        return formatTime(getCurrentLapTime())
    }
}