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
            DeviceModel("Pixel 9 Pro XL", "API 35", false),
            DeviceModel("Pixel 9 Fold", "API 35", false),
            DeviceModel("Samsung S24 Ultra", "API 34", false),
            DeviceModel("Pixel Tablet 2", "API 34", false),
            DeviceModel("Generic 10-inch Tablet", "API 33", false),
            DeviceModel("Nexus 6P (Elite Legacy)", "API 23", false)
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
    var activeRunProject: File? by mutableStateOf(null)
    var activeProject: File? by mutableStateOf(null)

    fun notifyProjectCreated(name: String) {
        lastCreatedProject = name
        refreshTrigger++
    }

    fun startRun(project: File) {
        activeRunProject = project
    }
    
    fun selectProject(project: File?) {
        activeProject = project
    }
}

class ThemeViewModel : ViewModel() {
    var currentTheme by mutableStateOf(ThemeMode.Obsidian)

    fun setTheme(theme: ThemeMode) {
        currentTheme = theme
    }
}

enum class ThemeMode(val label: String) {
    Obsidian("Obsidian (Elite)"),
    Arctic("Arctic White"),
    Solar("Solarized Pro"),
    Midnight("Midnight Blue"),
    Forest("Deep Forest"),
    Rose("Rose Gold")
}

class PluginViewModel : ViewModel() {
    private val _installedPlugins = mutableStateListOf<String>()
    val installedPlugins: List<String> get() = _installedPlugins
    private var storageFile: File? = null

    fun initStorage(rootDir: File) {
        storageFile = File(rootDir, "plugins.json")
        loadPlugins()
    }

    private fun loadPlugins() {
        try {
            if (storageFile?.exists() == true) {
                val json = storageFile!!.readText()
                val list = Json.decodeFromString<List<String>>(json)
                _installedPlugins.clear()
                _installedPlugins.addAll(list)
            }
        } catch (e: Exception) {}
    }

    private fun savePlugins() {
        viewModelScope.launch {
            try {
                val json = Json.encodeToString(_installedPlugins.toList())
                storageFile?.writeText(json)
            } catch (e: Exception) {}
        }
    }

    fun installPlugin(name: String) {
        if (!_installedPlugins.contains(name)) {
            _installedPlugins.add(name)
            savePlugins()
        }
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
