package com.example.asmobile.ui.workspace

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.io.File
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

@Serializable
data class DeviceModel(val name: String, val api: String, val isRunning: Boolean = false)

class DeviceViewModel : ViewModel() {
    private val _devices = mutableStateListOf<DeviceModel>()
    val devices: List<DeviceModel> get() = _devices
    
    private var storageFile: File? = null

    fun initStorage(rootDir: File) {
        storageFile = File(rootDir, "devices.json")
        loadDevices()
    }

    private fun loadDevices() {
        try {
            if (storageFile?.exists() == true) {
                val json = storageFile!!.readText()
                if (json.isNotBlank()) {
                    val list = Json.decodeFromString<List<DeviceModel>>(json)
                    _devices.clear()
                    _devices.addAll(list)
                } else {
                    storageFile!!.delete()
                    seedDefaultDevices()
                }
            } else {
                seedDefaultDevices()
            }
        } catch (e: Exception) {
            _devices.clear()
            seedDefaultDevices()
        }
    }

    private fun seedDefaultDevices() {
        _devices.addAll(listOf(
            DeviceModel("Pixel 9 Pro", "API 35", false),
            DeviceModel("Pixel Fold 2", "API 34", false),
            DeviceModel("Pixel Tablet", "API 34", false),
            DeviceModel("Nexus 6P (Legacy)", "API 23", false),
            DeviceModel("Samsung Galaxy S24 (Sim)", "API 34", false),
            DeviceModel("Generic Desktop", "API 33", false)
        ))
        saveDevices()
    }

    private fun saveDevices() {
        viewModelScope.launch {
            try {
                val json = Json.encodeToString(_devices.toList())
                storageFile?.writeText(json)
            } catch (e: Exception) {}
        }
    }

    fun addDevice(name: String, api: String) {
        _devices.add(DeviceModel(name, api, false))
        saveDevices()
    }

    fun toggleDevice(index: Int) {
        val device = _devices[index]
        _devices[index] = device.copy(isRunning = !device.isRunning)
        saveDevices()
    }
}

class ProjectViewModel : ViewModel() {
    var lastCreatedProject: String? = null
    var refreshTrigger by mutableIntStateOf(0)
    var selectedToolTab by mutableIntStateOf(0)

    fun notifyProjectCreated(name: String) {
        lastCreatedProject = name
        refreshTrigger++
    }
}

class BuildToolsViewModel : ViewModel() {
    private val _isProcessing = mutableStateOf(false)
    val isProcessing: State<Boolean> = _isProcessing

    fun generateKeystore(alias: String, pass: String) {
        viewModelScope.launch {
            _isProcessing.value = true
            kotlinx.coroutines.delay(2000)
            _isProcessing.value = false
        }
    }

    fun zipalignApk(path: String) {
        viewModelScope.launch {
            _isProcessing.value = true
            kotlinx.coroutines.delay(1500)
            _isProcessing.value = false
        }
    }
}
