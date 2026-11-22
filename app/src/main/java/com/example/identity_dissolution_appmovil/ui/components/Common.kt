package com.example.identity_dissolution_appmovil.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import kotlin.math.exp
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.identity_dissolution_appmovil.ui.theme.DarkRed
import com.example.identity_dissolution_appmovil.ui.theme.SoftRed
import com.example.identity_dissolution_appmovil.ui.theme.SurfaceHigh
import com.example.identity_dissolution_appmovil.ui.theme.TextMain
import com.example.identity_dissolution_appmovil.ui.theme.TextMuted
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.identity_dissolution_appmovil.ui.theme.BloodRed
import com.example.identity_dissolution_appmovil.ui.theme.Border
import com.example.identity_dissolution_appmovil.ui.theme.Surface

/** Barra superior con título rojo y botón de volver opcional. */
@Composable
fun HorrorTopBar(
    title: String,
    onBack: (() -> Unit)? = null,
    actions: @Composable () -> Unit = {},
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().height(56.dp).padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = BloodRed)
                }
            } else {
                Spacer(Modifier.width(8.dp))
            }
            GlitchText(
                text = title.uppercase(),
                style = MaterialTheme.typography.titleLarge,
                color = BloodRed,
                modifier = Modifier.weight(1f),
            )
            actions()
        }
        HorizontalDivider(color = Border)
    }
}

/** Etiqueta pequeña de sección con barrita roja ("MOVIMIENTO", "PREFERENCIAS"...). */
@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Row(modifier.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(width = 3.dp, height = 12.dp).background(BloodRed))
        Spacer(Modifier.width(8.dp))
        Text(text.uppercase(), style = MaterialTheme.typography.labelSmall, color = BloodRed)
    }
}

/** Tarjeta oscura con borde rojizo usada en casi todas las pantallas. */
@Composable
fun HorrorCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = RoundedCornerShape(16.dp)
    val colors = CardDefaults.cardColors(containerColor = Surface)
    val border = BorderStroke(1.dp, Border)
    if (onClick != null) {
        Card(onClick = onClick, modifier = modifier, shape = shape, colors = colors, border = border, content = content)
    } else {
        Card(modifier = modifier, shape = shape, colors = colors, border = border, content = content)
    }
}

// ---------------------------------------------------------------------------------------------
// Piezas del diseño "Identity".
// ---------------------------------------------------------------------------------------------

/** Encabezado de pantalla principal: título rojo grande, con espacio a la izquierda y a la derecha. */
@Composable
fun ScreenHeader(
    title: String,
    modifier: Modifier = Modifier,
    leading: (@Composable () -> Unit)? = null,
    trailing: @Composable () -> Unit = {},
) {
    Row(
        modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leading != null) {
            leading()
            Spacer(Modifier.width(12.dp))
        }
        GlitchText(
            text = title.uppercase(),
            style = MaterialTheme.typography.titleLarge,
            color = BloodRed,
            modifier = Modifier.weight(1f),
        )
        trailing()
    }
}

/** Botón redondo con borde rojizo (volver, marcador...). */
@Composable
fun CircleIconButton(icon: ImageVector, description: String, onClick: () -> Unit) {
    Box(
        Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(SurfaceHigh)
            .border(1.dp, Border, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, description, tint = BloodRed, modifier = Modifier.size(20.dp))
    }
}

/** Etiqueta pequeña de sección en gris ("CONTINUAR LECTURA"). */
@Composable
fun Eyebrow(text: String, modifier: Modifier = Modifier, color: Color = TextMuted) {
    Text(text.uppercase(), style = MaterialTheme.typography.labelMedium, color = color, modifier = modifier)
}

/** Pastilla de texto: "LORE", "ARMA", "DESTACADO", "NUEVO". */
@Composable
fun Tag(text: String, filled: Boolean = false) {
    Text(
        text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = if (filled) TextMain else SoftRed,
        modifier = Modifier
            .background(if (filled) DarkRed else Border.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp),
    )
}

/** Barra de progreso fina y roja. */
@Composable
fun ThinProgress(value: Float, modifier: Modifier = Modifier) {
    Box(modifier.height(4.dp).clip(RoundedCornerShape(2.dp)).background(Border)) {
        Box(Modifier.fillMaxWidth(value.coerceIn(0f, 1f)).fillMaxHeight().background(BloodRed))
    }
}

/** Barra hecha de segmentos: los primeros [filled] van en rojo. */
@Composable
fun SegmentedProgress(filled: Int, total: Int, modifier: Modifier = Modifier, height: Int = 4, gap: Int = 3) {
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(gap.dp)) {
        repeat(total) { i ->
            Box(
                Modifier
                    .weight(1f)
                    .height(height.dp)
                    .clip(RoundedCornerShape(1.dp))
                    .background(if (i < filled) BloodRed else Border.copy(alpha = 0.8f))
            )
        }
    }
}

/**
 * Monitor cardíaco animado: la onda PQRST avanza de derecha a izquierda al ritmo de [bpm].
 * El trazo se apaga hacia la izquierda y un punto brillante marca el latido actual.
 */
@Composable
fun EcgLine(bpm: Int, modifier: Modifier = Modifier) {
    // Tiempo en segundos, actualizado en cada frame.
    var time by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        val start = withFrameNanos { it }
        while (true) withFrameNanos { time = (it - start) / 1_000_000_000f }
    }
    val beatsPerSecond = bpm / 60f
    val trace = remember { Path() }

    Canvas(modifier.fillMaxWidth().height(36.dp)) {
        val w = size.width
        val mid = size.height * 0.6f
        val amp = size.height * 0.55f
        val windowSeconds = 3.2f            // segundos visibles en pantalla
        val step = 2.dp.toPx()

        trace.reset()
        var x = 0f
        while (x <= w) {
            // El borde derecho es "ahora"; a la izquierda, el pasado.
            val t = time - (w - x) / w * windowSeconds
            val y = mid - ecgWave((t * beatsPerSecond).mod(1f)) * amp
            if (x == 0f) trace.moveTo(x, y) else trace.lineTo(x, y)
            x += step
        }
        val fade = Brush.horizontalGradient(listOf(DarkRed.copy(alpha = 0.15f), DarkRed, BloodRed))
        drawPath(trace, fade, style = Stroke(1.8.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))

        val headY = mid - ecgWave((time * beatsPerSecond).mod(1f)) * amp
        drawCircle(BloodRed.copy(alpha = 0.25f), 6.dp.toPx(), Offset(w, headY))
        drawCircle(BloodRed, 2.5.dp.toPx(), Offset(w, headY))
    }
}

/** Forma de un latido (onda P, complejo QRS y onda T) para una fase 0..1. */
private fun ecgWave(p: Float): Float {
    fun bump(center: Float, width: Float, height: Float): Float {
        val d = (p - center) / width
        return height * exp(-d * d)
    }
    return bump(0.12f, 0.035f, 0.10f) +   // P
        bump(0.27f, 0.010f, -0.12f) +     // Q
        bump(0.30f, 0.012f, 1.00f) +      // R
        bump(0.33f, 0.012f, -0.28f) +     // S
        bump(0.52f, 0.060f, 0.22f)        // T
}
