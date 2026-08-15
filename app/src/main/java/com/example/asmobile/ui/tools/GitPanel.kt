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
                .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                .padding(8.dp)
        ) {
            Text("Changes", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            val changes = listOf("MainActivity.kt", "build.gradle.kts", "AndroidManifest.xml")
            LazyColumn {
                items(changes) { file ->
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                        Checkbox(checked = true, onCheckedChange = {}, modifier = Modifier.size(24.dp))
                        Icon(Icons.Rounded.Code, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFF7F52FF))
                        Spacer(Modifier.width(8.dp))
                        Text(file, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
        
        VerticalDivider()
        
        // Right Column: Commit details
        Column(
            modifier = Modifier
                .weight(1.2f)
                .fillMaxHeight()
                .padding(16.dp)
        ) {
            Text(stringResource(R.string.commit_message), style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = commitMessage,
                onValueChange = { commitMessage = it },
                modifier = Modifier.fillMaxWidth().weight(1f),
                placeholder = { Text("Write a commit message...") },
                textStyle = MaterialTheme.typography.bodySmall
            )
            
            Spacer(Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { viewModel.commit("cloned_repo", commitMessage) },
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("Commit", maxLines = 1, style = MaterialTheme.typography.labelLarge)
                }
                FilledTonalButton(
                    onClick = { /* Push logic */ },
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("Commit and Push", maxLines = 1, style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}
