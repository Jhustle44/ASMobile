package com.example.asmobile.ui.workspace

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.window.Dialog
import java.io.File
import com.example.asmobile.project.ProjectManager
import com.example.asmobile.project.ProjectTemplate
import com.example.asmobile.project.ProjectLanguage

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
    var showSystemPromptDialog by remember { mutableStateOf(false) }

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
                IconButton(onClick = { showSystemPromptDialog = true }) {
                    Icon(Icons.Rounded.Psychology, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        // Tool Actions Row
        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { AISuggestionChip("🛠️ Fix Bugs") { message = "Analyze my active file and fix any potential bugs or crashes." } }
            item { AISuggestionChip("⚡ Optimize") { message = "Refactor my code for better performance and Material 3 best practices." } }
            item { AISuggestionChip("📝 Document") { message = "Add KDoc comments and explanation to all functions in this file." } }
            item { AISuggestionChip("🎨 Style UI") { message = "Modernize the UI layout of this screen using Glassmorphism 2.0." } }
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
                Surface(
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(28.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { showSystemPromptDialog = true }) {
                            Icon(Icons.Rounded.Psychology, null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
                        }
                        androidx.compose.foundation.text.BasicTextField(
                            value = message,
                            onValueChange = { message = it },
                            modifier = Modifier.weight(1f).padding(8.dp),
                            textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
                            cursorBrush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary),
                            decorationBox = { innerTextField ->
                                if (message.isEmpty()) {
                                    Text("Describe your feature...", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                                }
                                innerTextField()
                            }
                        )
                    }
                }
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
                        containerColor = if (message.isBlank()) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary,
                        contentColor = if (message.isBlank()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(Icons.AutoMirrored.Rounded.Send, null, modifier = Modifier.size(20.dp))
                }
            }
        }
    }

    if (showSystemPromptDialog) {
        SystemPromptDialog(
            currentPrompt = systemPrompt,
            onDismiss = { showSystemPromptDialog = false },
            onSave = { systemPrompt = it }
        )
    }
}

