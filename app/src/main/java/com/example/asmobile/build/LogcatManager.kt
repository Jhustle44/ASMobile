package com.example.asmobile.build

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

class LogcatManager {
    private val _logs = MutableStateFlow<List<String>>(emptyList())
    val logs: StateFlow<List<String>> = _logs

    private var process: Process? = null

    suspend fun startReading() = withContext(Dispatchers.IO) {
        try {
            process = Runtime.getRuntime().exec("logcat")
            val reader = BufferedReader(InputStreamReader(process?.inputStream))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                line?.let { l ->
                    _logs.value = (_logs.value + l).takeLast(1000)
                }
            }
        } catch (e: Exception) {
            _logs.value = _logs.value + "Error reading logcat: ${e.message}"
        }
    }

    fun stopReading() {
        process?.destroy()
        process = null
    }

    fun clear() {
        _logs.value = emptyList()
    }
}
