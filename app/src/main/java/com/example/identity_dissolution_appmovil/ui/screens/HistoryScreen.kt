package com.example.identity_dissolution_appmovil.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.identity_dissolution_appmovil.data.repository.GameRepository
import com.example.identity_dissolution_appmovil.model.StoryEvent
import com.example.identity_dissolution_appmovil.ui.components.EcgLine
import com.example.identity_dissolution_appmovil.ui.components.HorrorTopBar
import com.example.identity_dissolution_appmovil.ui.components.t
import com.example.identity_dissolution_appmovil.ui.components.tr
import com.example.identity_dissolution_appmovil.ui.theme.BloodRed
import com.example.identity_dissolution_appmovil.ui.theme.Border
import com.example.identity_dissolution_appmovil.ui.theme.DarkRed
import com.example.identity_dissolution_appmovil.ui.theme.HorrorFont
import com.example.identity_dissolution_appmovil.ui.theme.Identity_DIssolution_AppMovilTheme
import com.example.identity_dissolution_appmovil.ui.theme.TextMain
import com.example.identity_dissolution_appmovil.ui.theme.TextMuted

private val CardBg get() = com.example.identity_dissolution_appmovil.ui.theme.Surface
private val LineX = 22.dp

/**
 * Cronología de Sektor V-7 como un expediente: cabecera con el monitor cardíaco,
 * una línea roja que brilla, un nodo por evento (los críticos más grandes, el presente late)
 * y tarjetas con imagen, fecha exacta y tipo de registro. Las tarjetas entran una tras otra.
 */
@Composable
fun HistoryScreen(onBack: () -> Unit) {
    val events = GameRepository.story
    Column(Modifier.fillMaxSize()) {
        HorrorTopBar(tr("Cronología"), onBack = onBack)
        LazyColumn(contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 32.dp)) {
            item { Header(events.size) }
            itemsIndexed(events) { index, event ->
                // Separador de año cuando cambia.
                if (index == 0 || events[index - 1].year != event.year) YearMarker(event.year, first = index == 0)
                TimelineRow(event, index, last = index == events.lastIndex)
            }
            item { EndMarker() }
        }
    }
}

@Composable
private fun Header(count: Int) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Brush.verticalGradient(listOf(DarkRed.copy(alpha = 0.35f), CardBg)))
            .border(1.dp, DarkRed.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
            .padding(18.dp),
    ) {
        Text("VITALIS KONZERN · SEKTOR V-7", color = BloodRed, fontFamily = HorrorFont, fontSize = 10.sp, letterSpacing = 2.sp)
        Spacer(Modifier.height(6.dp))
        Text(t("Registro de eventos", "Event log"), color = TextMain, fontFamily = HorrorFont, fontWeight = FontWeight.Bold, fontSize = 22.sp)
        Spacer(Modifier.height(4.dp))
        Text(
            t("$count entradas · 2080 → 2083 · acceso R-H-07", "$count entries · 2080 → 2083 · R-H-07 access"),
            color = TextMuted, fontFamily = HorrorFont, fontSize = 11.sp,
        )
        Spacer(Modifier.height(10.dp))
        EcgLine(bpm = 64)
    }
}

@Composable
private fun YearMarker(year: String, first: Boolean) {
    Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.width(LineX * 2).fillMaxHeight()) {
            if (!first) TimelineLine(Modifier.align(Alignment.Center))
        }
        Text(
            if (year == "Hoy") t("HOY", "NOW") else year,
            color = TextMain, fontFamily = HorrorFont, fontWeight = FontWeight.Black, fontSize = 28.sp,
            letterSpacing = 4.sp, modifier = Modifier.padding(vertical = 10.dp),
        )
        Spacer(Modifier.width(12.dp))
        Box(Modifier.weight(1f).height(1.dp).background(Brush.horizontalGradient(listOf(DarkRed, Color.Transparent))))
    }
}

@Composable
private fun TimelineRow(event: StoryEvent, index: Int, last: Boolean) {
    // Entrada escalonada: cada tarjeta aparece y sube un poco, con 70 ms de retraso entre ellas.
    val appear = remember { Animatable(0f) }
    LaunchedEffect(Unit) { appear.animateTo(1f, tween(450, delayMillis = (index * 70).coerceAtMost(500), easing = FastOutSlowInEasing)) }

    Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
        // Columna de la línea con el nodo.
        Box(Modifier.width(LineX * 2).fillMaxHeight()) {
            TimelineLine(Modifier.align(Alignment.TopCenter), fade = last)
            Node(event, Modifier.align(Alignment.TopCenter).padding(top = 18.dp))
        }
        EventCard(
            event,
            Modifier
                .weight(1f)
                .padding(bottom = 14.dp)
                .graphicsLayer { alpha = appear.value; translationY = (1 - appear.value) * 24.dp.toPx() },
        )
    }
}

