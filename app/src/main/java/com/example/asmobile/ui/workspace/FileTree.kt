package com.example.asmobile.ui.workspace

import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontWeight
import com.example.asmobile.R
import java.io.File

@Composable
fun FileTree(
    rootDir: File,
    onFileSelected: (File) -> Unit,
    modifier: Modifier = Modifier,
) {
    val expandedPaths = remember { mutableStateMapOf<String, Boolean>(rootDir.path to true) }

    val fileItems = remember(expandedPaths.size) {
        val list = mutableListOf<FileTreeItem>()
        addFilesToList(rootDir, 0, expandedPaths, list)
        list
    }

    var viewMode by remember { mutableStateOf("Android") }
    var showViewMenu by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceContainerLow
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box {
                    Row(
                        modifier = Modifier.clickable { showViewMenu = true },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = viewMode,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Icon(
                            Icons.Rounded.ArrowDropDown,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    DropdownMenu(
                        expanded = showViewMenu,
                        onDismissRequest = { showViewMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Android") },
                            onClick = { viewMode = "Android"; showViewMenu = false },
                            leadingIcon = { Icon(Icons.Rounded.Android, contentDescription = null, modifier = Modifier.size(18.dp)) }
                        )
                        DropdownMenuItem(
                            text = { Text("Project") },
                            onClick = { viewMode = "Project"; showViewMenu = false },
                            leadingIcon = { Icon(Icons.Rounded.Folder, contentDescription = null, modifier = Modifier.size(18.dp)) }
                        )
                    }
                }
                
                Row {
                    IconButton(onClick = { 
                        val dummy = expandedPaths.size
                        expandedPaths.clear()
                        expandedPaths[rootDir.path] = true
                    }) {
                        Icon(Icons.Rounded.Refresh, contentDescription = "Refresh", modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = { /* Could trigger project wizard */ }) {
                        Icon(Icons.Rounded.Add, contentDescription = "New", modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = { expandedPaths.clear(); expandedPaths[rootDir.path] = true }) {
                        Icon(Icons.Rounded.UnfoldLess, contentDescription = "Collapse All", modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(fileItems) { item ->
                FileRow(
                    item = item,
                    isExpanded = expandedPaths[item.file.path] == true,
                    onRefresh = {
                        if (item.file.isDirectory) {
                            val current = expandedPaths[item.file.path] ?: false
                            expandedPaths[item.file.path] = !current
                        }
                    },
                    onFileSelected = onFileSelected
                )
            }
        }
    }
}

data class FileTreeItem(val file: File, val level: Int)

private fun addFilesToList(
    dir: File,
    level: Int,
    expandedPaths: Map<String, Boolean>,
    list: MutableList<FileTreeItem>
) {
    val files = dir.listFiles()?.sortedWith(compareBy({ !it.isDirectory }, { it.name })) ?: return
    for (file in files) {
        if (file.name.startsWith(".")) continue
        
        list.add(FileTreeItem(file, level))
        if (file.isDirectory && (expandedPaths[file.path] == true)) {
            addFilesToList(file, level + 1, expandedPaths, list)
        }
    }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
private fun FileRow(
    item: FileTreeItem,
    isExpanded: Boolean,
    onRefresh: () -> Unit,
    onFileSelected: (File) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }
    val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    if (item.file.isDirectory) {
                        onRefresh() // Actually this should toggle expansion but let's stick to the callback
                    } else {
                        onFileSelected(item.file)
                    }
                },
                onLongClick = { showMenu = true }
            )
            .padding(horizontal = 12.dp, vertical = 1.dp),
        shape = RoundedCornerShape(2.dp),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 6.dp)
                .padding(start = (item.level * 12).dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when {
                    item.file.isDirectory && isExpanded -> Icons.Rounded.KeyboardArrowDown
                    item.file.isDirectory -> Icons.AutoMirrored.Rounded.KeyboardArrowRight
                    else -> Icons.Rounded.ChevronRight
                },
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                imageVector = when {
                    item.file.isDirectory -> Icons.Rounded.Folder
                    item.file.name == "AndroidManifest.xml" -> Icons.Rounded.Article
                    item.file.extension == "kt" -> Icons.Rounded.Code
                    item.file.extension == "java" -> Icons.Rounded.Code
                    item.file.extension == "gradle" || item.file.name.endsWith(".gradle.kts") -> Icons.Rounded.Build
                    item.file.extension == "xml" -> Icons.Rounded.SettingsEthernet
                    item.file.extension == "json" -> Icons.Rounded.Settings
                    else -> Icons.Rounded.Description
                },
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = when {
                    item.file.isDirectory -> MaterialTheme.colorScheme.primary
                    item.file.name == "AndroidManifest.xml" -> Color(0xFFF44336)
                    item.file.extension == "kt" -> Color(0xFF7F52FF)
                    item.file.extension == "java" -> Color(0xFFE76F51)
                    item.file.extension == "gradle" || item.file.name.endsWith(".gradle.kts") -> Color(0xFF005C97)
                    else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                }
            )
            Text(
                text = item.file.name,
                modifier = Modifier.padding(start = 12.dp).weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = if (item.file.isDirectory) FontWeight.SemiBold else FontWeight.Normal,
                maxLines = 1
            )
            
            Box {
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    if (item.file.isDirectory) {
                        DropdownMenuItem(
                            text = { Text("New Kotlin File") },
                            onClick = { 
                                val newFile = File(item.file, "NewFile.kt")
                                newFile.createNewFile()
                                newFile.writeText("package com.example.asmobile\n\nimport androidx.compose.runtime.Composable\n\n@Composable\nfun NewScreen() {\n\n}")
                                showMenu = false
                                onRefresh() // Refresh tree
                            },
                            leadingIcon = { Icon(Icons.Rounded.Add, null, Modifier.size(18.dp)) }
                        )
                        DropdownMenuItem(
                            text = { Text("New Directory") },
                            onClick = { 
                                File(item.file, "new_folder").mkdirs()
                                showMenu = false
                                onRefresh()
                            },
                            leadingIcon = { Icon(Icons.Rounded.CreateNewFolder, null, Modifier.size(18.dp)) }
                        )
                        HorizontalDivider()
                    }
                    
                    DropdownMenuItem(
                        text = { Text("Rename") },
                        onClick = { 
                            showRenameDialog = true
                            showMenu = false 
                        },
                        leadingIcon = { Icon(Icons.Rounded.Edit, null, Modifier.size(18.dp)) }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete") },
                        onClick = { 
                            if (item.file.deleteRecursively()) {
                                showMenu = false 
                                onRefresh() 
                            }
                        },
                        leadingIcon = { Icon(Icons.Rounded.Delete, null, Modifier.size(18.dp), tint = MaterialTheme.colorScheme.error) }
                    )
                    HorizontalDivider()
                    DropdownMenuItem(
                        text = { Text("Copy Path") },
                        onClick = { 
                            clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(item.file.absolutePath))
                            showMenu = false 
                        },
                        leadingIcon = { Icon(Icons.Rounded.ContentCopy, null, Modifier.size(18.dp)) }
                    )
                }
            }
        }
    }

    if (showRenameDialog) {
        RenameDialog(
            currentName = item.file.name,
            onDismiss = { showRenameDialog = false },
            onRename = { newName ->
                val newFile = File(item.file.parentFile, newName)
                item.file.renameTo(newFile)
                showRenameDialog = false
                onRefresh()
            }
        )
    }
}

@Composable
private fun RenameDialog(currentName: String, onDismiss: () -> Unit, onRename: (String) -> Unit) {
    var name by remember { mutableStateOf(currentName) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Rename Item") },
        text = {
            OutlinedTextField(
                value = name, 
                onValueChange = { name = it }, 
                label = { Text("New Name") },
                shape = RoundedCornerShape(12.dp)
            )
        },
        confirmButton = {
            Button(onClick = { onRename(name) }) { Text("Rename") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
