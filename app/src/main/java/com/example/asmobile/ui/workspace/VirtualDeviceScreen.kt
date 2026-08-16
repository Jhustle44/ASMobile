package com.example.asmobile.ui.workspace

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import androidx.compose.foundation.shape.CircleShape

@Composable
fun VirtualDeviceScreen(
    viewModel: DeviceViewModel,
    projectViewModel: ProjectViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onRunProject: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showAddDevice by remember { mutableStateOf(false) }
    var activeDeviceIndex by remember { mutableIntStateOf(-1) }

    LaunchedEffect(projectViewModel.activeRunProject) {
        if (projectViewModel.activeRunProject != null) {
            val runningIndex = viewModel.devices.indexOfFirst { it.isRunning }
            if (runningIndex != -1) {
                activeDeviceIndex = runningIndex
            }
        }
    }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        if (activeDeviceIndex == -1) {
            DeviceGallery(
                devices = viewModel.devices,
                onAddClick = { showAddDevice = true },
                onSelectDevice = { activeDeviceIndex = it },
                onToggle = { viewModel.toggleDevice(it) }
            )
        } else {
            VirtualDisplayView(
                device = viewModel.devices[activeDeviceIndex],
                projectName = projectViewModel.activeRunProject?.name,
                onBack = { 
                    activeDeviceIndex = -1 
                    projectViewModel.activeRunProject = null // Reset after viewing
                },
                onRun = onRunProject
            )
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
private fun DeviceGallery(
    devices: List<DeviceModel>,
    onAddClick: () -> Unit,
    onSelectDevice: (Int) -> Unit,
    onToggle: (Int) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Device Manager", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
            Button(
                onClick = onAddClick,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Rounded.Add, null)
                Spacer(Modifier.width(8.dp))
                Text("Create Virtual Device")
            }
        }

        Spacer(Modifier.height(24.dp))

        LazyVerticalGrid(
            columns = GridCells.Adaptive(160.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(devices.size) { index ->
                val device = devices[index]
                DeviceCard(
                    device = device,
                    onToggle = { onToggle(index) },
                    onRun = { onSelectDevice(index) }
                )
            }
        }
    }
}

@Composable
private fun VirtualDisplayView(
    device: DeviceModel,
    projectName: String?,
    onBack: () -> Unit,
    onRun: () -> Unit
) {
    var isAppLaunching by remember(projectName) { mutableStateOf(projectName != null) }

    LaunchedEffect(projectName) {
        if (projectName != null) {
            isAppLaunching = true
            kotlinx.coroutines.delay(2000)
            isAppLaunching = false
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Rounded.ArrowBack, null) }
            Column {
                Text(device.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(device.api, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.weight(1f))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = { /* Screenshot */ }) { Icon(Icons.Rounded.Screenshot, null) }
                IconButton(onClick = { /* Debug Toggle */ }) { Icon(Icons.Rounded.BugReport, null, tint = MaterialTheme.colorScheme.secondary) }
                Button(
                    onClick = onRun,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                ) {
                    Icon(Icons.Rounded.PlayArrow, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Deploy Build")
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            // Simulated Phone Display
            Surface(
                modifier = Modifier
                    .weight(0.6f)
                    .fillMaxHeight()
                    .padding(24.dp)
                    .aspectRatio(9f / 19f)
                    .border(8.dp, Color.DarkGray, RoundedCornerShape(32.dp))
                    .clip(RoundedCornerShape(32.dp)),
                color = Color.Black
            ) {
                if (device.isRunning) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            if (isAppLaunching) {
                                CircularProgressIndicator(color = Color.White)
                                Spacer(Modifier.height(16.dp))
                                Text("Launching $projectName...", color = Color.White, style = MaterialTheme.typography.labelSmall)
                            } else if (projectName != null) {
                                Icon(Icons.Rounded.AutoAwesome, null, tint = Color.Green, modifier = Modifier.size(48.dp))
                                Text(projectName, color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text("Running Live", color = Color.Green, style = MaterialTheme.typography.labelSmall)
                            } else {
                                Icon(Icons.Rounded.Android, null, tint = Color.Green, modifier = Modifier.size(48.dp))
                                Text("System Booted", color = Color.White, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                } else {
                    Box(Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
                        Text("Powered Off", color = Color.Gray)
                    }
                }
            }

            // Debug Console
            Column(modifier = Modifier.weight(0.4f).fillMaxHeight()) {
                Text("Debug Console", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(8.dp))
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF1E1E1E),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    LazyColumn(modifier = Modifier.padding(12.dp)) {
                        item { Text("I/System: Initializing hardware...", color = Color.Gray, fontSize = 10.sp) }
                        if (device.isRunning) {
                            item { Text("D/Activity: MainActivity created", color = Color.Cyan, fontSize = 10.sp) }
                            if (projectName != null) {
                                item { Text("I/App: Attaching to process: com.ai.${projectName.lowercase()}", color = Color.Green, fontSize = 10.sp) }
                                item { Text("D/ViewRoot: Starting main frame loop...", color = Color.White, fontSize = 10.sp) }
                            } else {
                                item { Text("V/View: Inflating layout...", color = Color.White, fontSize = 10.sp) }
                                item { Text("W/Asset: Loading pro resources", color = Color.Yellow, fontSize = 10.sp) }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DeviceCard(device: DeviceModel, onToggle: () -> Unit, onRun: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { if (device.isRunning) onRun() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var isBooting by remember { mutableStateOf(false) }
            val scope = rememberCoroutineScope()

            // Phone Frame Visual
            Box(
                modifier = Modifier
                    .width(70.dp)
                    .height(120.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black)
                    .border(2.dp, if (device.isRunning) MaterialTheme.colorScheme.primary else Color.DarkGray, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (isBooting) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else if (device.isRunning) {
                    Icon(Icons.Rounded.Android, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
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
                    onClick = { 
                        if (!device.isRunning) {
                            isBooting = true
                            scope.launch {
                                kotlinx.coroutines.delay(1500)
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
                    Icon(
                        if (device.isRunning) Icons.Rounded.Stop else Icons.Rounded.PlayArrow,
                        null,
                        tint = if (device.isRunning) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onRun, enabled = device.isRunning) {
                    Icon(Icons.Rounded.Visibility, null, modifier = Modifier.size(20.dp))
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
        title = { Text("Create Device Model") },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Device Name") })
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = api, onValueChange = { api = it }, label = { Text("Target SDK Level") })
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
