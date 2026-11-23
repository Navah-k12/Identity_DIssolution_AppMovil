package com.example.identity_dissolution_appmovil.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

/**
 * Tema activo (Ajustes → Tema claro). Es un estado de Compose: cada color de abajo
 * se lee a través de un getter, así que al cambiarlo toda la app se vuelve a dibujar.
 */
object AppTheme {
    var light by mutableStateOf(false)
}

private fun pick(dark: Long, light: Long) = Color(if (AppTheme.light) light else dark)

// Oscuro: tokens del diseño "Identity" (Figma).
// Claro: paletas del videojuego (GDD/Media/Referencia): "PaletaBase" (hueso y gris cálido
// de los documentos de Vitalis) y "PaletaCarmesi" (rojo del piso 3) para los acentos.
val Background: Color get() = pick(0xFF0A0A0A, 0xFFE9E4DE)  // Fondo · papel clínico
val Surface: Color get() = pick(0xFF161213, 0xFFF6F2EE)     // Superficie · hoja de expediente
val SurfaceHigh: Color get() = pick(0xFF1E1415, 0xFFDDD5CF) // Superficie elevada
val Border: Color get() = pick(0xFF3A1218, 0xFFC4B6B0)      // Borde
val BloodRed: Color get() = pick(0xFFE63839, 0xFF922336)    // Primario · carmesí del juego
val SoftRed: Color get() = pick(0xFFF26A6B, 0xFFA8304A)     // Rojo suave
val MidRed: Color get() = pick(0xFFC24445, 0xFF7A1C2D)      // Rojo medio
val DarkRed: Color get() = pick(0xFF8B0101, 0xFF51111F)     // Rojo oscuro
val TextMain: Color get() = pick(0xFFEDE7E5, 0xFF0F0607)    // Texto
val TextMuted: Color get() = pick(0xFFA39B98, 0xFF4A3F3D)   // Texto secundario
val TextFaint: Color get() = pick(0xFF8F8683, 0xFF746A68)   // Texto tenue
val OnRed: Color get() = pick(0xFF0A0A0A, 0xFFFFFFFF)       // Tinta sobre rojo
