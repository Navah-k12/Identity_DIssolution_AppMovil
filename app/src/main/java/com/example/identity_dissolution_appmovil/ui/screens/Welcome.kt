package com.example.identity_dissolution_appmovil.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.identity_dissolution_appmovil.ui.components.EcgLine
import com.example.identity_dissolution_appmovil.ui.components.GlitchText
import com.example.identity_dissolution_appmovil.ui.theme.BloodRed
import com.example.identity_dissolution_appmovil.ui.theme.DarkRed
import com.example.identity_dissolution_appmovil.ui.theme.HorrorFont
import com.example.identity_dissolution_appmovil.ui.theme.Identity_DIssolution_AppMovilTheme
import com.example.identity_dissolution_appmovil.ui.theme.TextMain
import com.example.identity_dissolution_appmovil.ui.theme.TextMuted
import kotlinx.coroutines.delay

/**
 * Creado por Bryan el 31/10/2026 a las 04:30 p.m..
 * Proyecto: Identity_DIssolution_AppMovil
 *
 * Pantalla de carga (diseño "Pantalla de carga" de Figma): corazón que late, monitor cardíaco,
 * título IDENTITY DISSOLUTION y barra de progreso. Al terminar, se toca para entrar.
 */

private val LoadingBlack = Color(0xFF070707)

/** Mensajes que cambian mientras la barra avanza. */
private val steps = listOf(
    0f to "CONECTANDO CON THE MOTHER…",
    0.25f to "RECUPERANDO REGISTROS DE VITALIS…",
    0.5f to "RECONSTRUYENDO IDENTIDAD…",
    0.75f to "ANALIZANDO AMENAZAS DEL LABORATORIO…",
)

@Composable
fun Welcome(onStart: () -> Unit) {
    var progress by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        for (i in 1..100) {
            progress = i / 100f
            delay(25)
        }
    }
    val loaded = progress >= 1f
    val status = if (loaded) "IDENTIDAD RESTAURADA" else steps.last { progress >= it.first }.second

    // "Toca para entrar" parpadea cuando la carga termina.
    val blink by rememberInfiniteTransition(label = "blink").animateFloat(
        0.35f, 1f, infiniteRepeatable(tween(800), RepeatMode.Reverse), label = "a",
    )

    Box(
        Modifier
            .fillMaxSize()
            .background(LoadingBlack)
            .drawBehind {
                // Resplandor rojo detrás del corazón.
                val c = Offset(size.width / 2, size.height * 0.36f)
                drawCircle(
                    Brush.radialGradient(listOf(DarkRed.copy(alpha = 0.35f), Color.Transparent), c, size.width * 0.7f),
                    size.width * 0.7f, c,
                )
                // Líneas de monitor antiguo.
                val step = 4.dp.toPx()
                var y = 0f
                while (y < size.height) {
                    drawLine(Color.White.copy(alpha = 0.025f), Offset(0f, y), Offset(size.width, y), 1f)
                    y += step
                }
            }
            .clickable(
                enabled = loaded,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onStart,
            ),
    ) {
        // Barra superior.
        Row(
            Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("COMPANION APP", color = TextMuted, fontFamily = HorrorFont, fontSize = 10.sp, letterSpacing = 2.sp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(6.dp).alpha(blink).background(BloodRed, CircleShape))
                Spacer(Modifier.width(6.dp))
                Text("EN LÍNEA", color = TextMuted, fontFamily = HorrorFont, fontSize = 10.sp, letterSpacing = 2.sp)
            }
        }

        Column(
            Modifier.fillMaxSize().padding(bottom = 120.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            BeatingHeart()
            Spacer(Modifier.height(16.dp))
            // Monitor que se desvanece a los lados.
            Box(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                EcgLine(bpm = 72)
            }
            Spacer(Modifier.height(28.dp))
            Text3D("IDENTITY", face = TextMain, side = Color(0xFF7A1418))
            Text3D("DISSOLUTION", face = BloodRed, side = Color(0xFF4A0A0D))
            Spacer(Modifier.height(18.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.width(28.dp).height(1.dp).background(TextMuted.copy(alpha = 0.4f)))
                Text(
                    "ARCHIVOS DE VITALIS", color = TextMuted, fontFamily = HorrorFont, fontSize = 11.sp,
                    letterSpacing = 3.sp, modifier = Modifier.padding(horizontal = 12.dp),
                )
                Box(Modifier.width(28.dp).height(1.dp).background(TextMuted.copy(alpha = 0.4f)))
            }
        }

        // Abajo: estado, barra de carga y versión.
        Column(
            Modifier.align(Alignment.BottomCenter).fillMaxWidth().navigationBarsPadding()
                .padding(horizontal = 28.dp, vertical = 40.dp),
        ) {
            Text(status, color = TextMain, fontFamily = HorrorFont, fontSize = 11.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            Box(Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)).background(Color(0xFF2A1416))) {
                Box(
                    Modifier.fillMaxWidth(progress).height(4.dp)
                        .background(Brush.horizontalGradient(listOf(DarkRed, BloodRed)))
                )
            }
            Spacer(Modifier.height(24.dp))
            Text(
                if (loaded) "v1.0 · TOCA PARA ENTRAR" else "v1.0",
                color = TextMuted, fontFamily = HorrorFont, fontSize = 10.sp, letterSpacing = 2.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally).alpha(if (loaded) blink else 0.6f),
            )
        }
    }
}

/**
 * Letras en 3D: se dibuja el texto varias veces, cada copia 1 dp más abajo y en un rojo oscuro
 * (el "grosor"), y encima la cara frontal con brillo. Es el mismo truco del arte de la pantalla de carga.
 */
@Composable
private fun Text3D(text: String, face: Color, side: Color) {
    val style = TextStyle(fontFamily = HorrorFont, fontWeight = FontWeight.Black, fontSize = 32.sp, letterSpacing = 3.sp)
    Box {
        for (i in 6 downTo 1) {
            Text(
                text, style = style, color = side.copy(alpha = 1f - i * 0.08f),
                modifier = Modifier.graphicsLayer { translationY = i * 1.dp.toPx(); translationX = i * 0.3.dp.toPx() },
            )
        }
        GlitchText(
            text = text, color = face,
            style = style.copy(shadow = Shadow(BloodRed.copy(alpha = 0.6f), blurRadius = 16f)),
        )
    }
}

/** Corazón dentro de un círculo oscuro con aro rojo; late y el aro emite una onda. */
@Composable
private fun BeatingHeart() {
    val t = rememberInfiniteTransition(label = "heart")
    val beat by t.animateFloat(
        1f, 1.14f, infiniteRepeatable(tween(420, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "beat",
    )
    val wave by t.animateFloat(0f, 1f, infiniteRepeatable(tween(1600)), label = "wave")

    Box(Modifier.size(130.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val r = size.minDimension / 2
            drawCircle(BloodRed.copy(alpha = 0.45f), r * 0.98f, style = Stroke(1.dp.toPx()))
            // Onda que sale del corazón y se apaga.
            drawCircle(BloodRed.copy(alpha = 0.35f * (1 - wave)), r * (0.7f + 0.3f * wave), style = Stroke(1.5.dp.toPx()))
        }
        Box(
            Modifier.size(84.dp).background(Color(0xFF181314), CircleShape)
                .border(1.dp, Color(0xFF2A1C1D), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Filled.Favorite, null, tint = BloodRed,
                modifier = Modifier.size(36.dp).graphicsLayer { scaleX = beat; scaleY = beat },
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun WelcomePreview() {
    Identity_DIssolution_AppMovilTheme { Welcome(onStart = {}) }
}
