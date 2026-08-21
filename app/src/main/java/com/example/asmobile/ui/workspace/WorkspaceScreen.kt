package com.example.asmobile.ui.workspace

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.draw.clip
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.Path
import kotlinx.coroutines.launch
import java.io.File
import com.example.asmobile.ui.theme.*
import androidx.compose.material3.*

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun WorkspaceScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val rootDir = remember { 
        val dir = File(context.filesDir, "Projects")
        if (!dir.exists()) dir.mkdirs()
        dir
    }
    
    // UI State
    var selectedDestination by remember { mutableStateOf(MobileDestination.Dashboard) }
    var showProjectWizard by remember { mutableStateOf(false) }
    var showSearchEverywhere by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var showAccount by remember { mutableStateOf(false) }
    var showExport by remember { mutableStateOf(false) }
    
    // Global State
    val deviceViewModel: DeviceViewModel = viewModel()
    val projectViewModel: ProjectViewModel = viewModel()
    val buildToolsViewModel: BuildToolsViewModel = viewModel()
    val themeViewModel: ThemeViewModel = viewModel()
    val pluginViewModel: PluginViewModel = viewModel()
    
    // Initialize persistence
    LaunchedEffect(rootDir) {
        deviceViewModel.initStorage(rootDir)
        pluginViewModel.initStorage(rootDir)
    }
    
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
                drawerContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp),
                modifier = Modifier.width(320.dp).border(BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)), RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp))
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.02f), Color.Transparent, Color.Black.copy(alpha = 0.05f)))))
                    
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            DrawerHeader()
                            IconButton(onClick = { scope.launch { drawerState.close() } }) {
                                Icon(Icons.Rounded.ArrowBackIosNew, "Close", modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                        
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            item {
                                NavigationDrawerItem(
                                    label = { Text("IDE Dashboard") },
                                    selected = selectedDestination == MobileDestination.Dashboard,
                                    onClick = { selectedDestination = MobileDestination.Dashboard; scope.launch { drawerState.close() } },
                                    icon = { Icon(Icons.Rounded.Dashboard, null) },
                                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                                )
                            }
                            
                            item {
                                NavigationDrawerItem(
                                    label = { Text("Virtual Device Lab") },
                                    selected = selectedDestination == MobileDestination.Devices,
                                    onClick = { selectedDestination = MobileDestination.Devices; scope.launch { drawerState.close() } },
                                    icon = { Icon(Icons.Rounded.Smartphone, null) },
                                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                                )
                            }

                            item {
                                HorizontalDivider(modifier = Modifier.padding(horizontal = 28.dp, vertical = 8.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                                Text("ADVANCED TOOLING", modifier = Modifier.padding(start = 28.dp, bottom = 8.dp, top = 8.dp), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                            }

                            item {
                                DrawerToolItem("Theme Engine", Icons.Rounded.Palette) { 
                                    selectedDestination = MobileDestination.Themes
                                    scope.launch { drawerState.close() }
                                }
                            }

                            item {
                                DrawerToolItem("Plugin Marketplace", Icons.Rounded.Extension) { 
                                    selectedDestination = MobileDestination.Plugins
                                    scope.launch { drawerState.close() }
                                }
                            }

                            item {
                                DrawerToolItem("Resource Explorer", Icons.Rounded.Source) { 
                                    selectedDestination = MobileDestination.Project
                                    scope.launch { drawerState.close() }
                                }
                            }
                            
                            item {
                                DrawerToolItem("Git History", Icons.Rounded.History) { 
                                    selectedDestination = MobileDestination.Git
                                    scope.launch { drawerState.close() }
                                }
                            }

                            item {
                                DrawerToolItem("Layout Inspector", Icons.Rounded.Layers) { 
                                    projectViewModel.selectedToolTab = 3
                                    selectedDestination = MobileDestination.Tools
                                    scope.launch { drawerState.close() }
                                }
                            }
                            
                            item {
                                DrawerToolItem("Database Inspector", Icons.Rounded.Storage) { 
                                    projectViewModel.selectedToolTab = 4
                                    selectedDestination = MobileDestination.Tools
                                    scope.launch { drawerState.close() }
                                }
                            }

                            item {
                                DrawerToolItem("Network Monitor", Icons.Rounded.Wifi) { 
                                    projectViewModel.selectedToolTab = 7
                                    selectedDestination = MobileDestination.Tools
                                    scope.launch { drawerState.close() }
                                }
                            }

                            item {
                                DrawerToolItem("App Inspection", Icons.Rounded.Search) { 
                                    projectViewModel.selectedToolTab = 8
                                    selectedDestination = MobileDestination.Tools
                                    scope.launch { drawerState.close() }
                                }
                            }

                            item {
                                HorizontalDivider(modifier = Modifier.padding(horizontal = 28.dp, vertical = 8.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                            }

                            item {
                                NavigationDrawerItem(
                                    label = { Text("Global Settings") },
                                    selected = false,
                                    onClick = { showSettings = true; scope.launch { drawerState.close() } },
                                    icon = { Icon(Icons.Rounded.Settings, null) },
                                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                                )
                            }

                            item {
                                NavigationDrawerItem(
                                    label = { Text("Developer Profile") },
                                    selected = false,
                                    onClick = { showAccount = true; scope.launch { drawerState.close() } },
                                    icon = { Icon(Icons.Rounded.Face, null) },
                                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                                )
                            }

                            item {
                                NavigationDrawerItem(
                                    label = { 
                                        Column {
                                            Text("System Environment", style = MaterialTheme.typography.labelLarge)
                                            Text("${buildToolsViewModel.sdkVersion} / ${buildToolsViewModel.jdkVersion}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                                        }
                                    },
                                    selected = false,
                                    onClick = { },
                                    icon = { Icon(Icons.Rounded.Memory, null, tint = GlowSky) },
                                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                                )
                            }

                            item {
                                Spacer(Modifier.height(40.dp))
                                Text("ASMobile v4.1-ELITE", modifier = Modifier.padding(28.dp), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                WorkspaceTopBar(
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onSearchClick = { showSearchEverywhere = true },
                    onAccountClick = { showAccount = true },
                    themeViewModel = themeViewModel
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
                        onClick = { 
                            activeFilePath?.let { path ->
                                val file = File(path)
                                val projectsDir = rootDir
                                var parent = file.parentFile
                                while (parent != null && parent.parentFile?.absolutePath != projectsDir.absolutePath) {
                                    parent = parent.parentFile
                                }
                                if (parent != null) {
                                    projectViewModel.startRun(parent)
                                }
                            }
                            
                            selectedDestination = MobileDestination.Tools
                            projectViewModel.selectedToolTab = 0
                            buildViewModel.startBuild() 
                        },
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
                val status by buildViewModel.buildStatus.collectAsState()
                val progress by buildViewModel.buildProgress.collectAsState()
                val isBuilding by buildViewModel.isBuilding.collectAsState()

                key(projectViewModel.refreshTrigger) {
                    when (selectedDestination) {
                        MobileDestination.Dashboard -> {
                            Dashboard(
                                rootDir = rootDir,
                                onFileSelected = { file -> openFile(file, openFiles, { activeFilePath = it }, { selectedDestination = it }) },
                                onNewProjectClick = { showProjectWizard = true },
                                onSyncClick = { buildViewModel.startSync() },
                                onCleanClick = { buildViewModel.startClean() },
                                buildStatus = status,
                                buildProgress = progress,
                                isBuilding = isBuilding
                            )
                        }
                        MobileDestination.Project -> ProjectExplorer(
                            rootDir = rootDir,
                            onFileSelected = { file -> openFile(file, openFiles, { activeFilePath = it }, { selectedDestination = it }) },
                            onNewProjectClick = { showProjectWizard = true },
                            onRunProject = { project ->
                                projectViewModel.startRun(project)
                                selectedDestination = MobileDestination.Devices
                                if (deviceViewModel.devices.none { it.isRunning }) {
                                    deviceViewModel.toggleDevice(0)
                                }
                            }
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
                            projectViewModel = projectViewModel,
                            modifier = Modifier.fillMaxSize()
                        )
                        MobileDestination.Devices -> VirtualDeviceScreen(
                            viewModel = deviceViewModel, 
                            onRunProject = { 
                                selectedDestination = MobileDestination.Tools
                                projectViewModel.selectedToolTab = 0
                                buildViewModel.startBuild()
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                        MobileDestination.Tools -> MobileToolsTabs(buildViewModel = buildViewModel, projectViewModel = projectViewModel)
                        MobileDestination.Plugins -> PluginMarketplace(viewModel = pluginViewModel, modifier = Modifier.fillMaxSize())
                        MobileDestination.Themes -> ThemeEngineScreen(viewModel = themeViewModel, modifier = Modifier.fillMaxSize())
                    }
                }
            }
        }

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
        if (showSettings) { SettingsScreen(onBack = { showSettings = false }) }
        if (showAccount) { AccountDialog(onDismiss = { showAccount = false }) }
        if (showExport) {
            ExportScreen(
                viewModel = buildToolsViewModel,
                onBack = { showExport = false }
            )
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
private fun WorkspaceTopBar(
    onMenuClick: () -> Unit, 
    onSearchClick: () -> Unit, 
    onAccountClick: () -> Unit,
    themeViewModel: ThemeViewModel
) {
    Surface(
        color = MaterialTheme.colorScheme.background.copy(alpha = 0.9f),
        tonalElevation = 0.dp,
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
    ) {
        CenterAlignedTopAppBar(
            title = {
                Surface(
                    onClick = onSearchClick,
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Rounded.Search, null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(12.dp))
                        Text("Search Elite...", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                        Spacer(Modifier.width(48.dp))
                        Box(
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text("⌘K", style = TextStyle(fontSize = 9.sp), color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                        }
                    }
                }
            },
            navigationIcon = {
                IconButton(onClick = onMenuClick) {
                    Icon(Icons.Rounded.Menu, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            actions = {
                IconButton(onClick = { 
                    val nextTheme = when(themeViewModel.currentTheme) {
                        ThemeMode.Obsidian -> ThemeMode.Arctic
                        ThemeMode.Arctic -> ThemeMode.Solar
                        ThemeMode.Solar -> ThemeMode.Midnight
                        ThemeMode.Midnight -> ThemeMode.Forest
                        ThemeMode.Forest -> ThemeMode.Rose
                        ThemeMode.Rose -> ThemeMode.Neon
                        ThemeMode.Neon -> ThemeMode.Vaporwave
                        ThemeMode.Vaporwave -> ThemeMode.Cyberpunk
                        ThemeMode.Cyberpunk -> ThemeMode.Obsidian
                    }
                    themeViewModel.setTheme(nextTheme)
                }) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .shadow(8.dp, CircleShape, ambientColor = MaterialTheme.colorScheme.primary)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), CircleShape)
                            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            when(themeViewModel.currentTheme) {
                                ThemeMode.Obsidian -> Icons.Rounded.DarkMode
                                ThemeMode.Arctic -> Icons.Rounded.LightMode
                                ThemeMode.Solar -> Icons.Rounded.WbSunny
                                ThemeMode.Midnight -> Icons.Rounded.Bedtime
                                ThemeMode.Forest -> Icons.Rounded.Park
                                ThemeMode.Rose -> Icons.Rounded.AutoFixHigh
                                ThemeMode.Neon -> Icons.Rounded.ElectricBolt
                                ThemeMode.Vaporwave -> Icons.Rounded.MusicNote
                                ThemeMode.Cyberpunk -> Icons.Rounded.Token
                            }, 
                            null, 
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                IconButton(
                    onClick = onAccountClick,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Surface(
                        modifier = Modifier.size(36.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Rounded.Person, null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
                        }
                    }
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
            .padding(horizontal = 28.dp, vertical = 14.dp)
            .shadow(32.dp, RoundedCornerShape(32.dp), ambientColor = GlowPurple.copy(alpha = 0.5f)),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
        shape = RoundedCornerShape(32.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            tonalElevation = 0.dp,
            modifier = Modifier.height(64.dp)
        ) {
            val essentialDestinations = listOf(
                MobileDestination.Dashboard,
                MobileDestination.Project,
                MobileDestination.Ai,
                MobileDestination.Editor,
                MobileDestination.Tools
            )
            essentialDestinations.forEach { destination ->
                val isSelected = selectedDestination == destination
                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onDestinationSelected(destination) },
                    icon = { 
                        Box(
                            modifier = if (isSelected) Modifier
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape)
                                .padding(8.dp) else Modifier
                        ) {
                            Icon(destination.icon, null, modifier = Modifier.size(if (isSelected) 22.dp else 20.dp)) 
                        }
                    },
                    label = { 
                        Text(destination.label, style = TextStyle(fontSize = 9.sp), fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium) 
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    }
}

@Composable
private fun DrawerHeader() {
    Column {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Rounded.Terminal, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
            }
        }
        Spacer(Modifier.height(16.dp))
        Text("jhustle44", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.5).sp)
        Text("ELITE DEVELOPER", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
    }
}

@Composable
private fun DrawerToolItem(label: String, icon: Vector, onClick: () -> Unit) {
    NavigationDrawerItem(
        label = { Text(label, style = MaterialTheme.typography.labelLarge) },
        selected = false,
        onClick = onClick,
        icon = { Icon(icon, null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)) },
        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
        colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
    )
}

enum class MobileDestination(val label: String, val icon: Vector) {
    Dashboard("Home", Icons.Rounded.Dashboard),
    Project("Project", Icons.Rounded.Folder),
    Ai("AI", Icons.Rounded.AutoAwesome),
    Editor("Editor", Icons.Rounded.Code),
    Tools("Tools", Icons.Rounded.Build),
    Git("Git", Icons.Rounded.History),
    Devices("Devices", Icons.Rounded.Smartphone),
    Plugins("Plugins", Icons.Rounded.Extension),
    Themes("Themes", Icons.Rounded.Palette)
}

@Composable
private fun MobileToolsTabs(
    buildViewModel: BuildLogViewModel,
    projectViewModel: ProjectViewModel = viewModel()
) {
    val selectedTab = projectViewModel.selectedToolTab
    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
            tonalElevation = 4.dp,
            border = BorderStroke(0.5.dp, Color.White.copy(alpha = 0.05f))
        ) {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                edgePadding = 16.dp,
                divider = {},
                indicator = { tabPositions ->
                    if (selectedTab < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            ) {
                val tabs = listOf("Build", "Logcat", "Terminal", "Layout", "Database", "Assets", "Colors", "Network", "Profiler", "Size", "Perms", "System")
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { projectViewModel.selectedToolTab = index },
                        text = { 
                            Text(
                                title.uppercase(), 
                                style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp),
                                maxLines = 1,
                                color = if (selectedTab == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            ) 
                        }
                    )
                }
            }
        }
        Box(modifier = Modifier.weight(1f)) {
            when (selectedTab) {
                0 -> BuildLogPanel(selectedTab = 1, modifier = Modifier.fillMaxSize())
                1 -> BuildLogPanel(selectedTab = 0, modifier = Modifier.fillMaxSize())
                2 -> TerminalPanel(modifier = Modifier.fillMaxSize())
                3 -> LayoutInspectorPanel(modifier = Modifier.fillMaxSize())
                4 -> DatabaseInspectorPanel(modifier = Modifier.fillMaxSize())
                5 -> AssetStudioPanel(modifier = Modifier.fillMaxSize())
                6 -> ColorPickerPanel(modifier = Modifier.fillMaxSize())
                7 -> NetworkInspectorPanel(modifier = Modifier.fillMaxSize())
                8 -> ProfilerPanel(modifier = Modifier.fillMaxSize())
                9 -> AppSizePanel(modifier = Modifier.fillMaxSize())
                10 -> PermissionsPanel(modifier = Modifier.fillMaxSize())
                11 -> SystemInfoPanel(modifier = Modifier.fillMaxSize())
                else -> AppInspectionPanel(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
private fun TerminalPanel(modifier: Modifier = Modifier) {
    var command by remember { mutableStateOf("") }
    val history = remember { mutableStateListOf<String>("ASMobile Elite Terminal v4.0", "Type 'help' for commands", "") }
    val listState = rememberLazyListState()

    Column(modifier = modifier.background(Color(0xFF010101)).padding(12.dp)) {
        LazyColumn(modifier = Modifier.weight(1f), state = listState) {
            items(history) { line ->
                Text(
                    text = if (line.startsWith(">")) line else "  $line",
                    color = if (line.startsWith(">")) GlowSky else Color.LightGray,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(vertical = 1.dp)
                )
            }
        }
        
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
            Text("> ", color = GlowSky, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
            androidx.compose.foundation.text.BasicTextField(
                value = command,
                onValueChange = { command = it },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(color = Color.White, fontFamily = FontFamily.Monospace, fontSize = 11.sp),
                cursorBrush = androidx.compose.ui.graphics.SolidColor(GlowSky),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Send),
                keyboardActions = androidx.compose.foundation.text.KeyboardActions(onSend = {
                    if (command.isNotBlank()) {
                        history.add("> $command")
                        val response = when(command.lowercase().trim()) {
                            "help" -> "Available: help, ls, build, clean, status, clear"
                            "ls" -> "app/  build/  libs/  src/  AndroidManifest.xml  build.gradle.kts"
                            "status" -> "All systems operational. Elite Engine v4.0"
                            "clear" -> { history.clear(); "" }
                            else -> "sh: command not found: $command"
                        }
                        if (response.isNotEmpty()) history.add(response)
                        command = ""
                    }
                })
            )
        }
    }
}

@Composable
private fun LayoutInspectorPanel(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("Layout Inspector", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Live process: com.example.asmobile", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            AssistChip(
                onClick = {}, 
                label = { Text("Live Updates", fontWeight = FontWeight.Bold) }, 
                leadingIcon = { Icon(Icons.Rounded.Bolt, null, Modifier.size(14.dp), tint = GlowEmerald) },
                colors = AssistChipDefaults.assistChipColors(labelColor = GlowEmerald)
            )
        }
        Spacer(Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Surface(
                modifier = Modifier.weight(0.35f).fillMaxHeight(),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("COMPONENT TREE", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.ExtraBold)
                    Spacer(Modifier.height(12.dp))
                    LazyColumn {
                        item { TreeItem("Scaffold", 0, true) }
                        item { TreeItem("Box (Root)", 1, false) }
                        item { TreeItem("WorkspaceBottomBar", 2, false) }
                        item { TreeItem("Box (Content)", 2, true) }
                        item { TreeItem("Dashboard", 3, false) }
                        item { TreeItem("LazyColumn", 4, false) }
                    }
                }
            }
            Surface(
                modifier = Modifier.weight(0.65f).fillMaxHeight(),
                color = Color.Black,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
            ) {
                Box(Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.02f), Color.Transparent))))
                    Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Icon(Icons.Rounded.Visibility, null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                        Spacer(Modifier.height(16.dp))
                        Text("Interactive Layout Preview", color = Color.Gray, style = MaterialTheme.typography.labelSmall)
                        Text("Tapping components highlights them in code", color = Color.DarkGray, style = TextStyle(fontSize = 10.sp))
                    }
                }
            }
        }
    }
}

@Composable
private fun NetworkInspectorPanel(modifier: Modifier = Modifier) {
    var isCapturing by remember { mutableStateOf(true) }
    
    Column(modifier = modifier.padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("Network Monitor", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
                Text(if (isCapturing) "Intercepting traffic..." else "Paused", style = MaterialTheme.typography.labelSmall, color = if (isCapturing) GlowEmerald else Color.Gray)
            }
            Row {
                IconButton(onClick = { isCapturing = !isCapturing }) {
                    Icon(if (isCapturing) Icons.Rounded.Pause else Icons.Rounded.PlayArrow, null, tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = {}) { Icon(Icons.Rounded.Block, null, tint = MaterialTheme.colorScheme.error) }
            }
        }
        Spacer(Modifier.height(16.dp))
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF020202),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
        ) {
            LazyColumn(modifier = Modifier.padding(12.dp)) {
                if (isCapturing) {
                    item { NetworkLogItem("GET", "https://api.gemini.ai/v1/models", 200, "840ms") }
                    item { NetworkLogItem("POST", "https://cloud.google.com/auth", 302, "1.1s") }
                    item { NetworkLogItem("GET", "https://github.com/jhustle44/ASMobile", 200, "420ms") }
                    item { NetworkLogItem("GET", "https://maven.google.com/ksp/compiler", 200, "2.4s") }
                    item { NetworkLogItem("POST", "https://firebase.google.com/log", 204, "150ms") }
                    item { NetworkLogItem("GET", "https://android.googleapis.com/v1/check", 401, "80ms") }
                } else {
                    item { Box(Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) { Text("Capture Paused", color = Color.Gray) } }
                }
            }
        }
    }
}

@Composable
private fun NetworkLogItem(method: String, url: String, code: Int, time: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            color = if (code < 300) GlowEmerald.copy(alpha = 0.1f) else if (code < 400) GlowGold.copy(alpha = 0.1f) else ErrorRed.copy(alpha = 0.1f),
            shape = RoundedCornerShape(4.dp),
            border = BorderStroke(0.5.dp, if (code < 300) GlowEmerald else if (code < 400) GlowGold else ErrorRed)
        ) {
            Text(method, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = TextStyle(fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (code < 300) GlowEmerald else if (code < 400) GlowGold else ErrorRed))
        }
        Spacer(Modifier.width(12.dp))
        Text(url, style = TextStyle(fontSize = 11.sp, fontFamily = FontFamily.Monospace), color = Color.LightGray, maxLines = 1, modifier = Modifier.weight(1f))
        Spacer(Modifier.width(12.dp))
        Text(code.toString(), style = TextStyle(fontSize = 10.sp), color = if (code < 400) Color.Gray else ErrorRed)
        Spacer(Modifier.width(8.dp))
        Text(time, style = TextStyle(fontSize = 10.sp), color = Color.DarkGray)
    }
}

@Composable
private fun AssetStudioPanel(modifier: Modifier = Modifier) {
    var iconScale by remember { mutableFloatStateOf(1f) }
    
    Column(modifier = modifier.padding(16.dp)) {
        Text("Elite Asset Studio", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
        Text("Design professional app resources", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(16.dp))
        
        Surface(
            modifier = Modifier.fillMaxWidth().weight(1f),
            color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.5f),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(Color.Black.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    // Glossy Reflection
                    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.03f), Color.Transparent))))
                    
                    Surface(
                        modifier = Modifier.size(100.dp * iconScale).shadow(12.dp, CircleShape, ambientColor = MaterialTheme.colorScheme.primary),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        shape = CircleShape,
                        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Rounded.AutoAwesome, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
                
                Spacer(Modifier.height(24.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text("ICON SCALE", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text("${(iconScale * 100).toInt()}%", style = MaterialTheme.typography.labelSmall, fontFamily = FontFamily.Monospace)
                }
                Slider(
                    value = iconScale, 
                    onValueChange = { iconScale = it }, 
                    valueRange = 0.5f..1.5f,
                    colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary, activeTrackColor = MaterialTheme.colorScheme.primary)
                )
                
                Spacer(Modifier.weight(1f))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(onClick = {}, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
                        Icon(Icons.Rounded.Add, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("New Asset")
                    }
                    OutlinedButton(onClick = {}, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
                        Text("Export All")
                    }
                }
            }
        }
    }
}

@Composable
private fun DatabaseInspectorPanel(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp)) {
        Text("Database Inspector", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(16.dp))
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surfaceContainerLowest.copy(alpha = 0.3f),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Rounded.Storage, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                    Spacer(Modifier.height(16.dp))
                    Text("No local databases detected", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Active Room sessions will appear here", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
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
            .padding(start = (level * 8).dp)
            .padding(vertical = 2.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent)
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Icon(
            imageVector = if (label.contains("(")) Icons.Rounded.Article else Icons.Rounded.Category, 
            contentDescription = null, 
            modifier = Modifier.size(14.dp), 
            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = label, 
            style = MaterialTheme.typography.bodySmall, 
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            softWrap = false,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun ProfilerPanel(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp)) {
        Text("Performance Profiler", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(16.dp))
        Surface(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            color = Color(0xFF050505),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
        ) {
            Box(Modifier.fillMaxSize()) {
                Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    val canvasSize = this.size
                    val p = Path()
                    p.moveTo(0f, canvasSize.height * 0.8f)
                    p.quadraticTo(canvasSize.width * 0.2f, canvasSize.height * 0.4f, canvasSize.width * 0.4f, canvasSize.height * 0.6f)
                    p.quadraticTo(canvasSize.width * 0.6f, canvasSize.height * 0.2f, canvasSize.width * 0.8f, canvasSize.height * 0.5f)
                    p.lineTo(canvasSize.width, canvasSize.height * 0.3f)
                    drawPath(p, color = GlowSky, style = Stroke(width = 4f))
                }
                Text("REAL-TIME MEMORY (MB)", modifier = Modifier.padding(12.dp), style = TextStyle(fontSize = 9.sp, fontWeight = FontWeight.Bold, color = GlowSky.copy(alpha = 0.6f)))
            }
        }
    }
}

