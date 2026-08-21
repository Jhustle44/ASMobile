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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import kotlinx.coroutines.launch
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.BorderStroke

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
            // Force select first running device if we just started a run
            val runningIndex = viewModel.devices.indexOfFirst { it.isRunning }
            if (runningIndex != -1) {
                activeDeviceIndex = runningIndex
            } else {
                // Auto-boot first device if none running
                viewModel.toggleDevice(0)
                activeDeviceIndex = 0
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
                    .weight(0.65f)
                    .fillMaxHeight()
                    .padding(16.dp)
                    .aspectRatio(9f / 19.5f)
                    .shadow(24.dp, RoundedCornerShape(36.dp), ambientColor = Color.White.copy(alpha = 0.1f))
                    .border(8.dp, Color(0xFF2C2C2C), RoundedCornerShape(36.dp))
                    .border(10.dp, Color.Black.copy(alpha = 0.5f), RoundedCornerShape(36.dp))
                    .clip(RoundedCornerShape(36.dp)),
                color = Color.Black
            ) {
                if (device.isRunning) {
                    Box(Modifier.fillMaxSize()) {
                        // High Gloss Reflection
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            Color.White.copy(alpha = 0.08f),
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.1f)
                                        )
                                    )
                                )
                        )
                        
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            if (isAppLaunching) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary, strokeWidth = 3.dp)
                                Spacer(Modifier.height(24.dp))
                                Text("Starting process...", color = Color.White, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            } else if (projectName != null) {
                                // 3D Card Simulation for App View
                                Card(
                                    modifier = Modifier.padding(16.dp).fillMaxWidth(0.85f).aspectRatio(1f).shadow(12.dp, RoundedCornerShape(16.dp)),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f))
                                ) {
                                    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                        Icon(Icons.Rounded.AutoAwesome, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(48.dp))
                                        Spacer(Modifier.height(12.dp))
                                        Text(projectName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
                                        Text("v1.0-DEBUG", style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                                Spacer(Modifier.height(24.dp))
                                Text("LIVE PREVIEW", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.2.sp)
                            } else {
                                Icon(Icons.Rounded.Android, null, tint = Color(0xFF3DDC84), modifier = Modifier.size(56.dp))
                                Text("System Booted", color = Color.White, style = MaterialTheme.typography.labelSmall)
                                Text("No app deployed", color = Color.Gray, style = TextStyle(fontSize = 10.sp))
                            }
                        }
                    }
                } else {
                    Box(Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
                        Text("POWERED OFF", color = Color.DarkGray, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    }
                }
            }

            // Debug Console
            Column(modifier = Modifier.weight(0.35f).fillMaxHeight()) {
                Text("VIRTUAL LOGCAT", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                Spacer(Modifier.height(12.dp))
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF0A0A0A),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
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
