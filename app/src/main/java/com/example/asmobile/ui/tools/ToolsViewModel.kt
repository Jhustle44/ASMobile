package com.example.asmobile.ui.tools

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.asmobile.build.BuildManager
import com.example.asmobile.build.LogcatManager
import com.example.asmobile.git.GitManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

class GitViewModel(rootDir: File) : ViewModel() {
    private val gitManager = GitManager(rootDir)
    
    private val _statusMessage = MutableStateFlow("Ready")
    val statusMessage: StateFlow<String> = _statusMessage

    fun clone(url: String) {
        viewModelScope.launch {
            _statusMessage.value = "Cloning..."
            val result = gitManager.clone(url, "cloned_repo")
            _statusMessage.value = if (result.isSuccess) "Clone successful" else "Error: ${result.exceptionOrNull()?.message}"
        }
    }

    fun commit(repoPath: String, message: String) {
        viewModelScope.launch {
            _statusMessage.value = "Committing..."
            val result = gitManager.commit(repoPath, message)
            _statusMessage.value = if (result.isSuccess) "Commit successful" else "Error: ${result.exceptionOrNull()?.message}"
        }
    }

    fun push(repoPath: String) {
        viewModelScope.launch {
            _statusMessage.value = "Pushing..."
            val result = gitManager.push(repoPath)
            _statusMessage.value = if (result.isSuccess) "Push successful" else "Error: ${result.exceptionOrNull()?.message}"
        }
    }

    fun pull(repoPath: String) {
        viewModelScope.launch {
            _statusMessage.value = "Pulling..."
            val result = gitManager.pull(repoPath)
            _statusMessage.value = if (result.isSuccess) "Pull successful" else "Error: ${result.exceptionOrNull()?.message}"
        }
    }
}

class BuildLogViewModel : ViewModel() {
    val logcatManager = LogcatManager()
    val buildManager = BuildManager()

    val logs = logcatManager.logs
    val buildProgress = buildManager.progress
    val buildStatus = buildManager.status
    val isBuilding = buildManager.isBuilding

    fun startLogcat() {
        viewModelScope.launch {
            logcatManager.startReading()
        }
    }

    fun stopLogcat() {
        logcatManager.stopReading()
    }

    fun clearLogs() {
        logcatManager.clear()
    }

    fun startBuild() {
        viewModelScope.launch {
            buildManager.startBuild()
        }
    }

    override fun onCleared() {
        super.onCleared()
        logcatManager.stopReading()
    }
}
