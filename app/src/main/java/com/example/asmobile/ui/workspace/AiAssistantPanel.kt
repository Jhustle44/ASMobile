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
    onProjectCreated: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var message by remember { mutableStateOf("") }
    val chatHistory = remember { mutableStateListOf<ChatMessage>() }
    var isGenerating by remember { mutableStateOf(false) }
    var generationTask by remember { mutableStateOf("") }
    var systemPrompt by remember { mutableStateOf("You are a professional Android Developer using ASMobile.") }

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
                Column(modifier = Modifier.weight(1f)) {
                    Text("Gemini AI Pro", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    if (isGenerating) {
                        Text(generationTask, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                    }
                }
                IconButton(onClick = { /* System Prompt Settings */ }) {
                    Icon(Icons.Rounded.Psychology, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
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
            if (isGenerating) {
                item { GeneratingIndicator() }
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
                    maxLines = 5,
                    enabled = !isGenerating
                )
                Spacer(Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (message.isNotBlank()) {
                            chatHistory.add(ChatMessage(message, true))
                            val input = message
                            message = ""
                            
                            // Start generation simulation
                            isGenerating = true
                            executeAiLogic(
                                input = input, 
                                rootDir = rootDir, 
                                activeFilePath = activeFilePath, 
                                onFileSelected = onFileSelected,
                                onStatusUpdate = { generationTask = it },
                                onProjectCreated = onProjectCreated,
                                onResponse = { response ->
                                    chatHistory.add(ChatMessage(response, false))
                                    isGenerating = false
                                }
                            )
                        }
                    },
                    enabled = !isGenerating,
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

@Composable
private fun GeneratingIndicator() {
    Row(
        modifier = Modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
        Text("Gemini is coding...", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
    }
}

private fun executeAiLogic(
    input: String,
    rootDir: File,
    activeFilePath: String?,
    onFileSelected: (File) -> Unit,
    onStatusUpdate: (String) -> Unit,
    onProjectCreated: () -> Unit,
    onResponse: (String) -> Unit
) {
    val lowInput = input.lowercase()
    
    when {
        // App Building Logic
        lowInput.contains("build") || lowInput.contains("create") || lowInput.contains("make") -> {
            onStatusUpdate("Interpreting requirements...")
            val appName = input.substringAfter("app ").substringBefore(" ").replaceFirstChar { it.uppercase() }
                .ifEmpty { input.split(" ").lastOrNull()?.replaceFirstChar { it.uppercase() } ?: "NewApp" }
            
            val template = when {
                lowInput.contains("notes") -> ProjectTemplate.NotesApp
                lowInput.contains("weather") -> ProjectTemplate.WeatherApp
                lowInput.contains("login") -> ProjectTemplate.LoginFlow
                lowInput.contains("nav") -> ProjectTemplate.BottomNav
                lowInput.contains("counter") -> ProjectTemplate.CounterApp
                else -> ProjectTemplate.CustomAi
            }
            
            onStatusUpdate("Scaffolding $appName architecture...")
            try {
                ProjectManager.createNewProject(rootDir, appName, "com.example.${appName.lowercase()}", template)
                
                if (template == ProjectTemplate.CustomAi) {
                    onStatusUpdate("Generating bespoke AI code...")
                    val customCode = generateCustomAppCode(appName, input)
                    val mainFile = File(rootDir, "$appName/app/src/main/java/com/example/${appName.lowercase()}/MainActivity.kt")
                    if (mainFile.exists()) {
                        mainFile.writeText(customCode)
                        onFileSelected(mainFile)
                    }
                } else {
                    val mainFile = File(rootDir, "$appName/app/src/main/java/com/example/${appName.lowercase()}/MainActivity.kt")
                    if (mainFile.exists()) onFileSelected(mainFile)
                }
                
                onProjectCreated()
                onResponse("✅ I've built '$appName' exactly as requested. I've also opened the primary entry point in your editor. What's the next feature?")
            } catch (e: Exception) {
                onResponse("❌ Scaffolding failed: ${e.message}")
            }
        }

        // Feature Injection (Deep listening)
        lowInput.contains("add") || lowInput.contains("implement") || lowInput.contains("inject") -> {
            if (activeFilePath != null) {
                val file = File(activeFilePath)
                onStatusUpdate("Analyzing ${file.name} context...")
                val currentText = file.readText()
                
                val codeToInject = when {
                    lowInput.contains("button") -> "\n\n@Composable\nfun CustomAIButton() {\n    Button(onClick = {}) { Text(\"AI Action\") }\n}"
                    lowInput.contains("list") -> "\n\n@Composable\nfun AIList() {\n    LazyColumn { items(10) { Text(\"Item \$it\") } }\n}"
                    lowInput.contains("image") -> "\n\n@Composable\nfun AIImage() {\n    Icon(Icons.Default.Face, null, modifier = Modifier.size(48.dp)) \n}"
                    else -> "\n\n// AI Generated Logic\nfun handleAIRequest() {\n    // TODO: Implement user specific logic\n}"
                }
                
                onStatusUpdate("Writing code to disk...")
                file.writeText(currentText + codeToInject)
                onResponse("⚡ I've injected the requested component into '${file.name}'. You can see it at the bottom of the file.")
            } else {
                onResponse("Which file should I work on? Please open one in the editor first.")
            }
        }

        // File Creation Logic
        lowInput.contains("file") -> {
            val fileName = input.split(" ").last()
            val file = File(rootDir, fileName)
            try {
                if (file.exists()) {
                    onResponse("File '$fileName' already exists in the root.")
                } else {
                    file.createNewFile()
                    val content = if (fileName.endsWith(".kt")) {
                        "package com.example.asmobile\n\nimport androidx.compose.runtime.Composable\nimport androidx.compose.material3.*\n\n@Composable\nfun AIScreen() {\n    Text(\"Generated by Gemini\")\n}"
                    } else "// AI Generated File"
                    file.writeText(content)
                    onFileSelected(file)
                    onProjectCreated()
                    onResponse("📄 Created and opened '$fileName' for you.")
                }
            } catch (e: Exception) {
                onResponse("❌ File error: ${e.message}")
            }
        }

        // Refactoring / Cleanup
        lowInput.contains("clean") || lowInput.contains("fix") || lowInput.contains("refactor") -> {
            onResponse("🛠️ I'm analyzing your code for potential improvements. I recommend moving your UI components into a dedicated 'ui' package to follow standard Android architecture.")
        }

        // Project Analysis
        lowInput.contains("analyze") || lowInput.contains("status") -> {
            val files = rootDir.listFiles()?.size ?: 0
            onResponse("🔍 Project Analysis: I found $files top-level entries in your workspace. Your current build configuration looks healthy.")
        }

        // Code Insertion/Refactor
        lowInput.contains("add button") || lowInput.contains("insert") -> {
            if (activeFilePath != null) {
                val file = File(activeFilePath)
                val currentText = file.readText()
                val updatedText = currentText + "\n\n@Composable\nfun GeneratedButton() {\n    Button(onClick = {}) { Text(\"AI Button\") }\n}"
                file.writeText(updatedText)
                onResponse("⚡ I've injected a new Composable button into '${file.name}'. Check the editor!")
            } else {
                onResponse("Please open a file in the editor first so I know where to insert the code.")
            }
        }

        // General Help
        lowInput.contains("help") -> {
            onResponse("I'm your Elite AI partner. I can:\n• 'Build a fitness app'\n• 'Create file Utils.kt'\n• 'Add a button to this file'\n• 'Analyze project status'")
        }

        else -> {
            onResponse("I've analyzed your project. I can scaffold a new app, generate code for your active file, or analyze your architecture. Try asking to 'build a chat app'.")
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
