package com.example.identity_dissolution_appmovil.ui.screens

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.widget.MediaController
import android.widget.VideoView
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.identity_dissolution_appmovil.R
import com.example.identity_dissolution_appmovil.data.repository.GameRepository
import com.example.identity_dissolution_appmovil.ui.components.EcgLine
import com.example.identity_dissolution_appmovil.ui.components.Eyebrow
import com.example.identity_dissolution_appmovil.ui.components.GlitchText
import com.example.identity_dissolution_appmovil.ui.components.HorrorCard
import com.example.identity_dissolution_appmovil.ui.components.SegmentedProgress
import com.example.identity_dissolution_appmovil.ui.components.Tag
import com.example.identity_dissolution_appmovil.ui.components.ThinProgress
import com.example.identity_dissolution_appmovil.ui.navigation.Routes
import com.example.identity_dissolution_appmovil.ui.theme.Background
import com.example.identity_dissolution_appmovil.ui.theme.BloodRed
import com.example.identity_dissolution_appmovil.ui.theme.Border
import com.example.identity_dissolution_appmovil.ui.theme.DarkRed
import com.example.identity_dissolution_appmovil.ui.theme.Identity_DIssolution_AppMovilTheme
import com.example.identity_dissolution_appmovil.ui.theme.OnRed
import com.example.identity_dissolution_appmovil.ui.theme.SurfaceHigh
import com.example.identity_dissolution_appmovil.ui.theme.TextMain
import com.example.identity_dissolution_appmovil.ui.theme.TextMuted
import com.example.identity_dissolution_appmovil.viewmodel.ContinueReading
import com.example.identity_dissolution_appmovil.viewmodel.HomeUiState
import com.example.identity_dissolution_appmovil.viewmodel.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.random.Random

private data class QuickAccess(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val route: String,
    val badge: String? = null,
    val badgeIsNew: Boolean = false,
)

@Composable
fun HomeScreen(onNavigate: (String) -> Unit, viewModel: HomeViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()
    HomeContent(state, onNavigate)
}

/** Diseño sin ViewModel: así se puede ver en @Preview. */
@Composable
fun HomeContent(state: HomeUiState, onNavigate: (String) -> Unit) {
    val quickAccess = remember(state.filesTotal) {
        listOf(
            QuickAccess("Enciclopedia", "Mecánicas de juego", Icons.AutoMirrored.Outlined.MenuBook, Routes.ENCYCLOPEDIA, badge = "${state.filesTotal}"),
            QuickAccess("Bestiario", "Amenazas registradas", Icons.Outlined.Visibility, Routes.BESTIARY, badge = "Nuevo", badgeIsNew = true),
            QuickAccess("Controles", "Teclado y mando", Icons.Outlined.Keyboard, Routes.CONTROLS),
            QuickAccess("Historia", "Cronología del lore", Icons.Outlined.History, Routes.HISTORY),
        )
    }

    // Un solo pulso para el corazón y el monitor, así laten juntos.
    var bpm by remember { mutableIntStateOf(72) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(2500)
            bpm = (bpm + Random.nextInt(-2, 3)).coerceIn(66, 84)
        }
    }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
        // Encabezado: sesión, misión y pulso.
        Spacer(Modifier.height(20.dp))
        Row(verticalAlignment = Alignment.Top) {
            Column(Modifier.weight(1f)) {
                Eyebrow("// Sesión activa")
                Spacer(Modifier.height(4.dp))
                GlitchText(
                    "MISIÓN ACTUAL:\nSOBREVIVIR",
                    style = MaterialTheme.typography.titleLarge,
                    color = BloodRed,
                )
            }
            HeartRateChip(bpm)
        }
        Spacer(Modifier.height(8.dp))
        EcgLine(bpm)

        Spacer(Modifier.height(20.dp))
        Eyebrow("Continuar lectura")
        Spacer(Modifier.height(10.dp))
        state.reading?.let { ContinueCard(it) { onNavigate(it.route) } }

        Spacer(Modifier.height(24.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Eyebrow("Tráiler oficial", Modifier.weight(1f))
            TrailerDuration()
        }
        Spacer(Modifier.height(10.dp))
        TrailerCard()

        Spacer(Modifier.height(20.dp))
        FilesReadCard(state)

        Spacer(Modifier.height(24.dp))
        Eyebrow("Accesos rápidos")
        Spacer(Modifier.height(10.dp))
        // Rejilla 2x2 hecha con filas: así no anidamos un LazyGrid dentro de un scroll.
        quickAccess.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { item -> QuickCard(item, Modifier.weight(1f)) { onNavigate(item.route) } }
            }
            Spacer(Modifier.height(12.dp))
        }
        Spacer(Modifier.height(12.dp))
    }
}

