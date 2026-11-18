package com.example.identity_dissolution_appmovil.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SentimentVeryDissatisfied
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.identity_dissolution_appmovil.data.repository.GameRepository
import com.example.identity_dissolution_appmovil.model.Enemy
import com.example.identity_dissolution_appmovil.ui.components.HorrorCard
import com.example.identity_dissolution_appmovil.ui.components.HorrorTopBar
import com.example.identity_dissolution_appmovil.ui.theme.Background
import com.example.identity_dissolution_appmovil.ui.theme.BloodRed
import com.example.identity_dissolution_appmovil.ui.theme.Border
import com.example.identity_dissolution_appmovil.ui.theme.DarkRed
import com.example.identity_dissolution_appmovil.ui.theme.HorrorFont
import com.example.identity_dissolution_appmovil.ui.theme.Identity_DIssolution_AppMovilTheme
import com.example.identity_dissolution_appmovil.ui.theme.SurfaceHigh
import com.example.identity_dissolution_appmovil.ui.theme.TextMuted
import com.example.identity_dissolution_appmovil.viewmodel.BestiaryUiState
import com.example.identity_dissolution_appmovil.viewmodel.BestiaryViewModel

@Composable
fun BestiaryScreen(
    focusId: Int = -1,
    onBack: () -> Unit,
    onView3D: (Int) -> Unit,
    viewModel: BestiaryViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsState()
    val listState = rememberLazyListState()

    // Al venir de "Continuar lectura", baja hasta ese monstruo (una sola vez).
    var focused by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(state.monsters, focusId) {
        val index = state.monsters.indexOfFirst { it.id == focusId }
        if (!focused && index >= 0) {
            listState.scrollToItem(index + if (state.error != null) 1 else 0)
            focused = true
        }
    }
    // Guarda el monstruo que está arriba en pantalla como "última lectura".
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.firstOrNull { it.key is Int }?.key as Int? }
            .filterNotNull().distinctUntilChanged()
            .collect { viewModel.markRead(it) }
    }
    BestiaryContent(state, onBack, onView3D, onRetry = viewModel::refresh, listState = listState)
}

