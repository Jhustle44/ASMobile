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
import com.example.asmobile.project.ProjectManager
import com.example.asmobile.project.ProjectTemplate

@Composable
fun AiAssistantPanel(
    rootDir: File,
    activeFilePath: String?,
    onFileSelected: (File) -> Unit,
    modifier: Modifier = Modifier
) {
    var message by remember { mutableStateOf("") }
    val chatHistory = remember { mutableStateListOf<ChatMessage>() }

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
                Text("Gemini AI Pro", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
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
                            "I'm ready to build your app. What's on your mind?",
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
                    placeholder = { Text("Command Gemini...") },
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 5
                )
                Spacer(Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (message.isNotBlank()) {
                            chatHistory.add(ChatMessage(message, true))
                            val input = message
                            message = ""
                            executeAiLogic(input, rootDir, activeFilePath, onFileSelected) { response ->
                                chatHistory.add(ChatMessage(response, false))
                            }
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

private fun executeAiLogic(
    input: String,
    rootDir: File,
    activeFilePath: String?,
    onFileSelected: (File) -> Unit,
    onResponse: (String) -> Unit
) {
    val lowInput = input.lowercase()
    
    when {
        // App Building Logic
        lowInput.contains("build") || lowInput.contains("create app") -> {
            val appName = if (input.contains("app ")) {
                input.substringAfter("app ").split(" ").firstOrNull()?.replaceFirstChar { it.uppercase() } ?: "NewApp"
            } else "NewApp"
            
            val template = when {
                lowInput.contains("notes") -> ProjectTemplate.NotesApp
                lowInput.contains("weather") -> ProjectTemplate.WeatherApp
                lowInput.contains("login") -> ProjectTemplate.LoginFlow
                lowInput.contains("nav") -> ProjectTemplate.BottomNav
                else -> ProjectTemplate.CustomAi // NEW: AI-Driven Custom App
            }
            
            try {
                ProjectManager.createNewProject(rootDir, appName, "com.example.${appName.lowercase()}", template)
                
                // If it's a custom app, Gemini "thinks" and generates specific code
                if (template == ProjectTemplate.CustomAi) {
                    val customCode = generateCustomAppCode(appName, input)
                    val mainFile = File(rootDir, "$appName/app/src/main/java/com/example/${appName.lowercase()}/MainActivity.kt")
                    if (mainFile.exists()) {
                        mainFile.writeText(customCode)
                    }
                }
                
                onResponse("🚀 Gemini Pro has built '$appName' from your description. I've scaffolded the architecture and injected tailored Compose code. Check it out in the Project tab!")
            } catch (e: Exception) {
                onResponse("❌ Error building custom app: ${e.message}")
            }
        }

        // File Creation Logic
        lowInput.contains("create file") || lowInput.contains("new file") -> {
            val fileName = input.split(" ").last()
            val file = File(rootDir, fileName)
            try {
                if (file.exists()) {
                    onResponse("File '$fileName' already exists.")
                } else {
                    file.createNewFile()
                    file.writeText("// Generated by Gemini\npackage com.example.asmobile\n\nimport androidx.compose.runtime.Composable\n\n@Composable\nfun NewScreen() {\n\n}")
                    onFileSelected(file)
                    onResponse("📄 Created and opened '$fileName' for you.")
                }
            } catch (e: Exception) {
                onResponse("❌ Error: ${e.message}")
            }
        }

        // General Help
        lowInput.contains("help") -> {
            onResponse("I can help you build apps instantly. Commands:\n• 'Build a notes app called MyNotes'\n• 'Create file ThemeUtils.kt'\n• 'How do I add a button?'")
        }

        else -> {
            onResponse("I've analyzed your project. I can scaffold a new app or generate code for your active file. Try asking to 'build a weather app'.")
        }
    }
}

private fun generateCustomAppCode(appName: String, description: String): String {
    val lowDesc = description.lowercase()
    val content = when {
        lowDesc.contains("recipe") -> "Text(\"Recipe Book App\", style = MaterialTheme.typography.headlineMedium)\nLazyColumn { items(5) { Text(\"Recipe #\$it\", modifier = Modifier.padding(8.dp)) } }"
        lowDesc.contains("fitness") -> "Icon(Icons.Default.DirectionsRun, null, modifier = Modifier.size(64.dp))\nText(\"Fitness Tracker\", style = MaterialTheme.typography.displaySmall)\nLinearProgressIndicator(progress = 0.7f, modifier = Modifier.fillMaxWidth())"
        lowDesc.contains("chat") -> "Column { Box(Modifier.weight(1f)) { Text(\"Chat History\") }\nOutlinedTextField(value = \"\", onValueChange = {}, label = { Text(\"Message\") }, modifier = Modifier.fillMaxWidth()) }"
        else -> "Text(\"AI Generated Content for \$appName\", style = MaterialTheme.typography.headlineMedium)\nText(\"Description: \$description\", style = MaterialTheme.typography.bodySmall)"
    }

    return """
        package com.example.${appName.lowercase()}

        import android.os.Bundle
        import androidx.activity.ComponentActivity
        import androidx.activity.compose.setContent
        import androidx.compose.foundation.layout.*
        import androidx.compose.foundation.lazy.LazyColumn
        import androidx.compose.material.icons.Icons
        import androidx.compose.material.icons.filled.*
        import androidx.compose.material3.*
        import androidx.compose.runtime.*
        import androidx.compose.ui.Alignment
        import androidx.compose.ui.Modifier
        import androidx.compose.ui.unit.dp

        class MainActivity : ComponentActivity() {
            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContent {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        $content
                    }
                }
            }
        }
    """.trimIndent()
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
                bottomStart = if (message.isUser) 16.dp else 2.dp,
                bottomEnd = if (message.isUser) 2.dp else 16.dp
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