/** Pastilla con un corazón que late y un pulso que varía un poco. */
@Composable
private fun HeartRateChip(bpm: Int) {
    // El icono late con el mismo ritmo (bpm) que el monitor.
    val beat by rememberInfiniteTransition(label = "beat").animateFloat(
        initialValue = 1f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            keyframes {
                durationMillis = 60_000 / bpm
                1f at 0; 1.25f at 100; 1f at 220; 1.15f at 320; 1f at 450
            },
            RepeatMode.Restart,
        ),
        label = "scale",
    )
    Row(
        Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(SurfaceHigh)
            .border(1.dp, Border, RoundedCornerShape(24.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(Icons.Filled.Favorite, "Pulso", tint = BloodRed, modifier = Modifier.size(20.dp).scale(beat))
        Spacer(Modifier.width(8.dp))
        Text("$bpm", style = MaterialTheme.typography.titleLarge, color = TextMain)
        Spacer(Modifier.width(4.dp))
        Text("BPM", style = MaterialTheme.typography.labelSmall, color = TextMuted)
    }
}

@Composable
private fun PlayButton(size: Int = 40) {
    Box(Modifier.size(size.dp).background(BloodRed, CircleShape), contentAlignment = Alignment.Center) {
        Icon(Icons.Filled.PlayArrow, "Reproducir", tint = OnRed, modifier = Modifier.size((size / 2).dp))
    }
}

/** Tarjeta con la miniatura de lo último que se leyó. */
@Composable
private fun ContinueCard(item: ContinueReading, onClick: () -> Unit) {
    HorrorCard(Modifier.fillMaxWidth(), onClick = onClick) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(SurfaceHigh)
                    .border(1.dp, DarkRed, RoundedCornerShape(10.dp)),
            ) {
                if (item.imageRes != null) {
                    Image(
                        painterResource(item.imageRes), item.title,
                        contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize(),
                    )
                }
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(item.title, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(item.subtitle, style = MaterialTheme.typography.bodySmall, color = TextMuted, maxLines = 1)
                if (item.progress != null) {
                    Spacer(Modifier.height(8.dp))
                    ThinProgress(item.progress, Modifier.fillMaxWidth(0.9f))
                }
            }
            Spacer(Modifier.width(12.dp))
            PlayButton()
        }
    }
}

/** Duración del tráiler leída del propio archivo ("0:40"). */
@Composable
private fun TrailerDuration() {
    val context = LocalContext.current
    val text by produceState("") {
        value = withContext(Dispatchers.IO) {
            runCatching {
                MediaMetadataRetriever().run {
                    setDataSource(context, trailerUri(context.packageName))
                    val ms = extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0L
                    release()
                    val s = ms / 1000
                    "%d:%02d".format(s / 60, s % 60)
                }
            }.getOrDefault("")
        }
    }
    Text(text, style = MaterialTheme.typography.labelMedium, color = TextMuted)
}

private fun trailerUri(pkg: String): Uri = Uri.parse("android.resource://$pkg/${R.raw.trailer}")

/** Tráiler del juego: muestra un fotograma y se reproduce aquí mismo al tocarlo. */
@Composable
private fun TrailerCard() {
    val context = LocalContext.current
    var playing by rememberSaveable { mutableStateOf(false) }
    // Fotograma del segundo 3 como portada.
    val poster by produceState<Bitmap?>(null) {
        value = withContext(Dispatchers.IO) {
            runCatching {
                MediaMetadataRetriever().run {
                    setDataSource(context, trailerUri(context.packageName))
                    getFrameAtTime(3_000_000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC).also { release() }
                }
            }.getOrNull()
        }
    }
    val shape = RoundedCornerShape(16.dp)
    Box(
        Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .clip(shape)
            .background(Background)
            .border(BorderStroke(1.dp, DarkRed), shape)
            .clickable(enabled = !playing) { playing = true },
        contentAlignment = Alignment.Center,
    ) {
        if (playing) {
            AndroidView(
                factory = { ctx ->
                    VideoView(ctx).apply {
                        setVideoURI(trailerUri(ctx.packageName))
                        setMediaController(MediaController(ctx).also { it.setAnchorView(this) })
                        setOnCompletionListener { playing = false }
                        start()
                    }
                },
                onRelease = { it.stopPlayback() },
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            poster?.let {
                Image(it.asImageBitmap(), "Tráiler", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            }
            PlayButton(size = 52)
        }
    }
}

@Composable
private fun FilesReadCard(state: HomeUiState) {
    HorrorCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Eyebrow("Archivos leídos")
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text("%02d".format(state.filesRead), style = MaterialTheme.typography.headlineSmall, color = BloodRed)
                Text("/${state.filesTotal}", style = MaterialTheme.typography.bodyLarge, color = TextMuted)
                Spacer(Modifier.weight(1f))
                state.nextTitle?.let {
                    Text("Siguiente: ", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                    Text(it, style = MaterialTheme.typography.bodySmall, color = TextMain, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
            Spacer(Modifier.height(12.dp))
            SegmentedProgress(state.filesRead, state.filesTotal, height = 5, gap = 3)
        }
    }
}

@Composable
private fun QuickCard(item: QuickAccess, modifier: Modifier, onClick: () -> Unit) {
    HorrorCard(modifier.height(128.dp), onClick = onClick) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    Modifier.size(38.dp).background(DarkRed.copy(alpha = 0.25f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(item.icon, null, tint = BloodRed, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.weight(1f))
                item.badge?.let {
                    if (item.badgeIsNew) {
                        Text(
                            it.uppercase(), style = MaterialTheme.typography.labelSmall, color = TextMain,
                            modifier = Modifier.background(BloodRed, RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp),
                        )
                    } else {
                        Text(
                            it, style = MaterialTheme.typography.labelSmall, color = TextMuted,
                            modifier = Modifier.border(1.dp, Border, RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 3.dp),
                        )
                    }
                }
            }
            Spacer(Modifier.weight(1f))
            Text(item.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
            Text(item.subtitle, style = MaterialTheme.typography.bodySmall, color = TextMuted)
        }
    }
}

@Preview(showSystemUi = true, showBackground = true, backgroundColor = 0xFF0A0A0A)
@Composable
fun HomeScreenPreview() {
    Identity_DIssolution_AppMovilTheme {
        HomeContent(
            state = HomeUiState(
                reading = ContinueReading(
                    "El incidente", "Guía · Lore · 4 min restantes",
                    imageRes = GameRepository.articles.first().image, progress = 0.62f,
                    route = Routes.article(1),
                ),
                filesRead = 7,
                nextTitle = "El latido",
            ),
            onNavigate = {},
        )
    }
}
