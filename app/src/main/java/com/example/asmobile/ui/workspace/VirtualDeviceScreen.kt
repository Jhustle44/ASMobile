package com.example.asmobile.ui.workspace

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun VirtualDeviceScreen(modifier: Modifier = Modifier) {
    var showAddDevice by remember { mutableStateOf(false) }
    val devices = remember { mutableStateListOf(
        DeviceModel("Pixel 8 Pro", "API 34", true),
        DeviceModel("Pixel Fold", "API 33", false),
        DeviceModel("Nexus 5X", "API 28", false)
    ) }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Virtual Devices", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
            Button(
                onClick = { showAddDevice = true },
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Rounded.Add, null)
                Spacer(Modifier.width(8.dp))
                Text("Create Device")
            }
        }

        Spacer(Modifier.height(24.dp))

        LazyVerticalGrid(
            columns = GridCells.Adaptive(160.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(devices) { device ->
                DeviceCard(device)
            }
        }
    }

    if (showAddDevice) {
        AddDeviceDialog(
            onDismiss = { showAddDevice = false },
            onAdd = { name, api ->
                devices.add(DeviceModel(name, api, false))
                showAddDevice = false
            }
        )
    }
}

data class DeviceModel(val name: String, val api: String, val isRunning: Boolean)

@Composable
private fun DeviceCard(device: DeviceModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Phone Frame
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(140.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black)
                    .border(2.dp, Color.DarkGray, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (device.isRunning) {
                    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)))
                    Icon(Icons.Rounded.Android, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                } else {
                    Icon(Icons.Rounded.PowerSettingsNew, null, tint = Color.Gray)
                }
            }

            Spacer(Modifier.height(16.dp))
            Text(device.name, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Text(device.api, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            
            Spacer(Modifier.height(12.dp))
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = { },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = if (device.isRunning) MaterialTheme.colorScheme.error.copy(alpha = 0.1f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    )
                ) {
                    Icon(
                        if (device.isRunning) Icons.Rounded.Stop else Icons.Rounded.PlayArrow,
                        null,
                        tint = if (device.isRunning) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = { }) {
                    Icon(Icons.Rounded.Edit, null, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
private fun AddDeviceDialog(onDismiss: () -> Unit, onAdd: (String, String) -> Unit) {
    var name by remember { mutableStateOf("New Pixel") }
    var api by remember { mutableStateOf("API 35") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Virtual Device") },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Device Name") })
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = api, onValueChange = { api = it }, label = { Text("API Level") })
            }
        },
        confirmButton = {
            Button(onClick = { onAdd(name, api) }) { Text("Create") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
