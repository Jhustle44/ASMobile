package com.example.asmobile.ui.workspace

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.text.TextStyle

@Composable
fun ProjectExplorer(
    rootDir: File,
    onFileSelected: (File) -> Unit,
    onNewProjectClick: () -> Unit,
    onRunProject: (File) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var activeProject by remember { mutableStateOf<File?>(null) }
    val projects = remember(rootDir) {
        rootDir.listFiles { file -> file.isDirectory && !file.name.startsWith(".") }?.toList() ?: emptyList()
    }
    var searchQuery by remember { mutableStateOf("") }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        if (activeProject == null) {
            // Projects Grid/List Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Your Projects", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
                IconButton(onClick = onNewProjectClick) {
                    Icon(Icons.Rounded.AddBox, null, tint = MaterialTheme.colorScheme.primary)
                }
            }
            
            Spacer(Modifier.height(16.dp))

            // Search Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.Search, null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                    Spacer(Modifier.width(12.dp))
                    androidx.compose.foundation.text.BasicTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.weight(1f).padding(vertical = 12.dp),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
                        cursorBrush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary),
                        decorationBox = { innerTextField ->
                            if (searchQuery.isEmpty()) {
                                Text("Find project...", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                            }
                            innerTextField()
                        }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
            
            val filteredProjects = projects.filter { it.name.contains(searchQuery, ignoreCase = true) }

            if (filteredProjects.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Rounded.FolderOff, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.surfaceVariant)
                        Text("No matching projects found.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        if (projects.isEmpty()) {
                            Button(onClick = onNewProjectClick, modifier = Modifier.padding(top = 16.dp)) {
                                Text("Create My First Project")
                            }
                        }
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(filteredProjects) { project ->
                        ProjectCard(
                            project = project,
                            onClick = { activeProject = project },
                            onRun = { onRunProject(project) }
                        )
                    }
                }
            }
        } else {
            // Project Detail / File Tree for specific project
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { activeProject = null }) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, null)
                }
                Column {
                    Text(activeProject!!.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    val branch = remember(activeProject) {
                        val gitDir = File(activeProject, ".git")
                        if (gitDir.exists()) {
                            val headFile = File(gitDir, "HEAD")
                            if (headFile.exists()) {
                                try {
                                    headFile.readText().substringAfter("refs/heads/").trim()
                                } catch (e: Exception) { "detached" }
                            } else "detached"
                        } else "main"
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.AccountTree, null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(4.dp))
                        Text("branch: $branch", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
            
            Spacer(Modifier.height(16.dp))
            
            FileTree(
                rootDir = activeProject!!,
                onFileSelected = onFileSelected,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ProjectCard(project: File, onClick: () -> Unit, onRun: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(20.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.Folder, null, tint = MaterialTheme.colorScheme.primary)
            }
            
            Spacer(Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(project.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Text("Last modified: Today", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            
            IconButton(onClick = onRun) {
                Icon(Icons.Rounded.Smartphone, "View in Device", tint = MaterialTheme.colorScheme.primary)
            }
            
            Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
