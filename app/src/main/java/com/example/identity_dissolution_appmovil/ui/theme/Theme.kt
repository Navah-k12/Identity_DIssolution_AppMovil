package com.example.identity_dissolution_appmovil.ui.theme

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

// Se crea en cada composición: los colores dependen de AppTheme.light.
// Se parte del esquema claro u oscuro de Material y se reemplazan los colores con los nuestros.
private fun colors() = (if (AppTheme.light) lightColorScheme() else darkColorScheme()).copy(
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

// Tema oscuro de terror o tema claro con las paletas del juego (Ajustes). Sin colores dinámicos del sistema.
@Composable
fun Identity_DIssolution_AppMovilTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = colors(),
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
