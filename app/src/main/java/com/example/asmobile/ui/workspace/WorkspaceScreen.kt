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
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun WorkspaceScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val rootDir = remember { context.filesDir }
    
    var selectedDestination by remember { mutableStateOf(MobileDestination.Dashboard) }
    var showProjectWizard by remember { mutableStateOf(false) }
    var showSearchEverywhere by remember { mutableStateOf(false) }
    var buildVariant by remember { mutableStateOf("debug") }
    
    val openFiles = remember { mutableStateListOf<String>() }
    var activeFilePath by remember { mutableStateOf<String?>(null) }
    
    val buildViewModel: BuildLogViewModel = viewModel()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surface,
                drawerShape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
            ) {
                Spacer(Modifier.height(24.dp))
                Text(
                    "ASMobile Tools", 
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )
                NavigationDrawerItem(
                    label = { Text("Search Project") },
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.Rounded.Search, null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    label = { Text("Device Manager") },
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.Rounded.Smartphone, null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    label = { Text("Resource Explorer") },
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.Rounded.Category, null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    label = { Text("Dependency Manager") },
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.Rounded.Layers, null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    label = { Text("Settings") },
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.Rounded.Settings, null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                Spacer(Modifier.weight(1f))
                Text("Version 1.4-Mobile", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    ) {
        Scaffold(
            topBar = {
                Surface(
                    color = MaterialTheme.colorScheme.background.copy(alpha = 0.9f),
                    modifier = Modifier.statusBarsPadding()
                ) {
                    CenterAlignedTopAppBar(
                        title = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(
                                            Brush.linearGradient(colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)),
                                            CircleShape
                                        )
                                        .padding(4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Rounded.Source, null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(14.dp))
                                }
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "ASMobile",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = { scope.launch { drawerState.open() } },
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                Icon(Icons.Rounded.Menu, contentDescription = "Menu", tint = MaterialTheme.colorScheme.primary)
                            }
                        },
                        actions = {
                            IconButton(onClick = { showSearchEverywhere = true }) {
                                Icon(Icons.Rounded.Search, contentDescription = "Search", modifier = Modifier.size(22.dp))
                            }
                            Spacer(Modifier.width(4.dp))
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = Color.Transparent
                        )
                    )
                }
            },
            bottomBar = {
                Surface(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .shadow(12.dp, RoundedCornerShape(24.dp)),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    NavigationBar(
                        containerColor = Color.Transparent,
                        tonalElevation = 0.dp,
                        modifier = Modifier.height(64.dp)
                    ) {
                        MobileDestination.entries.forEach { destination ->
                            NavigationBarItem(
                                selected = selectedDestination == destination,
                                onClick = { selectedDestination = destination },
                                icon = { 
                                    Icon(
                                        destination.icon, 
                                        contentDescription = destination.label,
                                        modifier = Modifier.size(22.dp)
                                    ) 
                                },
                                label = { 
                                    Text(
                                        destination.label, 
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (selectedDestination == destination) FontWeight.Bold else FontWeight.Normal
                                    ) 
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                )
                            )
                        }
                    }
                }
            },
            floatingActionButton = {
                if (selectedDestination == MobileDestination.Editor || selectedDestination == MobileDestination.Dashboard) {
                    ExtendedFloatingActionButton(
                        onClick = { buildViewModel.startBuild() },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        shape = RoundedCornerShape(16.dp),
                        icon = { Icon(Icons.Rounded.PlayArrow, "Run Build", modifier = Modifier.size(20.dp)) },
                        text = { Text("Run", fontWeight = FontWeight.Bold) },
                        modifier = Modifier.padding(bottom = 80.dp) // Adjust for floating bottom bar
                    )
                }
            },
            modifier = modifier.systemBarsPadding()
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                when (selectedDestination) {
                    MobileDestination.Dashboard -> Dashboard(
                        rootDir = rootDir,
                        onFileSelected = { file ->
                            if (!openFiles.contains(file.absolutePath)) openFiles.add(file.absolutePath)
                            activeFilePath = file.absolutePath
                            selectedDestination = MobileDestination.Editor
                        },
                        onNewProjectClick = { showProjectWizard = true }
                    )
                    MobileDestination.Project -> FileTree(
                        rootDir = rootDir,
                        onFileSelected = { file ->
                            if (!openFiles.contains(file.absolutePath)) openFiles.add(file.absolutePath)
                            activeFilePath = file.absolutePath
                            selectedDestination = MobileDestination.Editor
                        }
                    )
                    MobileDestination.Editor -> {
                        TabbedEditor(
                            openFiles = openFiles,
                            activeFilePath = activeFilePath,
                            onFileSelected = { activeFilePath = it },
                            onFileClosed = { path ->
                                openFiles.remove(path)
                                if (activeFilePath == path) activeFilePath = openFiles.lastOrNull()
                            }
                        )
                    }
                    MobileDestination.Git -> GitPanel(rootDir = rootDir, modifier = Modifier.fillMaxSize())
                    MobileDestination.Ai -> AiAssistantPanel(modifier = Modifier.fillMaxSize())
                    MobileDestination.Tools -> MobileToolsTabs(buildViewModel)
                }
            }
        }

        if (showProjectWizard) {
            NewProjectWizard(
                baseDir = rootDir,
                onDismiss = { showProjectWizard = false },
                onProjectCreated = { 
                    // Refresh or notify
                }
            )
        }

        if (showSearchEverywhere) {
            SearchEverywhere(
                rootDir = rootDir,
                onDismiss = { showSearchEverywhere = false },
                onFileSelected = { file ->
                    if (!openFiles.contains(file.absolutePath)) openFiles.add(file.absolutePath)
                    activeFilePath = file.absolutePath
                    selectedDestination = MobileDestination.Editor
                }
            )
        }
    }
}

