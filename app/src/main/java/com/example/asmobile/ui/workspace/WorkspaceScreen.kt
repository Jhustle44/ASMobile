package com.example.asmobile.ui.workspace

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.automirrored.rounded.ViewList
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.asmobile.navigation.FileDetailKey
import com.example.asmobile.navigation.FileListKey
import androidx.compose.ui.tooling.preview.Preview
import com.example.asmobile.ui.theme.ASMobileTheme
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import com.example.asmobile.ui.tools.BuildLogPanel
import com.example.asmobile.ui.tools.GitPanel
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.asmobile.R
import androidx.compose.ui.graphics.vector.ImageVector as Vector
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.asmobile.ui.tools.BuildLogViewModel
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun WorkspaceScreen(modifier: Modifier = Modifier) {
    val backStack = rememberNavBackStack(FileListKey)

    val windowAdaptiveInfo = currentWindowAdaptiveInfo()
    val directive = remember(windowAdaptiveInfo) {
        calculatePaneScaffoldDirective(windowAdaptiveInfo)
            .copy(horizontalPartitionSpacerSize = 0.dp)
    }
    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>(directive = directive)
    val context = LocalContext.current
    val rootDir = remember { context.filesDir }
    var currentTool by remember { mutableStateOf("Git") }
    var isToolsVisible by remember { mutableStateOf(true) }
    var leftPanelTab by remember { mutableStateOf("Project") }
    var rightPanelTab by remember { mutableStateOf<String?>(null) }
    
    val openFiles = remember { mutableStateListOf<String>() }
    var activeFilePath by remember { mutableStateOf<String?>(null) }
    
    val buildViewModel: BuildLogViewModel = viewModel()

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(
                                            Brush.linearGradient(
                                                colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
                                            ),
                                            CircleShape
                                        )
                                        .padding(6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Rounded.Source,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    stringResource(R.string.app_name),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp
                                )
                            }
                            
                            // IDE Toolbar
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                ToolbarButton(Icons.Rounded.Sync, "Sync Project")
                                Spacer(Modifier.width(8.dp))
                                
                                var showRunConfig by remember { mutableStateOf(false) }
                                Box {
                                    Surface(
                                        onClick = { showRunConfig = true },
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(4.dp),
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                        modifier = Modifier.padding(horizontal = 4.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Rounded.Android, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                                            Spacer(Modifier.width(8.dp))
                                            Text("app", style = MaterialTheme.typography.labelLarge)
                                            Icon(Icons.Rounded.ArrowDropDown, contentDescription = null, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                    
                                    DropdownMenu(
                                        expanded = showRunConfig,
                                        onDismissRequest = { showRunConfig = false }
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("app") },
                                            onClick = { showRunConfig = false },
                                            leadingIcon = { Icon(Icons.Rounded.Android, contentDescription = null, modifier = Modifier.size(18.dp)) }
                                        )
                                        HorizontalDivider()
                                        DropdownMenuItem(
                                            text = { Text("Edit Configurations...") },
                                            onClick = { showRunConfig = false }
                                        )
                                    }
                                }

                                Spacer(Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape)
                                        .clickable { 
                                            buildViewModel.startBuild()
                                            currentTool = "Build"
                                            isToolsVisible = true
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Rounded.PlayArrow, 
                                        contentDescription = "Run",
                                        tint = Color(0xFF4CAF50),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(Modifier.width(8.dp))
                                ToolbarButton(Icons.Rounded.BugReport, "Debug", tint = Color(0xFF4CAF50))
                                ToolbarButton(Icons.Rounded.Search, "Search")
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                    ),
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            }
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                shadowElevation = 8.dp
            ) {
                Column {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ToolTab(
                                label = "Git",
                                icon = Icons.Rounded.Source,
                                isSelected = currentTool == "Git" && isToolsVisible,
                                onClick = {
                                    if (currentTool == "Git") isToolsVisible = !isToolsVisible
                                    else {
                                        currentTool = "Git"
                                        isToolsVisible = true
                                    }
                                }
                            )
                            ToolTab(
                                label = "Build",
                                icon = Icons.Rounded.Build,
                                isSelected = currentTool == "Build" && isToolsVisible,
                                onClick = {
                                    if (currentTool == "Build") isToolsVisible = !isToolsVisible
                                    else {
                                        currentTool = "Build"
                                        isToolsVisible = true
                                    }
                                }
                            )
                            ToolTab(
                                label = "Logcat",
                                icon = Icons.AutoMirrored.Rounded.ViewList,
                                isSelected = currentTool == "Logcat" && isToolsVisible,
                                onClick = {
                                    if (currentTool == "Logcat") isToolsVisible = !isToolsVisible
                                    else {
                                        currentTool = "Logcat"
                                        isToolsVisible = true
                                    }
                                }
                            )
                            ToolTab(
                                label = "Terminal",
                                icon = Icons.Rounded.Terminal,
                                isSelected = currentTool == "Terminal" && isToolsVisible,
                                onClick = {
                                    if (currentTool == "Terminal") isToolsVisible = !isToolsVisible
                                    else {
                                        currentTool = "Terminal"
                                        isToolsVisible = true
                                    }
                                }
                            )
                        }
                    }
                }
            }
        },
        modifier = modifier.systemBarsPadding()
    ) { padding ->
        Row(modifier = Modifier.padding(padding)) {
            // Left Side Navigation Rail
            NavigationRail(
                modifier = Modifier.fillMaxHeight(),
                containerColor = MaterialTheme.colorScheme.background,
                header = {
                    Spacer(Modifier.height(16.dp))
                }
            ) {
                NavigationRailItem(
                    selected = leftPanelTab == "Project",
                    onClick = { leftPanelTab = "Project" },
                    icon = { Icon(Icons.Rounded.Folder, contentDescription = "Project") },
                    label = { Text("Project", style = MaterialTheme.typography.labelSmall) },
                    colors = NavigationRailItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    )
                )
                NavigationRailItem(
                    selected = leftPanelTab == "Resource",
                    onClick = { leftPanelTab = "Resource" },
                    icon = { Icon(Icons.Rounded.Image, contentDescription = "Resource Manager") },
                    label = { Text("Resource", style = MaterialTheme.typography.labelSmall) },
                    colors = NavigationRailItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    )
                )
                NavigationRailItem(
                    selected = leftPanelTab == "Structure",
                    onClick = { leftPanelTab = "Structure" },
                    icon = { Icon(Icons.AutoMirrored.Rounded.List, contentDescription = "Structure") },
                    label = { Text("Structure", style = MaterialTheme.typography.labelSmall) },
                    colors = NavigationRailItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    )
                )
            }
            
            VerticalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            
            Column(modifier = Modifier.weight(1f)) {
                Box(modifier = Modifier.weight(1f)) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        Box(modifier = Modifier.weight(1f)) {
                            NavDisplay(
                                backStack = backStack,
                                onBack = { backStack.removeLastOrNull() },
                                sceneStrategy = listDetailStrategy,
                                entryProvider = entryProvider {
                                    entry<FileListKey>(
                                        metadata = ListDetailSceneStrategy.listPane(
                                            detailPlaceholder = {
                                                TabbedEditor(
                                                    openFiles = openFiles,
                                                    activeFilePath = activeFilePath,
                                                    onFileSelected = { activeFilePath = it },
                                                    onFileClosed = { path ->
                                                        openFiles.remove(path)
                                                        if (activeFilePath == path) {
                                                            activeFilePath = openFiles.lastOrNull()
                                                        }
                                                    }
                                                )
                                            }
                                        )
                                    ) { _: FileListKey ->
                                        when (leftPanelTab) {
                                            "Project" -> FileTree(
                                                rootDir = rootDir,
                                                onFileSelected = { file ->
                                                    if (!openFiles.contains(file.absolutePath)) {
                                                        openFiles.add(file.absolutePath)
                                                    }
                                                    activeFilePath = file.absolutePath
                                                    backStack.add(FileDetailKey(file.absolutePath))
                                                }
                                            )
                                            "Resource" -> ResourceManager(modifier = Modifier.fillMaxSize())
                                            "Structure" -> StructureView(modifier = Modifier.fillMaxSize())
                                        }
                                    }
                                    entry<FileDetailKey>(
                                        metadata = ListDetailSceneStrategy.detailPane()
                                    ) { _: FileDetailKey ->
                                        TabbedEditor(
                                            openFiles = openFiles,
                                            activeFilePath = activeFilePath,
                                            onFileSelected = { activeFilePath = it },
                                            onFileClosed = { path ->
                                                openFiles.remove(path)
                                                if (activeFilePath == path) {
                                                    activeFilePath = openFiles.lastOrNull()
                                                }
                                            }
                                        )
                                    }
                                }
                            )
                        }
                        
                        if (rightPanelTab != null) {
                            VerticalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                            Box(
                                modifier = Modifier
                                    .width(250.dp)
                                    .fillMaxHeight()
                                    .background(MaterialTheme.colorScheme.surface)
                            ) {
                                when (rightPanelTab) {
                                    "Device" -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        Text("Device Manager")
                                    }
                                    "Gradle" -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        Text("Gradle Tool Window")
                                    }
                                }
                            }
                        }
                    }
                }
                    if (isToolsVisible) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(0.35f)
                                .background(MaterialTheme.colorScheme.surface)
                        ) {
                            when (currentTool) {
                                "Git" -> GitPanel(rootDir = rootDir, modifier = Modifier.fillMaxSize())
                                "Build" -> BuildLogPanel(selectedTab = 1, modifier = Modifier.fillMaxSize())
                                "Logcat" -> BuildLogPanel(selectedTab = 0, modifier = Modifier.fillMaxSize())
                                "Terminal" -> BuildLogPanel(selectedTab = 2, modifier = Modifier.fillMaxSize())
                                "Inspect" -> AppInspectionPanel(modifier = Modifier.fillMaxSize())
                            }
                        }
                    }
            }
            
            VerticalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            
            // Right Side Navigation Rail
            NavigationRail(
                modifier = Modifier.fillMaxHeight(),
                containerColor = MaterialTheme.colorScheme.surface,
            ) {
                NavigationRailItem(
                    selected = rightPanelTab == "Device",
                    onClick = { rightPanelTab = if (rightPanelTab == "Device") null else "Device" },
                    icon = { Icon(Icons.Rounded.Smartphone, contentDescription = "Device Manager") },
                    label = { Text("Device") }
                )
                NavigationRailItem(
                    selected = rightPanelTab == "Gradle",
                    onClick = { rightPanelTab = if (rightPanelTab == "Gradle") null else "Gradle" },
                    icon = { Icon(Icons.Rounded.Build, contentDescription = "Gradle") },
                    label = { Text("Gradle") }
                )
            }
        }
    }
}

