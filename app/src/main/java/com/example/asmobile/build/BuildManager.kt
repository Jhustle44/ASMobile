package com.example.asmobile.build

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BuildManager {
    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress

    private val _isBuilding = MutableStateFlow(false)
    val isBuilding: StateFlow<Boolean> = _isBuilding

    private val _status = MutableStateFlow("Idle")
    val status: StateFlow<String> = _status

    suspend fun startBuild() {
        if (_isBuilding.value) return
        _isBuilding.value = true
        _status.value = "Starting build..."
        _progress.value = 0f

        val steps = listOf(
            "Initializing...",
            "Resolving dependencies...",
            "Compiling Java/Kotlin...",
            "Processing resources...",
            "Linking...",
            "Signing APK...",
            "Build successful!"
        )

        for ((index, step) in steps.withIndex()) {
            _status.value = step
            val targetProgress = (index + 1).toFloat() / steps.size
            while (_progress.value < targetProgress) {
                delay(100)
                _progress.value += 0.05f
            }
            _progress.value = targetProgress
            delay(500)
        }

        _isBuilding.value = false
    }
}
