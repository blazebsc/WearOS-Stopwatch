package com.blake7.watchstopwatch.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
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

    // Format time as ss.cc (seconds.centiseconds), always length 5 like "00.00"
    fun formatTime(timeMillis: Long): String {
        val totalSeconds = TimeUnit.MILLISECONDS.toSeconds(timeMillis)
        val seconds = (totalSeconds % 60)
        val centiseconds = (timeMillis % 1000) / 10
        return String.format(Locale.US, "%02d.%02d", seconds, centiseconds)
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