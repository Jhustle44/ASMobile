package com.example.asmobile.ui.workspace

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Source
import androidx.compose.material3.*
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

    val extension = file.extension
    val colorScheme = MaterialTheme.colorScheme

    Column(modifier = modifier.fillMaxSize()) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceVariant,
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = file.name,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
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
                        .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                        .padding(top = 16.dp, bottom = 16.dp, start = 12.dp, end = 8.dp)
                        .width(44.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    for (i in 1..lines) {
                        Text(
                            text = i.toString(),
                            style = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                lineHeight = 20.sp
                            ),
                            maxLines = 1
                        )
                    }
                }
                
                VerticalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                
                BasicTextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    textStyle = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 20.sp
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    visualTransformation = remember(extension, colorScheme) { 
                        SyntaxVisualTransformation(extension, colorScheme) 
                    }
                )
            }
        }
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
