package com.example.identity_dissolution_appmovil.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.identity_dissolution_appmovil.R
import com.example.identity_dissolution_appmovil.ui.components.GlitchText
import com.example.identity_dissolution_appmovil.ui.theme.BloodRed
import com.example.identity_dissolution_appmovil.ui.theme.DarkRed
import com.example.identity_dissolution_appmovil.ui.theme.Identity_DIssolution_AppMovilTheme
import com.example.identity_dissolution_appmovil.ui.theme.TextMuted
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

/**
 * Creado por Bryan el 31/10/2026 a las 04:30 p.m..
 * Proyecto: Identity_DIssolution_AppMovil
 */

// Fuente con gotas de sangre (Rubik Wet Paint, Google Fonts, licencia OFL) en res/font.
private val BloodFont = FontFamily(Font(R.font.rubik_wet_paint))

private val WelcomeBlack = Color(0xFF070707)
private val LoadButtonColor = Color(0xFF1A1516)

@Composable
fun Welcome(onStart: () -> Unit) {
    var currentProgress by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        for (i in 1..100) {
            currentProgress = i / 100f
            delay(20.milliseconds)
        }
    }
    val loaded = currentProgress >= 1f
    // Los botones aparecen poco a poco y solo se pueden tocar al terminar la carga.
    val buttonsAlpha by animateFloatAsState(if (loaded) 1f else 0.25f, tween(500), label = "buttons")

    Box(
        Modifier
            .fillMaxSize()
            .background(WelcomeBlack)
            // Resplandor rojo muy suave detrás del logo.
            .drawBehind {
                drawCircle(
                    Brush.radialGradient(
                        listOf(DarkRed.copy(alpha = 0.22f), Color.Transparent),
                        center = Offset(size.width / 2, size.height * 0.33f),
                        radius = size.width * 0.6f,
                    ),
                    radius = size.width * 0.6f,
                    center = Offset(size.width / 2, size.height * 0.33f),
                )
            },
    ) {
        Column(
            Modifier.fillMaxSize().padding(horizontal = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            HeartbeatLogo()

            Spacer(Modifier.height(36.dp))

            GlitchText(
                text = "HEARTBEAT\nCOMPANION",
                color = BloodRed,
                style = TextStyle(
                    fontFamily = BloodFont,
                    fontSize = 44.sp,
                    lineHeight = 48.sp,
                    // Brillo rojo alrededor de las letras.
                    shadow = Shadow(color = BloodRed.copy(alpha = 0.6f), blurRadius = 24f),
                ),
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(40.dp))

            Column(Modifier.alpha(buttonsAlpha)) {
                Button(
                    onClick = onStart,
                    enabled = loaded,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkRed,
                        contentColor = Color.White,
                        disabledContainerColor = DarkRed,
                        disabledContentColor = Color.White,
                    ),
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                ) { Text("EMPEZAR AVENTURA", fontWeight = FontWeight.Bold, letterSpacing = 1.sp) }

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = onStart,
                    enabled = loaded,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFF241C1D)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LoadButtonColor,
                        contentColor = TextMuted,
                        disabledContainerColor = LoadButtonColor,
                        disabledContentColor = TextMuted,
                    ),
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                ) { Text("CARGAR DATOS", fontWeight = FontWeight.Bold, letterSpacing = 1.sp) }
            }
        }

        // Abajo: corazón pequeño + barra de carga fina, como en la referencia.
        Row(
            Modifier.align(Alignment.BottomCenter).padding(bottom = 48.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(id = R.drawable.corazon),
                contentDescription = null,
                modifier = Modifier.size(18.dp).alpha(0.6f),
            )
            Spacer(Modifier.width(10.dp))
            LoadLine(progress = { currentProgress })
        }
    }
}

/** Logo dibujado en código: círculo oscuro, aro rojo, texto y línea de pulso. */
@Composable
private fun HeartbeatLogo() {
    Box(
        Modifier
            .size(200.dp)
            // Halo rojo fijo alrededor del aro.
            .drawBehind {
                drawCircle(
                    Brush.radialGradient(
                        listOf(BloodRed.copy(alpha = 0.25f), Color.Transparent),
                        radius = size.minDimension * 0.62f,
                    ),
                    radius = size.minDimension * 0.62f,
                )
            }
            .border(BorderStroke(2.dp, DarkRed), CircleShape)
            .padding(2.dp)
            .background(
                Brush.radialGradient(listOf(Color(0xFF1C1C1C), Color(0xFF050505))),
                CircleShape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "HEARTBEAT",
                color = Color(0xFF8A8A8A),
                fontFamily = FontFamily.Serif,
                fontSize = 24.sp,
                letterSpacing = 1.sp,
            )
            // Línea de electrocardiograma.
            Canvas(Modifier.width(150.dp).height(22.dp)) {
                val w = size.width
                val mid = size.height / 2
                val path = Path().apply {
                    moveTo(0f, mid)
                    lineTo(w * 0.38f, mid)
                    lineTo(w * 0.44f, mid - size.height * 0.45f)
                    lineTo(w * 0.50f, mid + size.height * 0.45f)
                    lineTo(w * 0.56f, mid - size.height * 0.2f)
                    lineTo(w * 0.60f, mid)
                    lineTo(w, mid)
                }
                drawPath(
                    path,
                    color = Color(0xFF6E6E6E),
                    style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round),
                )
            }
            Text(
                "COMPANION",
                color = Color(0xFF5E5E5E),
                fontFamily = FontFamily.Serif,
                fontSize = 9.sp,
                letterSpacing = 4.sp,
            )
        }
    }
}

@Composable
fun LoadLine(progress: () -> Float) {
    LinearProgressIndicator(
        progress = progress,
        color = DarkRed,
        trackColor = Color(0xFF1E1E1E),
        strokeCap = StrokeCap.Round,
        gapSize = 0.dp,
        drawStopIndicator = {},
        modifier = Modifier.width(140.dp).height(2.dp),
    )
}

@Preview(showSystemUi = true)
@Composable
fun WelcomePreview() {
    Identity_DIssolution_AppMovilTheme { Welcome(onStart = {}) }
}
