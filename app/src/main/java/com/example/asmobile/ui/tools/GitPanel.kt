package com.example.asmobile.ui.tools

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
    var repoUrl by remember { mutableStateOf("") }
    var commitMessage by remember { mutableStateOf("") }
    val statusMessage by viewModel.statusMessage.collectAsState()

    Column(modifier = modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
        Text(stringResource(R.string.git_operations), style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = repoUrl,
            onValueChange = { repoUrl = it },
            label = { Text(stringResource(R.string.repo_url)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            leadingIcon = { Icon(Icons.Rounded.Link, contentDescription = null) }
        )
        Button(
            onClick = {
                viewModel.clone(repoUrl)
            },
            modifier = Modifier.padding(top = 12.dp).fillMaxWidth()
        ) {
            Icon(Icons.Rounded.Download, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(stringResource(R.string.clone), maxLines = 1)
        }

        HorizontalDivider(Modifier.padding(vertical = 24.dp))

        OutlinedTextField(
            value = commitMessage,
            onValueChange = { commitMessage = it },
            label = { Text(stringResource(R.string.commit_message)) },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Rounded.EditNote, contentDescription = null) }
        )
        
        Column(
            modifier = Modifier.padding(top = 12.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    viewModel.commit("cloned_repo", commitMessage)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Rounded.Check, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.commit), maxLines = 1)
            }
            Button(
                onClick = {
                    viewModel.push("cloned_repo")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Rounded.Upload, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.push), maxLines = 1)
            }
            Button(
                onClick = {
                    viewModel.pull("cloned_repo")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Rounded.Refresh, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.pull), maxLines = 1)
            }
        }

        Spacer(Modifier.height(24.dp))
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Status: $statusMessage",
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
