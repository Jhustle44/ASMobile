package com.example.asmobile.ui.workspace

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    var darkMode by remember { mutableStateOf(true) }
    var autoSave by remember { mutableStateOf(true) }
    var aiSuggestions by remember { mutableStateOf(true) }

    Dialog(
        onDismissRequest = onBack,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Bar
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, null, tint = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(Modifier.width(8.dp))
                        Text("Settings", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                    }
                }

                LazyColumn(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                    item { SettingSectionHeader("Appearance") }
                    item {
                        SettingToggle(
                            title = "Obsidian Dark Theme",
                            description = "Use high-contrast OLED black aesthetics",
                            checked = darkMode,
                            onCheckedChange = { darkMode = it }
                        )
                    }
                    
                    item { Spacer(Modifier.height(32.dp)) }
                    item { SettingSectionHeader("Development") }
                    item {
                        SettingToggle(
                            title = "Intelligent Auto-save",
                            description = "Commit changes to local disk automatically",
                            checked = autoSave,
                            onCheckedChange = { autoSave = it }
                        )
                    }
                    item {
                        SettingToggle(
                            title = "Gemini Code Suggestions",
                            description = "Real-time AI completions while typing",
                            checked = aiSuggestions,
                            onCheckedChange = { aiSuggestions = it }
                        )
                    }

                    item { Spacer(Modifier.height(32.dp)) }
                    item { SettingSectionHeader("System") }
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = androidx.compose.ui.graphics.Color.Transparent
                        ) {
                            Column(modifier = Modifier.padding(vertical = 12.dp)) {
                                Text("ASMobile version", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                                Text("v2.5-ELITE (Build 2026.08.15)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingSectionHeader(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
private fun SettingToggle(title: String, description: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(
            checked = checked, 
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            )
        )
    }
}
