package com.example.asmobile.ui.tools

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.example.asmobile.R
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.icons.rounded.CheckCircle

@Composable
fun BuildLogPanel(
    selectedTab: Int,
    modifier: Modifier = Modifier
) {
    val viewModel: BuildLogViewModel = viewModel()
    
    val logs by viewModel.logs.collectAsState()
    val buildProgress by viewModel.buildProgress.collectAsState()
    val buildStatus by viewModel.buildStatus.collectAsState()
    val isBuilding by viewModel.isBuilding.collectAsState()

    Column(modifier = modifier) {
        when (selectedTab) {
            0 -> LogcatView(
                logs = logs,
                onStart = { viewModel.startLogcat() },
                onStop = { viewModel.stopLogcat() },
                onClear = { viewModel.clearLogs() }
            )
            1 -> BuildView(
                progress = buildProgress,
                status = buildStatus,
                isBuilding = isBuilding,
                onBuild = { viewModel.startBuild() }
            )
            2 -> TerminalView()
        }
    }
}

@Composable
fun TerminalView() {
    var command by remember { mutableStateOf("") }
    val history = remember { mutableStateListOf<String>("Welcome to ASMobile Terminal", "$ ls", "app  build.gradle.kts  gradle  gradlew  local.properties  settings.gradle.kts", "$ ") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E))
            .padding(8.dp)
    ) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(history) { line ->
                Text(
                    text = line,
                    color = Color.White,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp
                )
            }
        }
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("$ ", color = Color.White, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
            androidx.compose.foundation.text.BasicTextField(
                value = command,
                onValueChange = { command = it },
                modifier = Modifier.fillMaxWidth(),
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = Color.White,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp
                ),
                cursorBrush = androidx.compose.ui.graphics.SolidColor(Color.White),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    imeAction = androidx.compose.ui.text.input.ImeAction.Done
                ),
                keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                    onDone = {
                        if (command.isNotBlank()) {
                            history.add("$ $command")
                            history.add("Command executed: $command")
                            history.add("$ ")
                            command = ""
                        }
                    }
                )
            )
        }
    }
}

@Composable
fun LogcatView(logs: List<String>, onStart: () -> Unit, onStop: () -> Unit, onClear: () -> Unit) {
    val listState = rememberLazyListState()
    
    LaunchedEffect(logs.size) {
        if (logs.isNotEmpty()) {
            listState.animateScrollToItem(logs.size - 1)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .background(MaterialTheme.colorScheme.surfaceContainer),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onStart) {
                Icon(Icons.Rounded.PlayArrow, contentDescription = "Start", tint = Color(0xFF4CAF50), modifier = Modifier.size(18.dp))
            }
            IconButton(onClick = onStop) {
                Icon(Icons.Rounded.Stop, contentDescription = "Stop", tint = Color(0xFFF44336), modifier = Modifier.size(18.dp))
            }
            IconButton(onClick = onClear) {
                Icon(Icons.Rounded.ClearAll, contentDescription = "Clear", modifier = Modifier.size(18.dp))
            }
            
            VerticalDivider(modifier = Modifier.height(24.dp).padding(horizontal = 4.dp))
            
            var filterText by remember { mutableStateOf("") }
            androidx.compose.foundation.text.BasicTextField(
                value = filterText,
                onValueChange = { filterText = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.small)
                    .padding(horizontal = 8.dp, vertical = 2.dp),
                textStyle = MaterialTheme.typography.bodySmall,
                decorationBox = { innerTextField ->
                    if (filterText.isEmpty()) {
                        Text("Filter", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                    }
                    innerTextField()
                }
            )
            
            Spacer(Modifier.width(8.dp))
            var showLevelMenu by remember { mutableStateOf(false) }
            Box {
                AssistChip(
                    onClick = { showLevelMenu = true },
                    label = { Text("Verbose") },
                    trailingIcon = { Icon(Icons.Rounded.ArrowDropDown, null, Modifier.size(16.dp)) },
                    shape = RoundedCornerShape(8.dp)
                )
                DropdownMenu(expanded = showLevelMenu, onDismissRequest = { showLevelMenu = false }) {
                    DropdownMenuItem(text = { Text("Verbose") }, onClick = { showLevelMenu = false })
                    DropdownMenuItem(text = { Text("Debug") }, onClick = { showLevelMenu = false })
                    DropdownMenuItem(text = { Text("Info") }, onClick = { showLevelMenu = false })
                    DropdownMenuItem(text = { Text("Warn") }, onClick = { showLevelMenu = false })
                    DropdownMenuItem(text = { Text("Error") }, onClick = { showLevelMenu = false })
                }
            }
            Spacer(Modifier.width(8.dp))
        }
        
        // Filter Chips
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(selected = true, onClick = {}, label = { Text("All", style = MaterialTheme.typography.labelSmall) })
            FilterChip(selected = false, onClick = {}, label = { Text("Firebase", style = MaterialTheme.typography.labelSmall) })
            FilterChip(selected = false, onClick = {}, label = { Text("Compose", style = MaterialTheme.typography.labelSmall) })
            FilterChip(selected = false, onClick = {}, label = { Text("System", style = MaterialTheme.typography.labelSmall) })
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF000000))
                .padding(8.dp)
        ) {
            items(logs) { log ->
                Text(
                    text = log,
                    color = Color.Green,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun BuildView(progress: Float, status: String, isBuilding: Boolean, onBuild: () -> Unit) {
    val tasks = remember(isBuilding) {
        if (isBuilding) {
            listOf(
                ":app:preBuild UP-TO-DATE",
                ":app:preDebugBuild UP-TO-DATE",
                ":app:mergeDebugResources",
                ":app:processDebugMainManifest",
                ":app:javaPreCompileDebug",
                ":app:mergeDebugNativeLibs",
                ":app:compileDebugKotlin",
                ":app:dexBuilderDebug"
            )
        } else listOf("Build finished successfully")
    }

    Row(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                .padding(8.dp)
        ) {
            Text("Build Output", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            LazyColumn {
                items(tasks) { task ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            if (task.contains("UP-TO-DATE")) Icons.Rounded.CheckCircle else Icons.Rounded.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = if (task.contains("UP-TO-DATE")) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(task, style = FontFamily.Monospace.toTextStyle().copy(fontSize = 11.sp))
                    }
                }
            }
        }
        
        VerticalDivider()
        
        Column(
            modifier = Modifier
                .width(200.dp)
                .fillMaxHeight()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(status, style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(16.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = onBuild,
                enabled = !isBuilding,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Icon(Icons.Rounded.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Build", maxLines = 1, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

private fun FontFamily.toTextStyle() = androidx.compose.ui.text.TextStyle(fontFamily = this)