@Composable
private fun ToolbarButton(
    icon: Vector,
    contentDescription: String,
    tint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(18.dp),
            tint = tint.copy(alpha = 0.8f)
        )
    }
}

@Composable
private fun ToolTab(
    label: String,
    icon: Vector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else Color.Transparent
    val contentColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else Color.Transparent

    Surface(
        onClick = onClick,
        color = backgroundColor,
        contentColor = contentColor,
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon, 
                contentDescription = null, 
                modifier = Modifier.size(16.dp),
                tint = contentColor
            )
            Spacer(Modifier.width(8.dp))
            Text(
                label, 
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

@Composable
private fun ResourceManager(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp)) {
        Text("Resource Manager", style = MaterialTheme.typography.titleSmall)
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AssistChip(onClick = {}, label = { Text("Drawable") }, leadingIcon = { Icon(Icons.Rounded.Image, null, Modifier.size(16.dp)) })
            AssistChip(onClick = {}, label = { Text("Color") }, leadingIcon = { Icon(Icons.Rounded.Palette, null, Modifier.size(16.dp)) })
            AssistChip(onClick = {}, label = { Text("Layout") }, leadingIcon = { Icon(Icons.Rounded.Dashboard, null, Modifier.size(16.dp)) })
        }
        Spacer(Modifier.height(16.dp))
        AdaptiveGrid(columns = GridCells.Adaptive(80.dp), spacing = Arrangement.spacedBy(8.dp)) {
            items(6) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.aspectRatio(1f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Rounded.Image, null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
                    }
                }
            }
        }
    }
}

