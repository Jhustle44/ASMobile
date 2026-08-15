package com.example.asmobile.ui.workspace

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

data class DeviceModel(val name: String, val api: String, val isRunning: Boolean)

class DeviceViewModel : ViewModel() {
    val devices = mutableStateListOf(
        DeviceModel("Pixel 8 Pro", "API 34", true),
        DeviceModel("Pixel Fold", "API 33", false),
        DeviceModel("Nexus 5X", "API 28", false)
    )

    fun addDevice(name: String, api: String) {
        devices.add(DeviceModel(name, api, false))
    }

    fun toggleDevice(index: Int) {
        val device = devices[index]
        devices[index] = device.copy(isRunning = !device.isRunning)
    }
}

class ProjectViewModel : ViewModel() {
    var lastCreatedProject: String? = null
    var refreshTrigger = 0

    fun notifyProjectCreated(name: String) {
        lastCreatedProject = name
        refreshTrigger++
    }
}