@Composable
private fun AppSizePanel(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp)) {
        Text("APK Analyzer", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(16.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.3f),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Total Build Size", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("12.84 MB", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(16.dp))
                AssetSizeRow("Dex Classes", "6.2 MB", 0.48f, GlowPurple)
                AssetSizeRow("Resources", "4.1 MB", 0.32f, GlowBlue)
                AssetSizeRow("Assets", "1.5 MB", 0.12f, GlowEmerald)
                AssetSizeRow("Manifest", "0.04 MB", 0.08f, GlowGold)
            }
        }
    }
}

@Composable
private fun AssetSizeRow(label: String, size: String, weight: Float, color: Color) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.bodySmall)
            Text(size, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { weight }, 
            modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape), 
            color = color, 
            trackColor = color.copy(alpha = 0.1f)
        )
    }
}

@Composable
private fun PermissionsPanel(modifier: Modifier = Modifier) {
    val perms = listOf("INTERNET", "CAMERA", "ACCESS_FINE_LOCATION", "STORAGE")
    Column(modifier = modifier.padding(16.dp)) {
        Text("Manifest Permissions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))
        LazyColumn {
            items(perms) { perm ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
                    Icon(Icons.Rounded.VerifiedUser, null, tint = GlowEmerald, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(12.dp))
                    Text(perm, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
private fun SystemInfoPanel(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp)) {
        Text("System Information", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))
        Text("Android Version: 15.0", style = MaterialTheme.typography.bodyMedium)
        Text("SDK Level: 35", style = MaterialTheme.typography.bodyMedium)
        Text("Architecture: arm64-v8a", style = MaterialTheme.typography.bodyMedium)
        Text("Heap Limit: 512MB", style = MaterialTheme.typography.bodyMedium)
    }
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
            else -> NetworkInspectorPanel(modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
private fun DeviceManagerList() {
    val devices = listOf("Pixel 8 Pro (API 34)", "Pixel Fold (API 33)", "Nexus 5X (API 28)")
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(devices) { device ->
            Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Smartphone, null, modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(16.dp))
                    Text(device, style = MaterialTheme.typography.bodyLarge)
                }
                IconButton(onClick = { }) { Icon(Icons.Rounded.PlayArrow, null, tint = Color(0xFF4CAF50)) }
            }
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant)
        }
    }
}

@Composable
private fun ColorPickerPanel(modifier: Modifier = Modifier) {
    var selectedColor by remember { mutableStateOf(GlowPurple) }
    Column(modifier = modifier.padding(16.dp)) {
        Text("Color Picker", style = MaterialTheme.typography.titleSmall)
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            listOf(GlowPurple, GlowBlue, GlowEmerald, GlowGold, Color.Red, Color.Cyan).forEach { color ->
                Box(modifier = Modifier.size(40.dp).background(color, CircleShape).clickable { selectedColor = color }.border(if (selectedColor == color) 2.dp else 0.dp, Color.White, CircleShape))
            }
        }
        Spacer(Modifier.height(24.dp))
        Text("HEX: #${selectedColor.value.toString(16).substring(2).uppercase()}", style = MaterialTheme.typography.labelLarge)
    }
}