@Composable
private fun StructureView(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp)) {
        Text("Structure", style = MaterialTheme.typography.titleSmall)
        Spacer(Modifier.height(16.dp))
        val structure = listOf("class MainActivity", "  fun onCreate()", "  fun seedSampleFiles()", "  fun CustomButton()")
        LazyColumn {
            items(structure) { item: String ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                    Icon(
                        if (item.contains("class")) Icons.Rounded.Category else Icons.Rounded.Terminal,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (item.contains("class")) Color(0xFFE4BC5E) else Color(0xFF4EC9B0)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(item, style = MaterialTheme.typography.bodySmall, fontFamily = FontFamily.Monospace)
                }
            }
        }
    }
}

@Composable
private fun AdaptiveGrid(columns: GridCells, spacing: Arrangement.HorizontalOrVertical, content: LazyGridScope.() -> Unit) {
    LazyVerticalGrid(
        columns = columns,
        horizontalArrangement = spacing,
        verticalArrangement = spacing,
        content = content
    )
}

@Composable
private fun AppInspectionPanel(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp)) {
        Text("App Inspection", style = MaterialTheme.typography.titleSmall)
        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Process: ", style = MaterialTheme.typography.labelMedium)
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("com.example.asmobile (3124)", style = MaterialTheme.typography.bodySmall)
                    Icon(Icons.Rounded.ArrowDropDown, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        TabRow(selectedTabIndex = 0, containerColor = Color.Transparent) {
            Tab(selected = true, onClick = {}) { Text("Database Inspector", modifier = Modifier.padding(8.dp)) }
            Tab(selected = false, onClick = {}) { Text("Background Task Inspector", modifier = Modifier.padding(8.dp)) }
            Tab(selected = false, onClick = {}) { Text("Network Inspector", modifier = Modifier.padding(8.dp)) }
        }
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Select a database to inspect", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
        }
    }
}

@Preview(showBackground = true, widthDp = 1000, heightDp = 800)
@Composable
fun WorkspaceScreenPreview() {
    ASMobileTheme {
        WorkspaceScreen()
    }
}
