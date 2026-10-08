package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val GuestFlowColors = lightColorScheme(
    primary = GuestFlowOrange,
    onPrimary = GuestFlowWhite,
    primaryContainer = Color(0xFFFFE0CF),
    onPrimaryContainer = GuestFlowInk,
    secondary = GuestFlowBrown,
    onSecondary = GuestFlowWhite,
    secondaryContainer = Color(0xFFF1E6DC),
    onSecondaryContainer = GuestFlowInk,
    tertiary = GuestFlowGold,
    onTertiary = GuestFlowInk,
    background = GuestFlowCream,
    onBackground = GuestFlowInk,
    surface = GuestFlowWhite,
    onSurface = GuestFlowInk,
    surfaceVariant = GuestFlowSoftSurface,
    onSurfaceVariant = GuestFlowMuted,
    outline = GuestFlowLine,
    outlineVariant = Color(0xFFF7EDE5),
    error = GuestFlowError,
    onError = GuestFlowWhite,
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = GuestFlowColors,
        typography = Typography,
        content = content
    )
}

@Composable
fun GuestFlowTheme(content: @Composable () -> Unit) = MyApplicationTheme(content = content)
