package com.example.asmobile.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = GlowPurple,
    secondary = GlowBlue,
    tertiary = GlowEmerald,
    background = ObsidianBlack,
    surface = ObsidianDeep,
    surfaceVariant = ObsidianSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = TextWhite,
    onSurface = TextWhite,
    onSurfaceVariant = TextGray,
    outline = ObsidianBorder,
    outlineVariant = ObsidianBorder.copy(alpha = 0.5f),
    error = ErrorRed
)

private val LightColorScheme = lightColorScheme(
    primary = GlowPurple,
    secondary = GlowBlue,
    tertiary = GlowEmerald,
    background = Color(0xFFF6F8FA),
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1F2328),
    onSurface = Color(0xFF1F2328)
)

@Composable
fun ASMobileTheme(
    themeMode: com.example.asmobile.ui.workspace.ThemeMode = com.example.asmobile.ui.workspace.ThemeMode.Obsidian,
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeMode) {
        com.example.asmobile.ui.workspace.ThemeMode.Obsidian -> DarkColorScheme
        com.example.asmobile.ui.workspace.ThemeMode.Arctic -> LightColorScheme
        com.example.asmobile.ui.workspace.ThemeMode.Solar -> darkColorScheme(
            primary = GlowGold,
            secondary = GlowBlue,
            tertiary = GlowEmerald,
            background = Color(0xFF002B36),
            surface = Color(0xFF073642),
            onPrimary = Color.White,
            onBackground = Color(0xFF93A1A1),
            onSurface = Color(0xFF93A1A1)
        )
        com.example.asmobile.ui.workspace.ThemeMode.Midnight -> darkColorScheme(
            primary = Color(0xFF38B6FF),
            background = Color(0xFF000814),
            surface = Color(0xFF001D3D),
            onPrimary = Color.White
        )
        com.example.asmobile.ui.workspace.ThemeMode.Forest -> darkColorScheme(
            primary = Color(0xFF2ECC71),
            background = Color(0xFF0B1A0E),
            surface = Color(0xFF1B2E1D),
            onPrimary = Color.White
        )
        com.example.asmobile.ui.workspace.ThemeMode.Rose -> darkColorScheme(
            primary = Color(0xFFFF4D6D),
            background = Color(0xFF1A0A0E),
            surface = Color(0xFF2D1419),
            onPrimary = Color.White
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
