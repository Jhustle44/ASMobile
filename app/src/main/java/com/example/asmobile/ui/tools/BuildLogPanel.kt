package com.example.asmobile.ui.tools

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ClearAll
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Stop
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

@Composable
fun BuildLogPanel(modifier: Modifier = Modifier) {
    val viewModel: BuildLogViewModel = viewModel()
    
    val logs by viewModel.logs.collectAsState()
    val buildProgress by viewModel.buildProgress.collectAsState()
    val buildStatus by viewModel.buildStatus.collectAsState()
    val isBuilding by viewModel.isBuilding.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }

    Column(modifier = modifier) {
        TabRow(selectedTabIndex = selectedTab) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                Text(stringResource(R.string.logcat), modifier = Modifier.padding(16.dp))
            }
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                Text(stringResource(R.string.build), modifier = Modifier.padding(16.dp))
            }
        }

        if (selectedTab == 0) {
            LogcatView(
                logs = logs,
                onStart = {
                    viewModel.startLogcat()
                },
                onStop = {
                    viewModel.stopLogcat()
                },
            ) {
                viewModel.clearLogs()
            }
        } else {
            BuildView(
                progress = buildProgress,
                status = buildStatus,
                isBuilding = isBuilding,
            ) {
                viewModel.startBuild()
            }
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
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onStart) {
                Icon(Icons.Rounded.PlayArrow, contentDescription = "Start")
            }
            IconButton(onClick = onStop) {
                Icon(Icons.Rounded.Stop, contentDescription = "Stop")
            }
            IconButton(onClick = onClear) {
                Icon(Icons.Rounded.ClearAll, contentDescription = "Clear")
            }
        }
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
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
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(status, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(16.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(32.dp))
        Button(
            onClick = onBuild,
            enabled = !isBuilding,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Rounded.PlayArrow, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(stringResource(R.string.start_build), maxLines = 1)
        }
    }
}
