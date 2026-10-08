package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

// Google Font: Playfair (Standard width, elegant serif), including Regular, true Bold (700), Italic, and Bold Italic (700 Italic)
val PlayfairFont = FontFamily(
    Font(R.font.playfair, FontWeight.Normal),
    Font(R.font.playfair, FontWeight.Medium),
    Font(R.font.playfair_bold, FontWeight.SemiBold),
    Font(R.font.playfair_bold, FontWeight.Bold),
    Font(R.font.playfair_bold, FontWeight.ExtraBold),
    Font(R.font.playfair_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.playfair_italic, FontWeight.Medium, FontStyle.Italic),
    Font(R.font.playfair_bold_italic, FontWeight.SemiBold, FontStyle.Italic),
    Font(R.font.playfair_bold_italic, FontWeight.Bold, FontStyle.Italic),
    Font(R.font.playfair_bold_italic, FontWeight.ExtraBold, FontStyle.Italic)
)

val Playfair = PlayfairFont
val PlayfairDisplay = PlayfairFont // Alias for full backward compatibility across all screens
val PlayfairBoldItalic = FontFamily(
    Font(R.font.playfair_bold_italic, FontWeight.Bold, FontStyle.Italic)
)

// DM Sans: primary typography for headings and body, with dedicated Bold font resource
val DmSans = FontFamily(
    Font(R.font.dm_sans, FontWeight.Normal),
    Font(R.font.dm_sans, FontWeight.Medium),
    Font(R.font.dm_sans_bold, FontWeight.SemiBold),
    Font(R.font.dm_sans_bold, FontWeight.Bold),
    Font(R.font.dm_sans_bold, FontWeight.ExtraBold),
    Font(R.font.dm_sans_bold, FontWeight.Black)
)

// DM Mono: for small labels and interface details
val DmMono = FontFamily(
    Font(R.font.dm_mono, FontWeight.Normal),
    Font(R.font.dm_mono, FontWeight.Medium)
)

// Backward compatibility alias if needed
val PlusJakartaSans = DmSans

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = DmSans,
        fontWeight = FontWeight.Bold,
        fontSize = 42.sp,
        lineHeight = 46.sp,
        letterSpacing = (-1.2).sp,
        color = GuestFlowInk
    ),
    headlineLarge = TextStyle(
        fontFamily = DmSans,
        fontWeight = FontWeight.Bold,
        fontSize = 34.sp,
        lineHeight = 38.sp,
        letterSpacing = (-0.8).sp,
        color = GuestFlowInk
    ),
    headlineMedium = TextStyle(
        fontFamily = DmSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 26.sp,
        lineHeight = 32.sp,
        letterSpacing = (-0.4).sp,
        color = GuestFlowInk
    ),
    titleLarge = TextStyle(
        fontFamily = DmSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        color = GuestFlowInk
    ),
    titleMedium = TextStyle(
        fontFamily = DmSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        color = GuestFlowInk
    ),
    bodyLarge = TextStyle(
        fontFamily = DmSans,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        color = GuestFlowMuted
    ),
    bodyMedium = TextStyle(
        fontFamily = DmSans,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        color = GuestFlowMuted
    ),
    labelLarge = TextStyle(
        fontFamily = DmSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        color = GuestFlowWhite
    ),
    labelSmall = TextStyle(
        fontFamily = DmMono,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        color = GuestFlowMuted
    )
)
