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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
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
                tonalElevation = 1.dp
            ) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
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
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
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
    val contentColor = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
    
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(IntrinsicSize.Min)
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = fileName,
                style = MaterialTheme.typography.labelLarge,
                color = contentColor,
                fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.Medium,
                maxLines = 1
            )
            if (isActive) {
                Spacer(Modifier.width(10.dp))
                Icon(
                    Icons.Rounded.Close,
                    contentDescription = "Close",
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .clickable { onClose() }
                        .padding(2.dp),
                    tint = contentColor.copy(alpha = 0.6f)
                )
            }
        }
        
        // Active Indicator Line
        if (isActive) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}
