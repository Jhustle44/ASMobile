package com.example.asmobile.ui.workspace

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel

data class DeviceModel(val name: String, val api: String, val isRunning: Boolean)

class DeviceViewModel : ViewModel() {
    private val _devices = mutableStateListOf(
        DeviceModel("Pixel 8 Pro", "API 34", true),
        DeviceModel("Pixel Fold", "API 33", false),
        DeviceModel("Nexus 5X", "API 28", false)
    )
    val devices: List<DeviceModel> get() = _devices

    fun addDevice(name: String, api: String) {
        _devices.add(DeviceModel(name, api, false))
    }

    fun toggleDevice(index: Int) {
        val device = _devices[index]
        _devices[index] = device.copy(isRunning = !device.isRunning)
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
