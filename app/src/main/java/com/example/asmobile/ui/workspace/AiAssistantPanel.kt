package com.example.asmobile.ui.workspace

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
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

@Composable
fun AiAssistantPanel(
    rootDir: File,
    activeFilePath: String?,
    onFileSelected: (File) -> Unit,
    modifier: Modifier = Modifier
) {
    var message by remember { mutableStateOf("") }
    val chatHistory = remember { mutableStateListOf<ChatMessage>() }
    val scope = rememberCoroutineScope()

    Column(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)) {
        // Chat Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Rounded.AutoAwesome, null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(12.dp))
                Text("Gemini in ASMobile", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        }

        // Chat Messages
        LazyColumn(
            modifier = Modifier.weight(1f).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (chatHistory.isEmpty()) {
                item {
                    Box(Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            "I have full access to your project. How can I help?",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            items(chatHistory) { msg ->
                ChatBubble(msg)
            }
        }

        // Input Area
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Ask Gemini...") },
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 5
                )
                Spacer(Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (message.isNotBlank()) {
                            chatHistory.add(ChatMessage(message, true))
                            processAiCommand(message, rootDir, activeFilePath, onFileSelected) { response ->
                                chatHistory.add(ChatMessage(response, false))
                            }
                            message = ""
                        }
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(Icons.AutoMirrored.Rounded.Send, null)
                }
            }
        }
    }
}

private fun processAiCommand(
    input: String,
    rootDir: File,
    activeFilePath: String?,
    onFileSelected: (File) -> Unit,
    onResponse: (String) -> Unit
) {
    val lowInput = input.lowercase()
    
    when {
        lowInput.contains("create file") || lowInput.contains("new file") -> {
            val fileName = input.split(" ").last()
            val newFile = File(rootDir, fileName)
            try {
                newFile.createNewFile()
                newFile.writeText("// Created by Gemini AI\npackage com.example.asmobile\n\n")
                onFileSelected(newFile)
                onResponse("Created '$fileName' and opened it for you.")
            } catch (e: Exception) {
                onResponse("Error creating file: ${e.message}")
            }
        }
        
        lowInput.contains("add button") -> {
            if (activeFilePath != null) {
                val file = File(activeFilePath)
                val currentText = file.readText()
                val newCode = "\n@Composable\nfun GeneratedButton() {\n    Button(onClick = { }) {\n        Text(\"AI Button\")\n    }\n}\n"
                file.writeText(currentText + newCode)
                onResponse("Added a standard Compose Button to ${file.name}.")
            } else {
                onResponse("Please open a file first so I know where to add the code.")
            }
        }

        lowInput.contains("build app") || lowInput.contains("create app") -> {
            val appName = if (lowInput.contains("app ")) {
                input.substringAfter("app ").replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            } else "AI_Generated_App"
            val packageName = "com.ai.generated.${appName.lowercase()}"
            
            try {
                com.example.asmobile.project.ProjectManager.createNewProject(
                    baseDir = rootDir,
                    projectName = appName,
                    packageName = packageName,
                    template = com.example.asmobile.project.ProjectTemplate.EmptyCompose
                )
                onResponse("I have successfully built the entire app '$appName' for you! You can find it in the Project Explorer.")
            } catch (e: Exception) {
                onResponse("Failed to build app: ${e.message}")
            }
        }

        else -> onResponse(getAiResponse(input))
    }
}

data class ChatMessage(val content: String, val isUser: Boolean)

@Composable
private fun ChatBubble(message: ChatMessage) {
    val alignment = if (message.isUser) Alignment.End else Alignment.Start
    val color = if (message.isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (message.isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = alignment) {
        Surface(
            color = color,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (message.isUser) 16.dp else 0.dp,
                bottomEnd = if (message.isUser) 0.dp else 16.dp
            )
        ) {
            Text(
                text = message.content,
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = textColor
            )
        }
    }
}

private fun getAiResponse(input: String): String {
    val lowInput = input.lowercase()
    return when {
        lowInput.contains("hello") || lowInput.contains("hi") -> 
            "Hello! I'm Gemini, your ASMobile AI Assistant. I have full access to your project files and can write code, create files, or build entire apps."
        
        lowInput.contains("explain") -> "This project is a modern Android IDE built with Jetpack Compose. It uses a custom file system bridge to allow real-time mobile development."

        lowInput.contains("help") -> 
            "You can ask me to:\n" +
            "• 'Create file Utils.kt'\n" +
            "• 'Add a button to this file'\n" +
            "• 'Build a weather app'\n" +
            "• 'Explain the project structure'"

        else -> "I'm analyzing your request. I can modify your project directly—just let me know what code or files you need."
    }
}
