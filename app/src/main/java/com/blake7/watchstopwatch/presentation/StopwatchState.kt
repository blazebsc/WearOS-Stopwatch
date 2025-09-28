package com.blake7.watchstopwatch.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class StopwatchState {
    var isRunning by mutableStateOf(false)
    var currentTimeMillis by mutableLongStateOf(0L)
    var lastLapTime by mutableLongStateOf(0L) // Time when last lap was recorded
    val lapTimes = mutableStateListOf<Long>()
    
    fun reset() {
        isRunning = false
        currentTimeMillis = 0L
        lastLapTime = 0L
        lapTimes.clear()
    }
    
    fun addLap(time: Long) {
        lapTimes.add(time)
        lastLapTime = time // Update the last lap time
    }
}