package com.example.identity_dissolution_appmovil.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Fuente "máquina de escribir" para títulos, como en el diseño.
val HorrorFont = FontFamily.Monospace

val Typography = Typography(
    titleLarge = TextStyle(fontFamily = HorrorFont, fontWeight = FontWeight.Bold, fontSize = 20.sp, letterSpacing = 2.sp),
    titleMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 16.sp),
    bodyLarge = TextStyle(fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall = TextStyle(fontSize = 12.sp, lineHeight = 16.sp),
    labelSmall = TextStyle(fontFamily = HorrorFont, fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 1.5.sp),
)
