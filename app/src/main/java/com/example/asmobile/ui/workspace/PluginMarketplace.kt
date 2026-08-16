package com.example.asmobile.ui.workspace

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun PluginMarketplace(
    viewModel: PluginViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    modifier: Modifier = Modifier
) {
    val plugins = remember {
        listOf(
            PluginItem("Rainbow Syntax", "High-contrast syntax highlighting for Elite Pro.", "1.2MB", 4.8f, "Editor"),
            PluginItem("Gemini Visualizer", "Advanced AI architecture graphing and flowcharts.", "5.6MB", 4.9f, "AI"),
            PluginItem("Gradle Optimizer", "Reduces build times by up to 40% on mobile.", "840KB", 4.5f, "Build"),
            PluginItem("Material 4 Preview", "Early access to the next generation design system.", "2.1MB", 4.2f, "UI"),
            PluginItem("ADB Wireless Pro", "Wireless debugging without any terminal commands.", "1.1MB", 4.7f, "Debug"),
            PluginItem("Git Graph", "Visualize your git history with interactive branches.", "1.8MB", 4.6f, "VCS"),
            PluginItem("Kotlin 2.1 support", "Early access to K2 compiler features.", "12MB", 4.9f, "Language"),
            PluginItem("Room Explorer", "Directly browse and edit your Room databases.", "2.5MB", 4.4f, "Data"),
            PluginItem("Layout Perf", "Real-time layout performance monitoring.", "3.2MB", 4.3f, "UI"),
            PluginItem("Code Spell Checker", "Fix typos in your code and comments.", "900KB", 4.1f, "Editor"),
            PluginItem("Vector Asset Studio", "Convert any SVG to Android Vector Drawable.", "4.1MB", 4.8f, "UI"),
            PluginItem("JSON to Data Class", "Generate Kotlin models from JSON strings.", "600KB", 4.9f, "Tools"),
            PluginItem("LeakCanary Mobile", "Memory leak detection for your local builds.", "3.5MB", 4.7f, "Debug"),
            PluginItem("Retrofit Inspector", "Intercept and view all network requests.", "2.2MB", 4.5f, "Network"),
            PluginItem("Firebase Console", "Mini console for Firebase Auth and Firestore.", "6.1MB", 4.6f, "Cloud"),
            PluginItem("Icon Pack: Fluent", "Official Microsoft Fluent icon set.", "2.8MB", 4.3f, "UI"),
            PluginItem("Icon Pack: FontAwesome", "Professional web icons for Android.", "3.1MB", 4.5f, "UI"),
            PluginItem("Logcat Colorizer", "Automatic tagging and coloring for logcat.", "400KB", 4.8f, "Debug"),
            PluginItem("Build Analyzer", "Detailed breakdown of where your build time goes.", "2.9MB", 4.4f, "Build"),
            PluginItem("Unit Test Hero", "AI-powered unit test generation.", "7.2MB", 4.9f, "Test"),
            PluginItem("Compose Preview Pro", "Interactive preview with multi-device support.", "15MB", 4.7f, "UI"),
            PluginItem("Java to Kotlin Converter", "Advanced conversion with AI refinement.", "8.1MB", 4.6f, "Tools"),
            PluginItem("Accessibility Scanner", "Check your UI for WCAG compliance.", "2.0MB", 4.3f, "UI"),
            PluginItem("Dark Mode Previewer", "Quick toggle system dark mode from IDE.", "300KB", 4.5f, "Tools"),
            PluginItem("Resource Shrinker", "Automatically remove unused assets.", "1.4MB", 4.2f, "Build"),
            PluginItem("Hilt Graph", "Visualize your Dagger/Hilt dependency tree.", "5.2MB", 4.8f, "Architecture"),
            PluginItem("Regex Tester", "Test regular expressions directly in editor.", "500KB", 4.1f, "Tools"),
            PluginItem("Markdown Editor", "Rich text editor for README.md files.", "2.6MB", 4.4f, "Editor"),
            PluginItem("DPI Calculator", "Instant conversion between dp and px.", "200KB", 4.6f, "UI"),
            PluginItem("Android SDK Manager", "Download SDK components on the fly.", "18MB", 4.7f, "System"),
            PluginItem("Terminal++", "Full Linux terminal with ZSH support.", "12MB", 4.9f, "Tools"),
            PluginItem("Keymap Pro", "Import VSCode or IntelliJ keybindings.", "400KB", 4.5f, "Editor"),
            PluginItem("Deep Link Tester", "Trigger deep links without shell commands.", "1.1MB", 4.4f, "Debug"),
            PluginItem("Lottie Player", "Preview animations directly in the IDE.", "3.8MB", 4.8f, "UI"),
            PluginItem("Protobuf Studio", "Full support for .proto file editing.", "2.3MB", 4.3f, "Language"),
            PluginItem("SQLite Pro", "Raw SQL editor with autocomplete.", "4.5MB", 4.6f, "Data"),
            PluginItem("Profiler Lite", "CPU and Memory tracking overlay.", "5.6MB", 4.5f, "Debug"),
            PluginItem("Package Exporter", "Prepare your app for Play Store publishing.", "2.7MB", 4.9f, "Build"),
            PluginItem("Color Palette Gen", "Generate app colors from an image.", "1.9MB", 4.4f, "UI"),
            PluginItem("Doc Generator", "Create full API documentation in HTML.", "4.2MB", 4.2f, "Tools"),
            PluginItem("Image Compressor", "Lossless compression for PNG and JPG.", "3.1MB", 4.7f, "UI"),
            PluginItem("String Translator", "Auto-translate strings.xml using AI.", "2.5MB", 4.6f, "Localization"),
            PluginItem("Dependency Guard", "Alerts you of outdated or insecure libs.", "1.2MB", 4.8f, "Build"),
            PluginItem("Crash Reporter", "Simulate and capture app crashes.", "1.6MB", 4.3f, "Debug"),
            PluginItem("WorkManager View", "Inspect background jobs and schedules.", "2.1MB", 4.5f, "Architecture"),
            PluginItem("Data Binding Pro", "Improved autocomplete for XML layouts.", "3.4MB", 4.1f, "Legacy"),
            PluginItem("Safe Args Studio", "Visual navigation graph argument editor.", "2.9MB", 4.4f, "Navigation"),
            PluginItem("Compose Metric Hub", "Performance reports for @Composable functions.", "6.2MB", 4.7f, "UI"),
            PluginItem("Emulator Controller", "Remote controls for your virtual devices.", "4.1MB", 4.6f, "Debug"),
            PluginItem("Shell Script Studio", "Enhanced editing for .sh and .bash files.", "1.3MB", 4.2f, "Tools"),
            PluginItem("Play Integrity Tool", "Verify your app's security status.", "3.5MB", 4.5f, "Security")
        )
    }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Plugin Marketplace", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
                Text("${plugins.size} Extensions available for Elite Pro", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = { }) { Icon(Icons.Rounded.Search, null) }
        }

        Spacer(Modifier.height(16.dp))

        var selectedCategoryTab by remember { mutableStateOf("All") }
        val categories = listOf("All") + plugins.map { it.category }.distinct()

        ScrollableTabRow(
            selectedTabIndex = categories.indexOf(selectedCategoryTab),
            containerColor = Color.Transparent,
            edgePadding = 0.dp,
            divider = {},
            indicator = { tabPositions ->
                if (categories.indexOf(selectedCategoryTab) < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[categories.indexOf(selectedCategoryTab)]),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        ) {
            categories.forEach { category ->
                Tab(
                    selected = selectedCategoryTab == category,
                    onClick = { selectedCategoryTab = category },
                    text = { Text(category, style = MaterialTheme.typography.labelLarge) }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        val filteredPlugins = if (selectedCategoryTab == "All") plugins else plugins.filter { it.category == selectedCategoryTab }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(filteredPlugins) { plugin ->
                PluginCard(
                    plugin = plugin,
                    isInstalled = viewModel.installedPlugins.contains(plugin.name),
                    onInstall = { viewModel.installPlugin(plugin.name) }
                )
            }
            item { Spacer(Modifier.height(100.dp)) }
        }
    }
}

data class PluginItem(val name: String, val desc: String, val size: String, val rating: Float, val category: String)

@Composable
private fun PluginCard(plugin: PluginItem, isInstalled: Boolean, onInstall: () -> Unit) {
    var isDownloading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when(plugin.category) {
                        "UI" -> Icons.Rounded.Palette
                        "Debug" -> Icons.Rounded.BugReport
                        "Build" -> Icons.Rounded.Build
                        "Data" -> Icons.Rounded.Storage
                        "Editor" -> Icons.Rounded.Edit
                        "Tools" -> Icons.Rounded.Category
                        "VCS" -> Icons.Rounded.History
                        "AI" -> Icons.Rounded.AutoAwesome
                        else -> Icons.Rounded.Extension
                    },
                    contentDescription = null, 
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(plugin.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Text(plugin.desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                
                Row(modifier = Modifier.padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Star, null, modifier = Modifier.size(12.dp), tint = Color(0xFFFBBF24))
                    Text(plugin.rating.toString(), style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(start = 4.dp))
                    Spacer(Modifier.width(12.dp))
                    Text(plugin.size, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.width(12.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(plugin.category, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
            
            Button(
                onClick = {
                    if (!isInstalled && !isDownloading) {
                        isDownloading = true
                        scope.launch {
                            kotlinx.coroutines.delay(2000) // Simulate download
                            isDownloading = false
                            onInstall()
                        }
                    }
                },
                enabled = !isInstalled && !isDownloading,
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isInstalled) Color(0xFF10B981) else MaterialTheme.colorScheme.primary
                )
            ) {
                if (isDownloading) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Color.White)
                } else if (isInstalled) {
                    Icon(Icons.Rounded.Check, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Ready", style = MaterialTheme.typography.labelLarge)
                } else {
                    Text("Install", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}
