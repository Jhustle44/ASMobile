package com.example.asmobile.ui.workspace

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
fun PluginMarketplace(modifier: Modifier = Modifier) {
    val plugins = listOf(
        PluginItem("Rainbow Syntax", "High-contrast syntax highlighting for Elite Pro.", "1.2MB", 4.8f),
        PluginItem("Gemini Visualizer", "Advanced AI architecture graphing and flowcharts.", "5.6MB", 4.9f),
        PluginItem("Gradle Optimizer", "Reduces build times by up to 40% on mobile.", "840KB", 4.5f),
        PluginItem("Material 4 Preview", "Early access to the next generation design system.", "2.1MB", 4.2f),
        PluginItem("ADB Wireless Pro", "Wireless debugging without any terminal commands.", "1.1MB", 4.7f)
    )

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Plugin Marketplace", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
                Text("Extend your mobile IDE capabilities", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = { }) { Icon(Icons.Rounded.Search, null) }
        }

        Spacer(Modifier.height(24.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(plugins) { plugin ->
                PluginCard(plugin)
            }
        }
    }
}

data class PluginItem(val name: String, val desc: String, val size: String, val rating: Float)

@Composable
private fun PluginCard(plugin: PluginItem) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.Extension, null, tint = MaterialTheme.colorScheme.primary)
            }
            
            Spacer(Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(plugin.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Text(plugin.desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                
                Row(modifier = Modifier.padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Star, null, modifier = Modifier.size(12.dp), tint = Color(0xFFFBBF24))
                    Text(plugin.rating.toString(), style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(start = 4.dp))
                    Spacer(Modifier.width(12.dp))
                    Text(plugin.size, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            
            Button(
                onClick = { },
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                Text("Install", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}
