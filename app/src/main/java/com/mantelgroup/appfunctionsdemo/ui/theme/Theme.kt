package com.mantelgroup.appfunctionsdemo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Red,
    onPrimary = Background,
    primaryContainer = RedContainer,
    onPrimaryContainer = OnRedContainer,
    secondary = WarmGrey,
    onSecondary = Background,
    secondaryContainer = WarmGreyContainer,
    onSecondaryContainer = OnWarmGreyContainer,
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceVariant,
    outline = Outline,
    outlineVariant = OutlineVariant,
    surfaceTint = Red,
)

private val DarkColorScheme = darkColorScheme(
    primary = RedDark,
    onPrimary = RedContainerDark,
    primaryContainer = RedContainerDark,
    onPrimaryContainer = OnRedContainerDark,
    secondary = WarmGreyDark,
    onSecondary = WarmGreyContainerDark,
    secondaryContainer = WarmGreyContainerDark,
    onSecondaryContainer = OnWarmGreyContainerDark,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = OutlineDark,
    outlineVariant = OutlineVariantDark,
    surfaceTint = RedDark,
)

@Composable
fun AppFunctionsDemoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content,
    )
}