enum class MobileDestination(val label: String, val icon: Vector) {
    Dashboard("Home", Icons.Rounded.Dashboard),
    Project("Project", Icons.Rounded.Folder),
    Ai("AI", Icons.Rounded.AutoAwesome),
    Editor("Editor", Icons.Rounded.Code),
    Git("Git", Icons.Rounded.History),
    Tools("Tools", Icons.Rounded.Build)
}

@Composable
private fun MobileToolsTabs(buildViewModel: BuildLogViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) }
    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            edgePadding = 16.dp,
            divider = { HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)) },
            indicator = { tabPositions ->
                if (selectedTab < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        ) {
            val tabs = listOf("Build", "Logcat", "Terminal", "Inspection")
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { 
                        Text(
                            title, 
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedTab == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        ) 
                    }
                )
            }
        }
        Box(modifier = Modifier.weight(1f)) {
            when (selectedTab) {
                0 -> BuildLogPanel(selectedTab = 1, modifier = Modifier.fillMaxSize())
                1 -> BuildLogPanel(selectedTab = 0, modifier = Modifier.fillMaxSize())
                2 -> BuildLogPanel(selectedTab = 2, modifier = Modifier.fillMaxSize())
                3 -> AppInspectionPanel(modifier = Modifier.fillMaxSize())
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
            Tab(selected = true, onClick = {}) { Text("Database", modifier = Modifier.padding(8.dp)) }
            Tab(selected = false, onClick = {}) { Text("Background", modifier = Modifier.padding(8.dp)) }
            Tab(selected = false, onClick = {}) { Text("Network", modifier = Modifier.padding(8.dp)) }
        }
        
        Spacer(Modifier.height(16.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Storage, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("app_database.db", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(8.dp))
                val tables = listOf("Users", "Notes", "Settings")
                tables.forEach { table ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(table, style = MaterialTheme.typography.bodySmall)
                        Text("24 entries", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                    }
                }
            }
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
