package com.example.identity_dissolution_appmovil.ui.screens

import com.example.identity_dissolution_appmovil.ui.components.tr
import com.example.identity_dissolution_appmovil.ui.components.t
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.SentimentVeryDissatisfied
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.identity_dissolution_appmovil.data.repository.GameRepository
import com.example.identity_dissolution_appmovil.model.Enemy
import com.example.identity_dissolution_appmovil.ui.components.CircleIconButton
import com.example.identity_dissolution_appmovil.ui.components.Eyebrow
import com.example.identity_dissolution_appmovil.ui.components.ScreenHeader
import com.example.identity_dissolution_appmovil.ui.components.Tag
import com.example.identity_dissolution_appmovil.ui.components.ThinProgress
import com.example.identity_dissolution_appmovil.ui.theme.Background
import com.example.identity_dissolution_appmovil.ui.theme.BloodRed
import com.example.identity_dissolution_appmovil.ui.theme.Border
import com.example.identity_dissolution_appmovil.ui.theme.DarkRed
import com.example.identity_dissolution_appmovil.ui.theme.HorrorFont
import com.example.identity_dissolution_appmovil.ui.theme.Identity_DIssolution_AppMovilTheme
import com.example.identity_dissolution_appmovil.ui.theme.OnRed
import com.example.identity_dissolution_appmovil.ui.theme.SoftRed
import com.example.identity_dissolution_appmovil.ui.theme.Surface
import com.example.identity_dissolution_appmovil.ui.theme.SurfaceHigh
import com.example.identity_dissolution_appmovil.ui.theme.TextMain
import com.example.identity_dissolution_appmovil.ui.theme.TextMuted
import com.example.identity_dissolution_appmovil.viewmodel.BestiaryUiState
import com.example.identity_dissolution_appmovil.viewmodel.BestiaryViewModel

@Composable
fun BestiaryScreen(
    onBack: () -> Unit,
    onMonsterClick: (Int) -> Unit,
    viewModel: BestiaryViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsState()
    BestiaryContent(state, onBack, onMonsterClick, onRetry = viewModel::refresh)
}

/** Diseño sin ViewModel: así se puede ver en @Preview. */
@Composable
fun BestiaryContent(
    state: BestiaryUiState,
    onBack: () -> Unit,
    onMonsterClick: (Int) -> Unit,
    onRetry: () -> Unit,
) {
    // Sin API ni copia en Room se muestran los datos locales: el bestiario nunca queda vacío.
    val monsters = state.monsters.ifEmpty { if (state.isLoading) emptyList() else GameRepository.enemies }
    val total = GameRepository.enemies.size.coerceAtLeast(monsters.size)

    Column(Modifier.fillMaxSize()) {
        ScreenHeader(
            tr("Bestiario"),
            leading = { CircleIconButton(Icons.AutoMirrored.Filled.ArrowBack, "Volver", onBack) },
            trailing = {
                Text("${monsters.size}", style = MaterialTheme.typography.titleLarge, color = TextMain)
                Text("/$total", style = MaterialTheme.typography.titleLarge, color = TextMuted)
            },
        )

        if (monsters.isEmpty()) {
            Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator(color = BloodRed) }
            return@Column
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item(span = { GridItemSpan(2) }) {
                Row {
                    Eyebrow(tr("Amenazas registradas"), Modifier.weight(1f))
                    Text("${monsters.size} criaturas", style = MaterialTheme.typography.labelMedium, color = TextMuted)
                }
            }
            if (state.error != null && state.monsters.isEmpty()) {
                item(span = { GridItemSpan(2) }) {
                    Text(
                        tr("Sin conexión: mostrando registros guardados. Toca aquí para reintentar."),
                        color = TextMuted, style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.clickable(onClick = onRetry),
                    )
                }
            }
            itemsIndexed(monsters, key = { _, m -> m.id }) { index, monster ->
                MonsterTile(index + 1, monster) { onMonsterClick(monster.id) }
            }
        }
    }
}

/** Imagen del monstruo: primero el arte local, si no la imagen de la API. */
@Composable
private fun MonsterImage(enemy: Enemy, modifier: Modifier) {
    val local = GameRepository.monsterArt[enemy.id]
    when {
        local != null -> Image(painterResource(local), enemy.name, contentScale = ContentScale.Crop, modifier = modifier)
        enemy.imageUrl != null -> AsyncImage(enemy.imageUrl, enemy.name, contentScale = ContentScale.Crop, modifier = modifier)
        else -> Box(modifier.background(SurfaceHigh), Alignment.Center) {
            Icon(Icons.Filled.SentimentVeryDissatisfied, null, tint = BloodRed.copy(alpha = 0.3f), modifier = Modifier.size(64.dp))
        }
    }
}

