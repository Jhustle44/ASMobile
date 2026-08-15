package com.example.asmobile.ui.workspace

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
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
import kotlinx.coroutines.launch

@Composable
fun VirtualDeviceScreen(
    viewModel: DeviceViewModel,
    modifier: Modifier = Modifier
) {
    var showAddDevice by remember { mutableStateOf(false) }

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
            itemsIndexed(viewModel.devices) { index, device ->
                DeviceCard(
                    device = device,
                    onToggle = { viewModel.toggleDevice(index) }
                )
            }
        }
    }

    if (showAddDevice) {
        AddDeviceDialog(
            onDismiss = { showAddDevice = false },
            onAdd = { name, api ->
                viewModel.addDevice(name, api)
                showAddDevice = false
            }
        )
    }
}

@Composable
private fun DeviceCard(device: DeviceModel, onToggle: () -> Unit) {
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
                var isBooting by remember { mutableStateOf(false) }
                val scope = rememberCoroutineScope()

                IconButton(
                    onClick = { 
                        if (!device.isRunning) {
                            isBooting = true
                            scope.launch {
                                kotlinx.coroutines.delay(2000)
                                isBooting = false
                                onToggle()
                            }
                        } else {
                            onToggle()
                        }
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = if (device.isRunning) MaterialTheme.colorScheme.error.copy(alpha = 0.1f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    )
                ) {
                    if (isBooting) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(
                            if (device.isRunning) Icons.Rounded.Stop else Icons.Rounded.PlayArrow,
                            null,
                            tint = if (device.isRunning) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        )
                    }
                }
                IconButton(onClick = { }) {
                    Icon(Icons.Rounded.Settings, null, modifier = Modifier.size(20.dp))
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
