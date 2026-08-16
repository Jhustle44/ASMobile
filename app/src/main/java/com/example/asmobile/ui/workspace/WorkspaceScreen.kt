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
import androidx.compose.runtime.key
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import kotlinx.coroutines.launch
import java.io.File
import com.example.asmobile.ui.theme.*

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun WorkspaceScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val rootDir = remember { context.filesDir }
    
    // UI State
    var selectedDestination by remember { mutableStateOf(MobileDestination.Dashboard) }
    var showProjectWizard by remember { mutableStateOf(false) }
    var showSearchEverywhere by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var showAccount by remember { mutableStateOf(false) }
    
    // Global State
    val deviceViewModel: DeviceViewModel = viewModel()
    val projectViewModel: ProjectViewModel = viewModel()
    
    // Project State
    val openFiles = remember { mutableStateListOf<String>() }
    var activeFilePath by remember { mutableStateOf<String?>(null) }
    
    val buildViewModel: BuildLogViewModel = viewModel()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = selectedDestination == MobileDestination.Dashboard,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surface,
                drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
            ) {
                DrawerHeader()
                Spacer(Modifier.height(12.dp))
                
                NavigationDrawerItem(
                    label = { Text("Dashboard") },
                    selected = selectedDestination == MobileDestination.Dashboard,
                    onClick = { selectedDestination = MobileDestination.Dashboard; scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Rounded.Dashboard, null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    label = { Text("Virtual Devices") },
                    selected = selectedDestination == MobileDestination.Devices,
                    onClick = { selectedDestination = MobileDestination.Devices; scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Rounded.Smartphone, null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    label = { Text("Settings") },
                    selected = false,
                    onClick = { showSettings = true; scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Rounded.Settings, null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                
                HorizontalDivider(modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.outlineVariant)
                
                Text("Project Tools", modifier = Modifier.padding(start = 28.dp, bottom = 12.dp), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                
                DrawerToolItem("Resource Explorer", Icons.Rounded.Category) { 
                    selectedDestination = MobileDestination.Project
                    scope.launch { drawerState.close() }
                }
                DrawerToolItem("Layout Inspector", Icons.Rounded.Layers) { 
                    projectViewModel.selectedToolTab = 3
                    selectedDestination = MobileDestination.Tools
                    scope.launch { drawerState.close() }
                }
                DrawerToolItem("Database Inspector", Icons.Rounded.Storage) { 
                    projectViewModel.selectedToolTab = 4
                    selectedDestination = MobileDestination.Tools
                    scope.launch { drawerState.close() }
                }
                DrawerToolItem("App Inspection", Icons.Rounded.Search) { 
                    projectViewModel.selectedToolTab = 5
                    selectedDestination = MobileDestination.Tools
                    scope.launch { drawerState.close() }
                }
                DrawerToolItem("Dependency Manager", Icons.Rounded.Layers) { 
                    projectViewModel.selectedToolTab = 0 
                    selectedDestination = MobileDestination.Tools
                    scope.launch { drawerState.close() }
                }
                DrawerToolItem("Cloud Sync (Beta)", Icons.Rounded.CloudSync) { 
                    showAccount = true
                    scope.launch { drawerState.close() }
                }
                
                Spacer(Modifier.weight(1f))
                Text("v2.5-ELITE", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    ) {
        Scaffold(
            topBar = {
                WorkspaceTopBar(
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onSearchClick = { showSearchEverywhere = true },
                    onAccountClick = { showAccount = true }
                )
            },
            bottomBar = {
                WorkspaceBottomBar(
                    selectedDestination = selectedDestination,
                    onDestinationSelected = { selectedDestination = it }
                )
            },
            floatingActionButton = {
                if (selectedDestination == MobileDestination.Editor) {
                    ExtendedFloatingActionButton(
                        onClick = { buildViewModel.startBuild() },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        shape = RoundedCornerShape(16.dp),
                        icon = { Icon(Icons.Rounded.PlayArrow, null) },
                        text = { Text("Build & Run") },
                        modifier = Modifier.padding(bottom = 80.dp)
                    )
                }
            },
            modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
        ) { padding ->
            Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                key(projectViewModel.refreshTrigger) {
                    when (selectedDestination) {
                        MobileDestination.Dashboard -> Dashboard(
                            rootDir = rootDir,
                            onFileSelected = { file -> openFile(file, openFiles, { activeFilePath = it }, { selectedDestination = it }) },
                            onNewProjectClick = { showProjectWizard = true },
                            onSyncClick = { buildViewModel.startBuild() },
                            onCleanClick = { buildViewModel.clearLogs() }
                        )
                        MobileDestination.Project -> FileTree(
                            rootDir = rootDir,
                            onFileSelected = { file -> openFile(file, openFiles, { activeFilePath = it }, { selectedDestination = it }) }
                        )
                        MobileDestination.Editor -> TabbedEditor(
                            openFiles = openFiles,
                            activeFilePath = activeFilePath,
                            onFileSelected = { activeFilePath = it },
                            onFileClosed = { path ->
                                openFiles.remove(path)
                                if (activeFilePath == path) activeFilePath = openFiles.lastOrNull()
                                if (openFiles.isEmpty()) selectedDestination = MobileDestination.Dashboard
                            }
                        )
                        MobileDestination.Git -> GitPanel(rootDir = rootDir, modifier = Modifier.fillMaxSize())
                        MobileDestination.Ai -> AiAssistantPanel(
                            rootDir = rootDir,
                            activeFilePath = activeFilePath,
                            onFileSelected = { file -> openFile(file, openFiles, { activeFilePath = it }, { selectedDestination = it }) },
                            onProjectCreated = { projectViewModel.notifyProjectCreated("AI") },
                            modifier = Modifier.fillMaxSize()
                        )
                        MobileDestination.Devices -> VirtualDeviceScreen(viewModel = deviceViewModel, modifier = Modifier.fillMaxSize())
                        MobileDestination.Tools -> MobileToolsTabs(buildViewModel = buildViewModel, projectViewModel = projectViewModel)
                    }
                }
            }
        }

        // Overlay Dialogs
        if (showProjectWizard) {
            NewProjectWizard(
                baseDir = rootDir,
                onDismiss = { showProjectWizard = false },
                onProjectCreated = { name ->
                    projectViewModel.notifyProjectCreated(name)
                    showProjectWizard = false
                }
            )
        }

        if (showSearchEverywhere) {
            SearchEverywhere(
                rootDir = rootDir,
                onDismiss = { showSearchEverywhere = false },
                onFileSelected = { file -> openFile(file, openFiles, { activeFilePath = it }, { selectedDestination = it }) }
            )
        }

        if (showSettings) {
            SettingsScreen(onBack = { showSettings = false })
        }

        if (showAccount) {
            AccountDialog(onDismiss = { showAccount = false })
        }
    }
}

private fun openFile(
    file: File, 
    openFiles: MutableList<String>, 
    setActive: (String) -> Unit, 
    setDest: (MobileDestination) -> Unit
) {
    if (!openFiles.contains(file.absolutePath)) {
        openFiles.add(file.absolutePath)
    }
    setActive(file.absolutePath)
    setDest(MobileDestination.Editor)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WorkspaceTopBar(onMenuClick: () -> Unit, onSearchClick: () -> Unit, onAccountClick: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.background,
        tonalElevation = 2.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        CenterAlignedTopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Rounded.AutoAwesome, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("ASMobile Pro", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.ExtraBold)
                }
            },
            navigationIcon = {
                IconButton(onClick = onMenuClick) {
                    Icon(Icons.Rounded.Menu, null, tint = MaterialTheme.colorScheme.primary)
                }
            },
            actions = {
                IconButton(onClick = onSearchClick) {
                    Icon(Icons.Rounded.Search, null)
                }
                IconButton(onClick = onAccountClick) {
                    Icon(Icons.Rounded.AccountCircle, null)
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
        )
    }
}

@Composable
private fun WorkspaceBottomBar(
    selectedDestination: MobileDestination,
    onDestinationSelected: (MobileDestination) -> Unit
) {
    Surface(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 12.dp)
            .shadow(16.dp, RoundedCornerShape(28.dp)),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.98f),
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
            NavigationBar(
                containerColor = Color.Transparent,
                tonalElevation = 0.dp,
                modifier = Modifier.height(60.dp)
            ) {
                MobileDestination.entries.forEach { destination ->
                    val isSelected = selectedDestination == destination
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { onDestinationSelected(destination) },
                        icon = { 
                            Icon(
                                destination.icon, 
                                null,
                                modifier = Modifier.size(if (isSelected) 20.dp else 18.dp)
                            ) 
                        },
                        label = { 
                            Text(
                                destination.label, 
                                style = TextStyle(fontSize = 8.sp),
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            ) 
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        )
                    )
                }
            }
    }
}

