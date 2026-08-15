package com.example.asmobile.ui.workspace

import androidx.compose.foundation.clickable
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

    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = stringResource(R.string.project_explorer),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp),
        )
        HorizontalDivider()
        LazyColumn {
            items(fileItems) { item ->
                FileRow(
                    item = item,
                    isExpanded = expandedPaths[item.file.path] == true,
                ) {
                    if (item.file.isDirectory) {
                        val current = expandedPaths[item.file.path] ?: false
                        expandedPaths[item.file.path] = !current
                    } else {
                        onFileSelected(item.file)
                    }
                }
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

@Composable
private fun FileRow(
    item: FileTreeItem,
    isExpanded: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 2.dp),
        shape = MaterialTheme.shapes.small,
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .padding(start = (item.level * 16).dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when {
                    item.file.isDirectory && isExpanded -> Icons.Rounded.KeyboardArrowDown
                    item.file.isDirectory -> Icons.AutoMirrored.Rounded.KeyboardArrowRight
                    else -> Icons.Rounded.ChevronRight // Placeholder for file
                },
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = when {
                    item.file.isDirectory -> Icons.Rounded.Folder
                    item.file.extension == "kt" -> Icons.Rounded.Code
                    item.file.extension == "java" -> Icons.Rounded.Code
                    item.file.extension == "xml" -> Icons.Rounded.SettingsEthernet
                    else -> Icons.Rounded.Description
                },
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = if (item.file.isDirectory) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
            )
            Text(
                text = item.file.name,
                modifier = Modifier.padding(start = 12.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
