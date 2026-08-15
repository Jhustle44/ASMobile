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

@Composable
fun AiAssistantPanel(modifier: Modifier = Modifier) {
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
                Text("AI Assistant", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
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
                            "How can I help you build your app today?",
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
                    placeholder = { Text("Ask anything...") },
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 3
                )
                Spacer(Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (message.isNotBlank()) {
                            chatHistory.add(ChatMessage(message, true))
                            val response = getAiResponse(message)
                            chatHistory.add(ChatMessage(response, false))
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
            "Hello! I'm your ASMobile AI Assistant. I can help you build entire apps! Try asking me to 'build a notes app' or 'create a weather app'."
        
        lowInput.contains("build") || lowInput.contains("create") -> {
            when {
                lowInput.contains("notes") -> "I can build a Notes App for you! Go to Home -> New Project and select the 'Notes App' template. I'll scaffold the list and FAB for you."
                lowInput.contains("counter") -> "I'll help you build a Counter App. Use the 'Counter App' template in the New Project wizard to see how state management works."
                lowInput.contains("weather") -> "Weather App? Great choice. Select the 'Weather App' template when creating a new project for a beautiful UI layout."
                else -> "I can scaffold several types of apps! Try asking for a 'notes app', 'counter app', or 'login flow'. You can find these in the New Project wizard."
            }
        }
            
        lowInput.contains("compose") || lowInput.contains("ui") -> 
            "Jetpack Compose is Android's modern toolkit for building native UI. I can help you write entire screens if you tell me what you need!"

        lowInput.contains("button") -> 
            "Here is a modern Compose Button snippet:\n\n" +
            "Button(\n" +
            "    onClick = { /* Handle click */ },\n" +
            "    shape = RoundedCornerShape(12.dp)\n" +
            ") {\n" +
            "    Text(\"Click Me\")\n" +
            "}"
            
        lowInput.contains("help") -> 
            "I'm here to assist! You can ask me to:\n" +
            "• 'Build a notes app'\n" +
            "• 'How do I create a login screen?'\n" +
            "• 'Show me a Compose List example'"

        else -> "I'm ready to help you build! You can ask me to create specific types of apps, and I'll guide you through the templates or provide the code."
    }
}
