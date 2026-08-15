package com.example.asmobile.ui.tools

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.asmobile.R
import java.io.File
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GitPanel(rootDir: File, modifier: Modifier = Modifier) {
    val viewModel: GitViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                GitViewModel(rootDir)
            }
        },
    )
    var commitMessage by remember { mutableStateOf("") }

    Row(modifier = modifier.padding(8.dp)) {
        // Left Column: Changes list
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.background, RoundedCornerShape(4.dp))
                .padding(12.dp)
        ) {
            Text("Changes", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(12.dp))
            val changes = listOf("MainActivity.kt", "build.gradle.kts", "AndroidManifest.xml")
            LazyColumn {
                items(changes) { file ->
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                        Checkbox(
                            checked = true, 
                            onCheckedChange = {}, 
                            modifier = Modifier.size(20.dp),
                            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                        )
                        Spacer(Modifier.width(8.dp))
                        Icon(Icons.Rounded.Code, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFF7F52FF).copy(alpha = 0.8f))
                        Spacer(Modifier.width(8.dp))
                        Text(file, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }
        
        Spacer(Modifier.width(8.dp))
        
        // Right Column: Commit details
        Column(
            modifier = Modifier
                .weight(1.2f)
                .fillMaxHeight()
                .padding(8.dp)
        ) {
            Text(stringResource(R.string.commit_message), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = commitMessage,
                onValueChange = { commitMessage = it },
                modifier = Modifier.fillMaxWidth().weight(1f),
                placeholder = { Text("Write a commit message...", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) },
                textStyle = MaterialTheme.typography.bodySmall,
                shape = RoundedCornerShape(4.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )
            
            Spacer(Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { viewModel.commit("cloned_repo", commitMessage) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Text("Commit", maxLines = 1, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                }
                FilledTonalButton(
                    onClick = { /* Push logic */ },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Text("Push", maxLines = 1, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
