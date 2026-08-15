package com.example.asmobile.ui.workspace

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import java.io.File

@Composable
fun TabbedEditor(
    openFiles: List<String>,
    activeFilePath: String?,
    onFileSelected: (String) -> Unit,
    onFileClosed: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        if (openFiles.isNotEmpty()) {
            Surface(
                color = MaterialTheme.colorScheme.background,
                shadowElevation = 2.dp
            ) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(openFiles) { filePath ->
                        val fileName = File(filePath).name
                        val isActive = filePath == activeFilePath
                        
                        EditorTab(
                            fileName = fileName,
                            isActive = isActive,
                            onClick = { onFileSelected(filePath) },
                            onClose = { onFileClosed(filePath) }
                        )
                    }
                }
            }
        }
        
        Box(modifier = Modifier.weight(1f)) {
            Editor(filePath = activeFilePath)
        }
    }
}

@Composable
private fun EditorTab(
    fileName: String,
    isActive: Boolean,
    onClick: () -> Unit,
    onClose: () -> Unit
) {
    val backgroundColor = if (isActive) MaterialTheme.colorScheme.surface else Color.Transparent
    val contentColor = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
    
    Row(
        modifier = Modifier
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .height(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = fileName,
            style = MaterialTheme.typography.labelMedium,
            color = contentColor,
            fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.Medium
        )
        if (isActive) {
            Spacer(Modifier.width(12.dp))
            Icon(
                Icons.Rounded.Close,
                contentDescription = "Close",
                modifier = Modifier
                    .size(14.dp)
                    .clip(CircleShape)
                    .clickable { onClose() },
                tint = contentColor.copy(alpha = 0.5f)
            )
        }
    }
}
