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
    primary = ObsidianAccent,
    secondary = ObsidianAccentVariant,
    tertiary = ObsidianBorder,
    background = ObsidianDeep,
    surface = ObsidianBase,
    surfaceVariant = ObsidianSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = ObsidianTextPrimary,
    onBackground = ObsidianTextPrimary,
    onSurface = ObsidianTextPrimary,
    onSurfaceVariant = ObsidianTextSecondary,
    outline = ObsidianBorder,
    outlineVariant = ObsidianBorder.copy(alpha = 0.5f)
)

private val LightColorScheme = lightColorScheme(
    primary = ObsidianAccent,
    secondary = ObsidianAccentVariant,
    tertiary = ObsidianBorder,
    background = Color(0xFFFFFFFF),
    surface = Color(0xFFF9F9F9),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.Black,
    onBackground = Color(0xFF1A1A1A),
    onSurface = Color(0xFF1A1A1A)
)

@Composable
fun ASMobileTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}