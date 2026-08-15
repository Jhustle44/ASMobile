package com.example.asmobile.ui.workspace

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Redo
import androidx.compose.material.icons.automirrored.rounded.Undo
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.asmobile.R
import java.io.File
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.BorderStroke

@Composable
fun Editor(
    filePath: String?,
    modifier: Modifier = Modifier,
) {
    if (filePath == null) {
        Column(
            modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Rounded.Source,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.surfaceVariant
            )
            Spacer(Modifier.height(32.dp))
            ShortcutHint("Search Everywhere", "Double Shift")
            ShortcutHint("Run", "Ctrl + R")
            ShortcutHint("Open File", "Ctrl + O")
        }
        return
    }

    val file = remember(filePath) { File(filePath) }
    var text by remember(filePath) {
        mutableStateOf(
            try {
                file.readText()
            } catch (e: Exception) {
                "Error reading file: ${e.message}"
            }
        )
    }
    
    val undoStack = remember(filePath) { mutableStateListOf<String>() }
    val redoStack = remember(filePath) { mutableStateListOf<String>() }

    val extension = file.extension
    val colorScheme = MaterialTheme.colorScheme

    Column(modifier = modifier.fillMaxSize()) {
        // Breadcrumbs Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Rounded.FolderOpen, null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.width(8.dp))
                Text("src", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Icon(Icons.Rounded.ChevronRight, null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("main", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Icon(Icons.Rounded.ChevronRight, null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(file.name, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        
        val scrollState = rememberScrollState()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                // Line numbers gutter
                val lines = text.split("\n").size
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f))
                        .padding(top = 16.dp, bottom = 16.dp, start = 12.dp, end = 12.dp)
                        .width(52.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    for (i in 1..lines) {
                        Text(
                            text = i.toString(),
                            style = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                                lineHeight = 24.sp
                            ),
                            maxLines = 1
                        )
                    }
                }
                
                VerticalDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), modifier = Modifier.width(1.dp))
                
                BasicTextField(
                    value = text,
                    onValueChange = { 
                        if (it != text) {
                            undoStack.add(text)
                            redoStack.clear()
                            text = it 
                            try {
                                file.writeText(it)
                            } catch (e: Exception) {
                                // Handle write error
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    textStyle = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 24.sp
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    visualTransformation = remember(extension, colorScheme) { 
                        SyntaxVisualTransformation(extension, colorScheme) 
                    }
                )
            }
            
            // Editor Controls and Hints
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                horizontalAlignment = Alignment.End
            ) {
                // Undo/Redo Group
                Row(
                    modifier = Modifier.padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    EditorSmallButton(Icons.AutoMirrored.Rounded.Undo, "Undo") {
                        if (undoStack.isNotEmpty()) {
                            redoStack.add(text)
                            text = undoStack.removeAt(undoStack.size - 1)
                            file.writeText(text)
                        }
                    }
                    EditorSmallButton(Icons.AutoMirrored.Rounded.Redo, "Redo") {
                        if (redoStack.isNotEmpty()) {
                            undoStack.add(text)
                            text = redoStack.removeAt(redoStack.size - 1)
                            file.writeText(text)
                        }
                    }
                }

                // Floating Status Indicator
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f),
                    shape = CircleShape,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Text(
                        text = "Ln 1, Col 1",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Suggestion Bar (Simulated IntelliSense)
            SuggestionBar(
                onInsert = { suggestion ->
                    undoStack.add(text)
                    redoStack.clear()
                    text += suggestion
                    file.writeText(text)
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
                    .padding(vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun EditorSmallButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    description: String,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.size(36.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
        shape = CircleShape,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.clickable(onClick = onClick)) {
            Icon(icon, description, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun SuggestionBar(
    onInsert: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val suggestions = listOf("composable", "modifier", "val", "var", "Modifier.fillMaxSize()", "println")
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(suggestions) { suggestion ->
            SuggestionChip(suggestion) { onInsert(suggestion) }
        }
    }
}

@Composable
private fun SuggestionChip(text: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ShortcutHint(label: String, keys: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.width(16.dp))
        Text(
            text = keys,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
    }
}

private class SyntaxVisualTransformation(
    private val extension: String,
    private val colorScheme: ColorScheme
) : VisualTransformation {
    override fun filter(text: androidx.compose.ui.text.AnnotatedString): TransformedText {
        return TransformedText(
            SyntaxHighlighter.highlight(text.text, extension, colorScheme),
            OffsetMapping.Identity
        )
    }
}