@Composable
private fun MonsterTile(number: Int, enemy: Enemy, onClick: () -> Unit) {
    val shape = RoundedCornerShape(16.dp)
    Box(
        Modifier
            .fillMaxWidth()
            .aspectRatio(0.72f)
            .clip(shape)
            .border(BorderStroke(1.dp, DarkRed), shape)
            .clickable(onClick = onClick),
    ) {
        MonsterImage(enemy, Modifier.fillMaxSize())
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(0.5f to Color.Transparent, 1f to Background.copy(alpha = 0.95f))
            )
        )
        Column(Modifier.align(Alignment.BottomStart).padding(12.dp)) {
            Text("REGISTRO %02d".format(number), style = MaterialTheme.typography.labelSmall, color = SoftRed)
            Spacer(Modifier.height(4.dp))
            Text(enemy.name, fontFamily = HorrorFont, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = TextMain)
        }
    }
}

@Composable
fun MonsterDetailScreen(
    monsterId: Int,
    onBack: () -> Unit,
    onSummon: (Int) -> Unit,
    viewModel: BestiaryViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsState()
    // Abrir un monstruo lo guarda como tr("última lectura") para Inicio.
    LaunchedEffect(monsterId) { viewModel.markRead(monsterId) }
    val enemy = state.monsters.find { it.id == monsterId } ?: GameRepository.enemies.find { it.id == monsterId }
    MonsterDetailContent(enemy, onBack, onSummon)
}

@Composable
fun MonsterDetailContent(enemy: Enemy?, onBack: () -> Unit, onSummon: (Int) -> Unit) {
    Column(Modifier.fillMaxSize()) {
        ScreenHeader(
            enemy?.name ?: tr("Registro"),
            leading = { CircleIconButton(Icons.AutoMirrored.Filled.ArrowBack, "Volver", onBack) },
        )
        if (enemy == null) {
            Text("Archivo corrupto.", color = TextMuted, modifier = Modifier.padding(20.dp))
            return@Column
        }
        Column(Modifier.verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
            val shape = RoundedCornerShape(16.dp)
            Box(Modifier.fillMaxWidth().aspectRatio(0.9f).clip(shape).border(1.dp, DarkRed, shape)) {
                MonsterImage(enemy, Modifier.fillMaxSize())
                Box(Modifier.fillMaxSize().background(Brush.verticalGradient(0.6f to Color.Transparent, 1f to Background)))
                Row(Modifier.align(Alignment.BottomStart).padding(16.dp)) {
                    Tag(enemy.enemyClass, filled = true)
                    Spacer(Modifier.size(8.dp))
                    Tag("Peligro ${enemy.dangerLevel}")
                }
            }

            Spacer(Modifier.height(20.dp))
            Row {
                StatBar(tr("Peligro"), enemy.danger, Modifier.weight(1f))
                Spacer(Modifier.size(16.dp))
                StatBar(tr("Velocidad"), enemy.speed, Modifier.weight(1f))
            }
            InfoBlock(tr("Comportamiento"), enemy.behavior)
            InfoBlock(tr("Estrategia"), enemy.strategy)

            Spacer(Modifier.height(20.dp))
            Button(
                // Abre la cámara: el monstruo aparece en tu mundo, como en Pokémon GO.
                onClick = { onSummon(enemy.id) },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BloodRed,
                    contentColor = OnRed,
                    disabledContainerColor = SurfaceHigh,
                    disabledContentColor = TextMuted,
                ),
                modifier = Modifier.fillMaxWidth().height(48.dp),
            ) {
                Icon(Icons.Filled.CameraAlt, null)
                Spacer(Modifier.size(8.dp))
                Text("INVOCAR MONSTRUO", fontFamily = HorrorFont, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(16.dp))
            Text(
                tr("ARCHIVO INCOMPLETO... ENCUENTRA MÁS INFORMACIÓN EN EL JUEGO."),
                color = TextMuted, fontFamily = HorrorFont, fontStyle = FontStyle.Italic,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun StatBar(label: String, value: Float, modifier: Modifier) {
    Column(modifier.background(Surface, RoundedCornerShape(12.dp)).border(1.dp, Border, RoundedCornerShape(12.dp)).padding(12.dp)) {
        Row {
            Eyebrow(label, Modifier.weight(1f))
            Text("${(value * 100).toInt()}", style = MaterialTheme.typography.labelMedium, color = BloodRed)
        }
        Spacer(Modifier.height(8.dp))
        ThinProgress(value, Modifier.fillMaxWidth())
    }
}

@Composable
private fun InfoBlock(title: String, text: String) {
    Spacer(Modifier.height(20.dp))
    Text(title.uppercase(), style = MaterialTheme.typography.labelMedium, color = BloodRed)
    HorizontalDivider(Modifier.padding(vertical = 8.dp), color = Border)
    Text(text, style = MaterialTheme.typography.bodyMedium, color = TextMain)
}

@Preview(showSystemUi = true, showBackground = true, backgroundColor = 0xFF0A0A0A)
@Composable
fun BestiaryScreenPreview() {
    Identity_DIssolution_AppMovilTheme {
        BestiaryContent(
            state = BestiaryUiState(monsters = GameRepository.enemies),
            onBack = {}, onMonsterClick = {}, onRetry = {},
        )
    }
}

@Preview(showSystemUi = true, showBackground = true, backgroundColor = 0xFF0A0A0A)
@Composable
fun MonsterDetailPreview() {
    Identity_DIssolution_AppMovilTheme {
        MonsterDetailContent(GameRepository.enemies.first(), onBack = {}, onSummon = {})
    }
}