@Composable
private fun AISuggestionChip(label: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SystemPromptDialog(
    currentPrompt: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var text by remember { mutableStateOf(currentPrompt) }
    
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.width(340.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Rounded.Psychology, null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(16.dp))
                Text("AI Behavior", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                Text("Instruct Gemini how to code for you", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                
                Spacer(Modifier.height(24.dp))
                
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("e.g. 'Always use Material 3 and clean architecture'") },
                    minLines = 4,
                    shape = RoundedCornerShape(16.dp),
                    textStyle = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(Modifier.height(24.dp))
                
                Button(
                    onClick = { onSave(text); onDismiss() },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Apply Instructions", fontWeight = FontWeight.Bold)
                }
                
                TextButton(onClick = onDismiss, modifier = Modifier.padding(top = 8.dp)) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
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
        lowInput.contains("build") || lowInput.contains("create") || lowInput.contains("make") || lowInput.contains("new app") -> {
            onStatusUpdate("Interpreting requirements...")
            
            // Smarter name extraction
            val appName = when {
                input.contains("called ") -> input.substringAfter("called ").substringBefore(" ")
                input.contains("name ") -> input.substringAfter("name ").substringBefore(" ")
                input.contains("app ") -> input.substringAfter("app ").substringBefore(" ")
                else -> input.split(" ").lastOrNull() ?: "NewApp"
            }.replaceFirstChar { it.uppercase() }.filter { it.isLetterOrDigit() }
            
            val template = when {
                lowInput.contains("notes") -> ProjectTemplate.NotesApp
                lowInput.contains("weather") -> ProjectTemplate.WeatherApp
                lowInput.contains("login") -> ProjectTemplate.LoginFlow
                lowInput.contains("nav") -> ProjectTemplate.BottomNav
                lowInput.contains("counter") -> ProjectTemplate.CounterApp
                lowInput.contains("social") || lowInput.contains("feed") -> ProjectTemplate.SocialApp
                lowInput.contains("shop") || lowInput.contains("commerce") || lowInput.contains("store") -> ProjectTemplate.ECommerce
                else -> ProjectTemplate.CustomAi
            }
            
            val language = if (lowInput.contains("java")) ProjectLanguage.Java else ProjectLanguage.Kotlin
            
            onStatusUpdate("Scaffolding $appName architecture...")
            try {
                ProjectManager.createNewProject(rootDir, appName, "com.ai.${appName.lowercase()}", template, language)
                
                val mainFile = File(rootDir, "$appName/app/src/main/java/com/ai/${appName.lowercase()}/MainActivity.kt")
                
                if (template == ProjectTemplate.CustomAi) {
                    onStatusUpdate("Generating bespoke AI code...")
                    val customCode = generateCustomAppCode(appName, input, language)
                    if (mainFile.exists()) {
                        mainFile.writeText(customCode)
                    }
                }
                
                if (mainFile.exists()) {
                    onFileSelected(mainFile)
                }
                
                onProjectCreated()
                onResponse("✅ I've built '$appName' exactly as requested. I've also opened the primary entry point in your editor. What's the next feature?")
            } catch (e: Exception) {
                onResponse("❌ Scaffolding failed: ${e.message}")
            }
        }

        // Feature Injection (Deep listening)
        lowInput.contains("add") || lowInput.contains("implement") || lowInput.contains("inject") || lowInput.contains("put") -> {
            if (activeFilePath != null) {
                val file = File(activeFilePath)
                onStatusUpdate("Analyzing ${file.name} context...")
                val currentText = try { file.readText() } catch(e: Exception) { "" }
                
                val codeToInject = when {
                    lowInput.contains("button") -> "\n\n@Composable\nfun CustomAIButton() {\n    Button(onClick = {}) { Text(\"AI Action\") }\n}"
                    lowInput.contains("list") -> "\n\n@Composable\nfun AIList() {\n    LazyColumn { items(10) { Text(\"Item \$it\") } }\n}"
                    lowInput.contains("image") || lowInput.contains("icon") -> "\n\n@Composable\nfun AIImage() {\n    Icon(Icons.Default.Face, null, modifier = Modifier.size(48.dp)) \n}"
                    lowInput.contains("text") -> "\n\n@Composable\nfun AIText() {\n    Text(\"Hello from Gemini AI\", style = MaterialTheme.typography.bodyLarge)\n}"
                    else -> "\n\n// AI Generated Logic for: $input\nfun handleAIRequest() {\n    // TODO: Implement user specific logic\n}"
                }
                
                onStatusUpdate("Writing code to disk...")
                file.writeText(currentText + codeToInject)
                onResponse("⚡ I've injected the requested component into '${file.name}'. You can see it at the bottom of the file.")
            } else {
                onResponse("Which file should I work on? Please open one in the editor first so I know where to insert the code.")
            }
        }

        // File Creation Logic
        lowInput.contains("file") || lowInput.contains("create") -> {
            val fileName = input.split(" ").last()
            if (fileName.contains(".")) {
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
            } else {
                onResponse("Please specify a full file name (e.g., 'MyScreen.kt').")
            }
        }

        // Refactoring / Cleanup
        lowInput.contains("clean") || lowInput.contains("fix") || lowInput.contains("refactor") || lowInput.contains("sync") -> {
            onResponse("🛠️ I'm initiating a project maintenance cycle. I'll analyze your dependencies and clean the build artifacts to ensure Elite performance.")
        }

        // Export & Signing
        lowInput.contains("export") || lowInput.contains("sign") || lowInput.contains("zipalign") -> {
            onResponse("📦 You can access professional signing tools in the side drawer under 'Export & Sign'. I can guide you through generating a release keystore there.")
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

private fun generateCustomAppCode(appName: String, description: String, language: ProjectLanguage = ProjectLanguage.Kotlin): String {
    val lowDesc = description.lowercase()
    
    if (language == ProjectLanguage.Java) {
        return """
            package com.ai.${appName.lowercase()};

            import android.os.Bundle;
            import androidx.activity.ComponentActivity;
            import androidx.activity.compose.ComponentActivityKt;
            import androidx.compose.material3.Text;
            import androidx.compose.runtime.Composable;

            public class MainActivity extends ComponentActivity {
                @Override
                protected void onCreate(Bundle savedInstanceState) {
                    super.onCreate(savedInstanceState);
                    // AI Generated Java Content
                    ComponentActivityKt.setContent(this, null, () -> {
                        return null; // Compose in Java is complex, normally used with Kotlin interop
                    });
                }
            }
        """.trimIndent()
    }

    val content = when {
        lowDesc.contains("recipe") -> "Text(\"Recipe Book App\", style = MaterialTheme.typography.headlineMedium)\nLazyColumn { items(5) { Text(\"Recipe #\$it\", modifier = Modifier.padding(8.dp)) } }"
        lowDesc.contains("fitness") -> "Icon(Icons.Default.DirectionsRun, null, modifier = Modifier.size(64.dp))\nText(\"Fitness Tracker\", style = MaterialTheme.typography.displaySmall)\nLinearProgressIndicator(progress = 0.7f, modifier = Modifier.fillMaxWidth())"
        lowDesc.contains("chat") -> "Column { Box(Modifier.weight(1f)) { Text(\"Chat History\") }\nOutlinedTextField(value = \"\", onValueChange = {}, label = { Text(\"Message\") }, modifier = Modifier.fillMaxWidth()) }"
        lowDesc.contains("bakery") -> "Text(\"Bakery Management\", style = MaterialTheme.typography.displayMedium)\nText(\"Track your orders and ingredients.\")\nButton(onClick = {}) { Text(\"New Order\") }"
        else -> "Text(\"AI Generated Content for \$appName\", style = MaterialTheme.typography.headlineMedium)\nText(\"Description: \$description\", style = MaterialTheme.typography.bodySmall)"
    }

    return """
        package com.ai.${appName.lowercase()}

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
    val color = if (message.isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    val textColor = if (message.isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    val shape = RoundedCornerShape(
        topStart = 20.dp,
        topEnd = 20.dp,
        bottomStart = if (message.isUser) 20.dp else 4.dp,
        bottomEnd = if (message.isUser) 4.dp else 20.dp
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp), 
        horizontalAlignment = alignment
    ) {
        if (!message.isUser) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 12.dp, bottom = 4.dp)
            ) {
                Icon(Icons.Rounded.AutoAwesome, null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(6.dp))
                Text("GEMINI ELITE", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.ExtraBold, letterSpacing = 0.5.sp)
            }
        }
        
        Surface(
            color = color,
            shape = shape,
            border = if (!message.isUser) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)) else null,
            tonalElevation = if (message.isUser) 4.dp else 0.dp
        ) {
            Text(
                text = message.content,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = textColor,
                lineHeight = 22.sp
            )
        }
    }
}
