package com.example.asmobile.ui.workspace

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.asmobile.project.ProjectManager
import com.example.asmobile.project.ProjectTemplate
import com.example.asmobile.project.ProjectLanguage
import java.io.File
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.BorderStroke

@Composable
fun NewProjectWizard(
    baseDir: File,
    onDismiss: () -> Unit,
    onProjectCreated: (String) -> Unit
) {
    var projectName by remember { mutableStateOf("MyAwesomeApp") }
    var packageName by remember { mutableStateOf("com.example.awesomeapp") }
    var selectedTemplate by remember { mutableStateOf(ProjectTemplate.EmptyCompose) }
    var selectedLanguage by remember { mutableStateOf(ProjectLanguage.Kotlin) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Create New Project",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Rounded.Close, null)
                    }
                }
                
                Spacer(Modifier.height(24.dp))
                
                OutlinedTextField(
                    value = projectName,
                    onValueChange = { projectName = it },
                    label = { Text("Project Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                
                Spacer(Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = packageName,
                    onValueChange = { packageName = it },
                    label = { Text("Package Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                
                Spacer(Modifier.height(24.dp))
                
                Text("Select Language", style = MaterialTheme.typography.labelLarge, modifier = Modifier.align(Alignment.Start))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    ProjectLanguage.entries.forEach { lang ->
                        FilterChip(
                            selected = selectedLanguage == lang,
                            onClick = { selectedLanguage = lang },
                            label = { Text(lang.label) },
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
                
                Spacer(Modifier.height(24.dp))
                
                Text("Select Template", style = MaterialTheme.typography.labelLarge, modifier = Modifier.align(Alignment.Start))
                Spacer(Modifier.height(8.dp))
                
                ProjectTemplate.entries.forEach { template ->
                    TemplateItem(
                        template = template,
                        isSelected = selectedTemplate == template,
                        onClick = { selectedTemplate = template }
                    )
                    Spacer(Modifier.height(8.dp))
                }
                
                Spacer(Modifier.height(24.dp))
                
                Button(
                    onClick = {
                        try {
                            ProjectManager.createNewProject(baseDir, projectName, packageName, selectedTemplate, selectedLanguage)
                            onProjectCreated(projectName)
                            onDismiss()
                        } catch (e: Exception) {
                            // UI feedback for error
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Finish and Create", modifier = Modifier.padding(8.dp))
                }
            }
        }
    }
}

@Composable
private fun TemplateItem(
    template: ProjectTemplate,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp).fillMaxWidth()) {
            Text(template.label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Text(template.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
