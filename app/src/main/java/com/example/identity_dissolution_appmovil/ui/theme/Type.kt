package com.example.identity_dissolution_appmovil.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Títulos y etiquetas: monoespaciada (el diseño usa JetBrains Mono). Cuerpo: Roboto (la del sistema).
val HorrorFont = FontFamily.Monospace

val Typography = Typography(
    headlineSmall = TextStyle(fontFamily = HorrorFont, fontWeight = FontWeight.Bold, fontSize = 22.sp, letterSpacing = 1.sp),
    titleLarge = TextStyle(fontFamily = HorrorFont, fontWeight = FontWeight.Bold, fontSize = 20.sp, letterSpacing = 2.sp),
    titleMedium = TextStyle(fontWeight = FontWeight.Medium, fontSize = 16.sp),
    bodyLarge = TextStyle(fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall = TextStyle(fontSize = 13.sp, lineHeight = 18.sp),
    labelMedium = TextStyle(fontFamily = HorrorFont, fontWeight = FontWeight.Medium, fontSize = 11.sp, letterSpacing = 2.sp),
    labelSmall = TextStyle(fontFamily = HorrorFont, fontWeight = FontWeight.Bold, fontSize = 10.sp, letterSpacing = 1.5.sp),
)
