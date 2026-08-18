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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle

@Composable
fun PluginMarketplace(
    viewModel: PluginViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    modifier: Modifier = Modifier
) {
    val plugins = remember { generateMassivePluginList() }
    val scope = rememberCoroutineScope()
    var selectedCategoryTab by remember { mutableStateOf("All") }
    val categories = remember { listOf("All") + plugins.map { it.category }.distinct().sorted() }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Elite Plugin Marketplace", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
                Text("${plugins.size} extensions available for Elite Pro", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Row {
                IconButton(onClick = { viewModel.checkUpdates() }) { Icon(Icons.Rounded.Update, "Check for Updates") }
                IconButton(onClick = { }) { Icon(Icons.Rounded.Search, null) }
            }
        }

        Spacer(Modifier.height(16.dp))

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

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            items(filteredPlugins) { plugin ->
                PluginCard(
                    plugin = plugin,
                    isInstalled = viewModel.installedPlugins.contains(plugin.name),
                    hasUpdate = viewModel.pluginUpdates.contains(plugin.name),
                    onInstall = { viewModel.installPlugin(plugin.name) },
                    onUninstall = { viewModel.uninstallPlugin(plugin.name) }
                )
            }
        }
    }
}

private fun generateMassivePluginList(): List<PluginItem> {
    val basePlugins = listOf(
        PluginItem("Rainbow Syntax", "High-contrast syntax highlighting for Elite Pro.", "1.2MB", 4.8f, "Editor"),
        PluginItem("Gemini Visualizer", "Advanced AI architecture graphing and flowcharts.", "5.6MB", 4.9f, "AI"),
        PluginItem("Gradle Optimizer", "Reduces build times by up to 40% on mobile.", "840KB", 4.5f, "Build"),
        PluginItem("Material 4 Preview", "Early access to the next generation design system.", "2.1MB", 4.2f, "UI"),
        PluginItem("ADB Wireless Pro", "Wireless debugging without any terminal commands.", "1.1MB", 4.7f, "Debug")
    )
    
    val categories = listOf("Editor", "Build", "UI", "Debug", "AI", "Cloud", "VCS", "Testing", "Security", "Performance")
    val suffix = listOf("Pro", "Helper", "Toolkit", "Extension", "Plus", "Studio", "Lite", "Elite")
    
    val generated = (1..200).map { i ->
        val cat = categories.random()
        PluginItem(
            name = "${cat} ${suffix.random()} #$i",
            desc = "Advanced utility to improve your ${cat.lowercase()} workflow in ASMobile Elite.",
            size = "${(1..20).random()}MB",
            rating = (35..50).random() / 10f,
            category = cat
        )
    }
    
    return (basePlugins + generated).sortedByDescending { it.rating }
}

data class PluginItem(val name: String, val desc: String, val size: String, val rating: Float, val category: String)

@Composable
private fun PluginCard(
    plugin: PluginItem, 
    isInstalled: Boolean, 
    hasUpdate: Boolean,
    onInstall: () -> Unit,
    onUninstall: () -> Unit
) {
    var isDownloading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(if (isInstalled) 8.dp else 0.dp, RoundedCornerShape(24.dp), ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f),
        border = BorderStroke(1.dp, if (isInstalled) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
    ) {
        Box {
            // Glossy Overlay
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.White.copy(alpha = 0.03f), Color.Transparent, Color.Black.copy(alpha = 0.05f))
                        )
                    )
            )

            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                        .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), RoundedCornerShape(16.dp)),
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
                            "Security" -> Icons.Rounded.Security
                            "Performance" -> Icons.Rounded.Speed
                            else -> Icons.Rounded.Extension
                        },
                        contentDescription = null, 
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
                
                Spacer(Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(plugin.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                        if (hasUpdate) {
                            Box(
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .size(8.dp)
                                    .background(Color.Red, androidx.compose.foundation.shape.CircleShape)
                            )
                        }
                    }
                    Text(plugin.desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
                    
                    Row(modifier = Modifier.padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Star, null, modifier = Modifier.size(12.dp), tint = Color(0xFFFBBF24))
                        Text(plugin.rating.toString(), style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(start = 4.dp))
                        Spacer(Modifier.width(12.dp))
                        Text(plugin.size, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.width(12.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(plugin.category, style = TextStyle(fontSize = 9.sp), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                
                var showMenu by remember { mutableStateOf(false) }

                Box {
                    Button(
                        onClick = {
                            if (!isInstalled && !isDownloading) {
                                isDownloading = true
                                scope.launch {
                                    kotlinx.coroutines.delay(2000)
                                    isDownloading = false
                                    onInstall()
                                }
                            } else {
                                showMenu = true
                            }
                        },
                        enabled = !isDownloading,
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isInstalled) MaterialTheme.colorScheme.surfaceContainerHigh else MaterialTheme.colorScheme.primary
                        )
                    ) {
                        if (isDownloading) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Color.White)
                        } else if (isInstalled) {
                            Icon(Icons.Rounded.Check, null, modifier = Modifier.size(16.dp), tint = Color(0xFF10B981))
                            Spacer(Modifier.width(4.dp))
                            Text("Ready", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurface)
                        } else {
                            Text("Install", style = MaterialTheme.typography.labelLarge)
                        }
                    }

                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        DropdownMenuItem(
                            text = { Text("Uninstall") },
                            onClick = { onUninstall(); showMenu = false },
                            leadingIcon = { Icon(Icons.Rounded.Delete, null, tint = MaterialTheme.colorScheme.error) }
                        )
                        if (hasUpdate) {
                            DropdownMenuItem(
                                text = { Text("Update Plugin") },
                                onClick = { /* Update logic */ showMenu = false },
                                leadingIcon = { Icon(Icons.Rounded.SystemUpdate, null, tint = Color(0xFF10B981)) }
                            )
                        }
                    }
                }
            }
        }
    }
}