@Composable
private fun DrawerHeader() {
    Column(modifier = Modifier.padding(24.dp)) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    Brush.linearGradient(colors = listOf(GlowPurple, GlowBlue)),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.Source, null, tint = Color.White, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.height(12.dp))
        Text("jhustle44", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
        Text("Premium Developer", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun DrawerToolItem(label: String, icon: Vector, onClick: () -> Unit) {
    NavigationDrawerItem(
        label = { Text(label, style = MaterialTheme.typography.labelMedium) },
        selected = false,
        onClick = onClick,
        icon = { Icon(icon, null, modifier = Modifier.size(18.dp)) },
        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
    )
}

enum class MobileDestination(val label: String, val icon: Vector) {
    Dashboard("Home", Icons.Rounded.Dashboard),
    Project("Project", Icons.Rounded.Folder),
    Ai("AI", Icons.Rounded.AutoAwesome),
    Editor("Editor", Icons.Rounded.Code),
    Git("Git", Icons.Rounded.History),
    Devices("Devices", Icons.Rounded.Smartphone),
    Tools("Tools", Icons.Rounded.Build)
}

@Composable
private fun MobileToolsTabs(
    buildViewModel: BuildLogViewModel,
    projectViewModel: ProjectViewModel = viewModel()
) {
    val selectedTab = projectViewModel.selectedToolTab
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
            val tabs = listOf("Build", "Logcat", "Terminal", "Layout", "Database", "Inspection")
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { projectViewModel.selectedToolTab = index },
                    text = { 
                        Text(
                            title, 
                            style = TextStyle(fontSize = 10.sp),
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
                3 -> LayoutInspectorPanel(modifier = Modifier.fillMaxSize())
                4 -> DatabaseInspectorPanel(modifier = Modifier.fillMaxSize())
                5 -> AppInspectionPanel(modifier = Modifier.fillMaxSize())
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
    var selectedTab by remember { mutableIntStateOf(0) }
    Column(modifier = modifier.padding(16.dp)) {
        Text("App Inspection", style = MaterialTheme.typography.titleSmall)
        Spacer(Modifier.height(16.dp))
        TabRow(selectedTabIndex = selectedTab, containerColor = Color.Transparent) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) { Text("Build", modifier = Modifier.padding(8.dp)) }
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) { Text("Devices", modifier = Modifier.padding(8.dp)) }
            Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }) { Text("Network", modifier = Modifier.padding(8.dp)) }
        }
        
        Spacer(Modifier.height(16.dp))
        
        when (selectedTab) {
            0 -> BuildLogPanel(selectedTab = 1, modifier = Modifier.fillMaxSize())
            1 -> DeviceManagerList()
            else -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Network Inspector") }
        }
    }
}

