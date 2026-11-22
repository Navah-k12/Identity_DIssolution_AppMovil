package com.example.identity_dissolution_appmovil.ui.theme

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

private val HeartbeatColors = darkColorScheme(
    primary = BloodRed,
    onPrimary = OnRed,
    primaryContainer = DarkRed,
    secondary = TextMuted,
    background = Background,
    onBackground = TextMain,
    surface = Surface,
    onSurface = TextMain,
    surfaceVariant = SurfaceHigh,
    onSurfaceVariant = TextMuted,
    outline = Border,
)

// La app siempre usa el tema oscuro de terror (sin colores dinámicos del sistema).
@Composable
fun Identity_DIssolution_AppMovilTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = HeartbeatColors,
        typography = Typography,
    ) {
        // Fondo negro y texto claro para TODO lo que esté dentro del tema:
        // la app completa, las vistas previas y las vistas previas lanzadas en el emulador.
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Background,
            contentColor = TextMain,
            content = content,
        )
    }
}