@Composable
private fun TimelineLine(modifier: Modifier, fade: Boolean = false) {
    Box(
        modifier
            .width(2.dp)
            .fillMaxHeight()
            .background(
                if (fade) Brush.verticalGradient(listOf(DarkRed, Color.Transparent))
                else Brush.verticalGradient(listOf(DarkRed, DarkRed.copy(alpha = 0.6f)))
            )
    )
}

/** Nodo: rombo para eventos críticos, punto para el resto; el presente emite ondas. */
@Composable
private fun Node(event: StoryEvent, modifier: Modifier) {
    val pulse by rememberInfiniteTransition(label = "node").animateFloat(
        0f, 1f, infiniteRepeatable(tween(1400), RepeatMode.Restart), label = "p",
    )
    val size = if (event.critical || event.current) 18.dp else 12.dp
    Box(modifier.size(36.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.size(36.dp)) {
            // Halo
            drawCircle(Brush.radialGradient(listOf(BloodRed.copy(alpha = 0.45f), Color.Transparent)), radius = this.size.minDimension / 2)
            if (event.current) {
                drawCircle(BloodRed.copy(alpha = 1f - pulse), radius = this.size.minDimension / 2 * (0.4f + 0.6f * pulse), style = Stroke(2.dp.toPx()))
            }
        }
        Box(
            Modifier
                .size(size)
                .graphicsLayer { rotationZ = if (event.critical) 45f else 0f }
                .background(if (event.critical || event.current) BloodRed else Color(0xFF0B0A0A), if (event.critical) RoundedCornerShape(2.dp) else CircleShape)
                .border(2.dp, BloodRed, if (event.critical) RoundedCornerShape(2.dp) else CircleShape)
        )
    }
}

@Composable
private fun EventCard(event: StoryEvent, modifier: Modifier) {
    val accent = if (event.critical || event.current) BloodRed else DarkRed
    Column(
        modifier
            .clip(RoundedCornerShape(12.dp))
            .background(CardBg)
            .border(1.dp, if (event.critical) BloodRed.copy(alpha = 0.6f) else Border, RoundedCornerShape(12.dp))
            // Franja de color a la izquierda.
            .drawBehind { drawRect(accent, size = androidx.compose.ui.geometry.Size(3.dp.toPx(), size.height)) },
    ) {
        event.image?.let {
            Box(Modifier.fillMaxWidth().aspectRatio(16f / 7f)) {
                Image(painterResource(it), null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                // Oscurece abajo para que la etiqueta se lea.
                Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, CardBg))))
                if (event.tag.isNotEmpty()) {
                    Text(
                        event.tag, color = Color.White, fontFamily = HorrorFont, fontSize = 9.sp, letterSpacing = 1.5.sp, fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.TopStart).padding(10.dp)
                            .background(accent.copy(alpha = 0.85f), RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp),
                    )
                }
                if (event.current) {
                    Text(
                        t("● EN VIVO", "● LIVE"), color = BloodRed, fontFamily = HorrorFont, fontSize = 9.sp, fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.TopEnd).padding(10.dp)
                            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp),
                    )
                }
            }
        }
        Column(Modifier.padding(start = 16.dp, end = 14.dp, top = 10.dp, bottom = 14.dp)) {
            event.date?.let {
                Text("▸ $it", color = BloodRed, fontFamily = HorrorFont, fontSize = 11.sp, letterSpacing = 1.sp)
                Spacer(Modifier.height(4.dp))
            }
            Text(event.title, color = TextMain, fontFamily = HorrorFont, fontWeight = FontWeight.Bold, fontSize = 17.sp)
            Spacer(Modifier.height(6.dp))
            Text(event.description, color = TextMuted, fontSize = 13.sp, lineHeight = 19.sp)
        }
    }
}

@Composable
private fun EndMarker() {
    Row(Modifier.fillMaxWidth().padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Spacer(Modifier.width(LineX * 2))
        Text(
            t("// FIN DEL REGISTRO. EL RESTO SE ESCRIBE EN EL JUEGO.", "// END OF LOG. THE REST IS WRITTEN IN THE GAME."),
            color = TextMuted.copy(alpha = 0.7f), fontFamily = HorrorFont, fontSize = 10.sp, letterSpacing = 1.sp,
        )
    }
}

@Preview(showSystemUi = true, showBackground = true, backgroundColor = 0xFF0B0A0A)
@Composable
fun HistoryScreenPreview() {
    Identity_DIssolution_AppMovilTheme { HistoryScreen(onBack = {}) }
}