/** Diseño sin ViewModel: así se puede ver en @Preview. */
@Composable
fun BestiaryContent(
    state: BestiaryUiState,
    onBack: () -> Unit,
    onView3D: (Int) -> Unit,
    onRetry: () -> Unit,
    listState: LazyListState = rememberLazyListState(),
) {
    Column(Modifier.fillMaxSize()) {
        HorrorTopBar("Bestiario de pesadilla", onBack = onBack, actions = {
            IconButton(onClick = onRetry, enabled = !state.isLoading) {
                Icon(Icons.Filled.Refresh, "Actualizar", tint = BloodRed)
            }
        })

        if (state.isLoading) {
            LinearProgressIndicator(Modifier.fillMaxWidth().height(2.dp), color = BloodRed, trackColor = Background)
        }

        when {
            // Primera carga sin copia local.
            state.monsters.isEmpty() && state.isLoading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                CircularProgressIndicator(color = BloodRed)
            }
            // Sin datos y sin conexión.
            state.monsters.isEmpty() -> ErrorBox(state.error ?: "No hay registros.", onRetry)
            else -> LazyColumn(
                state = listState,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                if (state.error != null) {
                    item {
                        Text(
                            "Sin conexión: mostrando la copia guardada.",
                            color = TextMuted, style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
                items(state.monsters, key = { it.id }) { EnemyCard(it, onView3D) }
                item { CorruptedFooter() }
            }
        }
    }
}

@Composable
private fun EnemyCard(enemy: Enemy, onView3D: (Int) -> Unit) {
    HorrorCard(Modifier.fillMaxWidth()) {
        // 3:2 es el formato de las imágenes cinematográficas de la API.
        Box(Modifier.fillMaxWidth().aspectRatio(3f / 2f).background(SurfaceHigh)) {
            if (enemy.imageUrl != null) {
                AsyncImage(
                    model = enemy.imageUrl,
                    contentDescription = enemy.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                Icon(
                    Icons.Filled.SentimentVeryDissatisfied, null,
                    tint = BloodRed.copy(alpha = 0.25f),
                    modifier = Modifier.size(120.dp).align(Alignment.Center),
                )
            }
            // Degradado para que el nombre se lea sobre la imagen.
            Box(
                Modifier.fillMaxSize().background(
                    Brush.verticalGradient(listOf(Color.Transparent, Color.Transparent, Background))
                )
            )
            Column(Modifier.align(Alignment.BottomStart).padding(16.dp)) {
                Text(
                    enemy.name.uppercase(), color = BloodRed, fontFamily = HorrorFont,
                    fontWeight = FontWeight.Black, style = MaterialTheme.typography.headlineSmall,
                )
                Text(
                    "CLASE: ${enemy.enemyClass.uppercase()}  •  PELIGRO ${enemy.dangerLevel}",
                    color = DarkRed, style = MaterialTheme.typography.labelSmall,
                )
            }
        }

        Column(Modifier.padding(16.dp)) {
            Row {
                StatBar("Peligro", enemy.danger, Modifier.weight(1f))
                Spacer(Modifier.size(16.dp))
                StatBar("Velocidad", enemy.speed, Modifier.weight(1f))
            }
            InfoBlock("Comportamiento", enemy.behavior)
            InfoBlock("Estrategia", enemy.strategy)

            Spacer(Modifier.height(16.dp))
            Button(
                onClick = { onView3D(enemy.id) },
                enabled = enemy.modelUrl != null,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DarkRed,
                    contentColor = Color.White,
                    disabledContainerColor = SurfaceHigh,
                    disabledContentColor = TextMuted,
                ),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(Icons.Filled.ViewInAr, null)
                Spacer(Modifier.size(8.dp))
                Text(
                    if (enemy.modelUrl != null) "VER MODELO 3D" else "MODELO 3D PRÓXIMAMENTE",
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun ErrorBox(message: String, onRetry: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            "ARCHIVO INACCESIBLE", color = BloodRed, fontFamily = HorrorFont, fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(8.dp))
        Text(message, color = TextMuted, textAlign = TextAlign.Center, style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = DarkRed, contentColor = Color.White),
        ) { Text("REINTENTAR") }
    }
}

@Composable
private fun CorruptedFooter() {
    Box(
        Modifier
            .fillMaxWidth()
            .border(BorderStroke(1.dp, Border), RoundedCornerShape(14.dp))
            .padding(24.dp),
    ) {
        Text(
            "ARCHIVO CORRUPTO... ENCUENTRA MÁS INFORMACIÓN EN EL JUEGO.",
            color = TextMuted, fontFamily = HorrorFont, fontStyle = FontStyle.Italic,
            textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun StatBar(label: String, value: Float, modifier: Modifier) {
    Column(modifier) {
        Text(label.uppercase(), style = MaterialTheme.typography.labelSmall, color = TextMuted)
        Spacer(Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { value },
            color = DarkRed,
            trackColor = SurfaceHigh,
            strokeCap = StrokeCap.Round,
            modifier = Modifier.fillMaxWidth().height(6.dp),
        )
    }
}

@Composable
private fun InfoBlock(title: String, text: String) {
    Spacer(Modifier.height(16.dp))
    Text(title.uppercase(), style = MaterialTheme.typography.labelSmall, color = BloodRed)
    HorizontalDivider(Modifier.padding(vertical = 6.dp), color = Border)
    Text(text, style = MaterialTheme.typography.bodySmall, fontStyle = FontStyle.Italic)
}

@Preview(showSystemUi = true, showBackground = true, backgroundColor = 0xFF0B0A0A)
@Composable
fun BestiaryScreenPreview() {
    Identity_DIssolution_AppMovilTheme {
        BestiaryContent(
            state = BestiaryUiState(monsters = GameRepository.enemies),
            onBack = {}, onView3D = {}, onRetry = {},
        )
    }
}
