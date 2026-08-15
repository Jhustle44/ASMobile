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

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    var darkMode by remember { mutableStateOf(true) }
    var autoSave by remember { mutableStateOf(true) }
    var aiSuggestions by remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxSize()) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, null)
                }
                Text("Settings", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
            }
        }

        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            item { SettingHeader("Appearance") }
            item {
                SettingSwitch(
                    title = "Dark Mode",
                    description = "Use Obsidian Dark theme system-wide",
                    checked = darkMode,
                    onCheckedChange = { darkMode = it }
                )
            }
            
            item { Spacer(Modifier.height(24.dp)) }
            item { SettingHeader("Editor") }
            item {
                SettingSwitch(
                    title = "Auto-save",
                    description = "Automatically save files on every keystroke",
                    checked = autoSave,
                    onCheckedChange = { autoSave = it }
                )
            }
            item {
                SettingSwitch(
                    title = "AI Suggestions",
                    description = "Enable Gemini real-time code completions",
                    checked = aiSuggestions,
                    onCheckedChange = { aiSuggestions = it }
                )
            }

            item { Spacer(Modifier.height(24.dp)) }
            item { SettingHeader("About") }
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { },
                    color = androidx.compose.ui.graphics.Color.Transparent
                ) {
                    Column(modifier = Modifier.padding(vertical = 12.dp)) {
                        Text("ASMobile version", style = MaterialTheme.typography.labelLarge)
                        Text("1.7-Final (Build 7)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun SettingSwitch(title: String, description: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
