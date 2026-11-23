package com.example.identity_dissolution_appmovil.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.identity_dissolution_appmovil.ui.theme.BloodRed
import kotlinx.coroutines.delay
import kotlin.random.Random

/** Ajustes visuales disponibles en cualquier pantalla sin pasarlos por parámetro. */
data class VisualEffects(val glitch: Boolean = false, val intensity: Float = 0.6f)

val LocalVisualEffects = compositionLocalOf { VisualEffects() }

/** Líneas horizontales finas, como un monitor CRT antiguo. */
fun Modifier.retroScanlines(enabled: Boolean): Modifier =
    if (!enabled) this else drawWithContent {
        drawContent()
        val gap = 4.dp.toPx()
        var y = 0f
        while (y < size.height) {
            drawLine(Color.Black.copy(alpha = 0.25f), Offset(0f, y), Offset(size.width, y), strokeWidth = 1f)
            y += gap
        }
    }

/**
 * Texto que de vez en cuando "falla": se desplaza y deja copias roja y cian.
 * Si el usuario desactiva el glitch en Ajustes, se dibuja como un Text normal.
 */
@Composable
fun GlitchText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified,
    textAlign: TextAlign? = null,
) {
    val effects = LocalVisualEffects.current
    val enabled = effects.glitch && effects.intensity > 0f
    // Con 100 % el texto se desplaza hasta 7 dp; con 10 % apenas 1 dp.
    val maxShift = (effects.intensity * 7).toInt().coerceAtLeast(1)
    var shift by remember { mutableIntStateOf(0) }

    if (enabled) {
        LaunchedEffect(text, maxShift) {
            while (true) {
                delay(Random.nextLong(1500, 4000))
                repeat(4) {
                    shift = Random.nextInt(-maxShift, maxShift + 1)
                    delay(50)
                }
                shift = 0
            }
        }
    }

    Box(modifier) {
        if (enabled && shift != 0) {
            Text(text, style = style, color = Color.Cyan.copy(alpha = 0.6f), textAlign = textAlign,
                modifier = Modifier.offset(x = (-shift).dp))
            Text(text, style = style, color = Color.Red.copy(alpha = 0.6f), textAlign = textAlign,
                modifier = Modifier.offset(x = shift.dp))
        }
        Text(text, style = style, color = color, textAlign = textAlign,
            modifier = Modifier.offset(x = (shift / 2).dp))
    }
}
