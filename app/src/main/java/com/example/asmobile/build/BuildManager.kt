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

    private suspend fun performLintCheck() {
        _status.value = "Checking for project errors..."
        _progress.value = 0.1f
        delay(1000)
        // Simulate finding 0 errors
        _status.value = "0 Errors found. Code is healthy."
        _progress.value = 0.2f
        delay(500)
    }

    suspend fun startBuild() {
        if (_isBuilding.value) return
        _isBuilding.value = true
        
        performLintCheck()
        
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

    suspend fun startSync() {
        _isBuilding.value = true
        _status.value = "Syncing project with Gradle..."
        _progress.value = 0.2f
        delay(1000)
        _progress.value = 0.8f
        delay(500)
        _progress.value = 1.0f
        _status.value = "Project synced"
        _isBuilding.value = false
    }

    suspend fun startClean() {
        _isBuilding.value = true
        _status.value = "Cleaning project..."
        _progress.value = 0.3f
        delay(800)
        _progress.value = 1.0f
        _status.value = "Project cleaned"
        _isBuilding.value = false
    }
}