@Composable
private fun DeviceManagerList() {
    val devices = listOf("Pixel 8 Pro (API 34)", "Pixel Fold (API 33)", "Nexus 5X (API 28)")
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(devices) { device ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Smartphone, null, modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(16.dp))
                    Text(device, style = MaterialTheme.typography.bodyLarge)
                }
                IconButton(onClick = { }) {
                    Icon(Icons.Rounded.PlayArrow, null, tint = Color(0xFF4CAF50))
                }
            }
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant)
        }
    }
}

@Composable
private fun LayoutInspectorPanel(modifier: Modifier = Modifier) {
    var selectedView by remember { mutableStateOf("MainActivity") }
    
    Column(modifier = modifier.padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Layout Inspector: $selectedView", style = MaterialTheme.typography.titleSmall)
            AssistChip(onClick = {}, label = { Text("Live Updates") }, leadingIcon = { Icon(Icons.Rounded.Bolt, null, Modifier.size(14.dp)) })
        }
        Spacer(Modifier.height(16.dp))
        
        Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Surface(
                modifier = Modifier.weight(0.4f).fillMaxHeight(),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Component Tree", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(12.dp))
                    TreeItem("Scaffold", 0, true)
                    TreeItem("Box", 1, false)
                    TreeItem("Column", 2, true)
                    TreeItem("Text (\"Hello World\")", 3, false)
                    TreeItem("Button", 2, false)
                }
            }
            
            Surface(
                modifier = Modifier.weight(0.6f).fillMaxHeight(),
                color = Color.Black,
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("Live View Mirror", color = Color.Gray, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
private fun TreeItem(label: String, level: Int, isSelected: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = (level * 12).dp)
            .padding(vertical = 4.dp)
            .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(Icons.Rounded.Category, null, modifier = Modifier.size(14.dp), tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.width(8.dp))
        Text(label, style = MaterialTheme.typography.bodySmall, color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun DatabaseInspectorPanel(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp)) {
        Text("Database Inspector", style = MaterialTheme.typography.titleSmall)
        Spacer(Modifier.height(24.dp))
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Rounded.Storage, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.surfaceVariant)
                Spacer(Modifier.height(16.dp))
                Text("No active databases found.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Run an app with Room to inspect data.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
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
