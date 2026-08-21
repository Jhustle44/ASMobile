package com.example.asmobile.ui.workspace

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.asmobile.ui.theme.*

@Composable
fun ThemeEngineScreen(
    viewModel: ThemeViewModel,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text("Elite Theme Engine", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
        Text("Customize your 3D glossy environment", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        
        Spacer(Modifier.height(24.dp))
        
        LazyColumn(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            item {
                SectionHeader("Core Aesthetics", Icons.Rounded.AutoAwesome)
            }
            
            item {
                ThemePresetGrid(viewModel)
            }
            
            item {
                SectionHeader("Visual Tuning", Icons.Rounded.Tune)
            }
            
            item {
                TuningCard {
                    Column(modifier = Modifier.padding(16.dp)) {
                        TuningSlider("Glass Opacity", 0.8f)
                        Spacer(Modifier.height(16.dp))
                        TuningSlider("Glow Intensity", 0.6f)
                        Spacer(Modifier.height(16.dp))
                        TuningSlider("3D Depth (Shadows)", 0.5f)
                        Spacer(Modifier.height(16.dp))
                        TuningToggle("High-Gloss Reflections", true)
                        Spacer(Modifier.height(16.dp))
                        TuningToggle("Dynamic 3D Parallax", false)
                        Spacer(Modifier.height(16.dp))
                        TuningToggle("Vibrant Accents", true)
                    }
                }
            }
            
            item {
                SectionHeader("Glass Refraction (3D)", Icons.Rounded.Layers)
            }
            
            item {
                TuningCard {
                    Column(modifier = Modifier.padding(16.dp)) {
                        TuningSlider("Refraction Blur", 0.4f)
                        Spacer(Modifier.height(16.dp))
                        TuningSlider("Edge Sharpness", 0.7f)
                        Spacer(Modifier.height(16.dp))
                        TuningSlider("Light Reflection", 0.2f)
                    }
                }
            }
            
            item {
                SectionHeader("Elite Color Palette", Icons.Rounded.Palette)
            }
            
            item {
                ColorPaletteRow()
            }
            
            item { Spacer(Modifier.height(100.dp)) }
        }
    }
}

@Composable
private fun SectionHeader(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 12.dp)) {
        Icon(icon, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(8.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ThemePresetGrid(viewModel: ThemeViewModel) {
    val modes = ThemeMode.entries
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        modes.chunked(2).forEach { rowModes ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                rowModes.forEach { mode ->
                    ThemePresetCard(
                        mode = mode,
                        isSelected = viewModel.currentTheme == mode,
                        onClick = { viewModel.setTheme(mode) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowModes.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun ThemePresetCard(
    mode: ThemeMode,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = if (isSelected) 0.4f else 0.15f),
        border = androidx.compose.foundation.BorderStroke(
            if (isSelected) 2.dp else 1.dp,
            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (isSelected) {
                Box(modifier = Modifier.matchParentSize().background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)))
            }
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    when(mode) {
                        ThemeMode.Obsidian -> Icons.Rounded.DarkMode
                        ThemeMode.Arctic -> Icons.Rounded.LightMode
                        ThemeMode.Solar -> Icons.Rounded.WbSunny
                        ThemeMode.Midnight -> Icons.Rounded.Bedtime
                        ThemeMode.Forest -> Icons.Rounded.Park
                        ThemeMode.Rose -> Icons.Rounded.AutoFixHigh
                        ThemeMode.Neon -> Icons.Rounded.ElectricBolt
                        ThemeMode.Vaporwave -> Icons.Rounded.MusicNote
                        ThemeMode.Cyberpunk -> Icons.Rounded.Token
                    },
                    null,
                    tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    mode.label.split(" (")[0], 
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun TuningCard(content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
    ) {
        content()
    }
}

@Composable
private fun TuningSlider(label: String, value: Float) {
    var sliderValue by remember { mutableStateOf(value) }
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.labelMedium)
            Text("${(sliderValue * 100).toInt()}%", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
        }
        Slider(
            value = sliderValue,
            onValueChange = { sliderValue = it },
            colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary, activeTrackColor = MaterialTheme.colorScheme.primary)
        )
    }
}

@Composable
private fun TuningToggle(label: String, initial: Boolean) {
    var checked by remember { mutableStateOf(initial) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Switch(checked = checked, onCheckedChange = { checked = it })
    }
}

@Composable
private fun ColorPaletteRow() {
    val colors = listOf(GlowPurple, GlowBlue, GlowEmerald, GlowGold, GlowRose, GlowSky)
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        colors.forEach { color ->
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .shadow(4.dp, CircleShape)
                    .background(color, CircleShape)
                    .border(2.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                    .clickable { }
            )
        }
    }
}
