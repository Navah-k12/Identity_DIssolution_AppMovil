package com.example.identity_dissolution_appmovil.ui.screens

import androidx.compose.ui.tooling.preview.Preview
import com.example.identity_dissolution_appmovil.ui.theme.Identity_DIssolution_AppMovilTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.identity_dissolution_appmovil.ui.theme.DarkRed
import com.example.identity_dissolution_appmovil.viewmodel.ContinueReading
import com.example.identity_dissolution_appmovil.viewmodel.HomeViewModel
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.SentimentVeryDissatisfied
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.identity_dissolution_appmovil.data.repository.GameRepository
import com.example.identity_dissolution_appmovil.ui.components.HorrorCard
import com.example.identity_dissolution_appmovil.ui.components.HorrorTopBar
import com.example.identity_dissolution_appmovil.ui.navigation.Routes
import com.example.identity_dissolution_appmovil.ui.theme.BloodRed
import com.example.identity_dissolution_appmovil.ui.theme.HorrorFont
import com.example.identity_dissolution_appmovil.ui.theme.SurfaceHigh
import com.example.identity_dissolution_appmovil.ui.theme.TextMuted

private data class QuickAccess(val title: String, val subtitle: String, val icon: ImageVector, val route: String)

private val quickAccess = listOf(
    QuickAccess("Enciclopedia", "Mecánicas de juego", Icons.AutoMirrored.Filled.MenuBook, Routes.ENCYCLOPEDIA),
    QuickAccess("Bestiario", "Amenazas registradas", Icons.Filled.SentimentVeryDissatisfied, Routes.BESTIARY),
    QuickAccess("Controles", "Configuración y guía", Icons.Filled.Keyboard, Routes.CONTROLS),
    QuickAccess("Historia", "Documentos y lore", Icons.Filled.History, Routes.HISTORY),
)

@Composable
fun HomeScreen(onNavigate: (String) -> Unit, viewModel: HomeViewModel = viewModel()) {
    val reading by viewModel.continueReading.collectAsState()
    HomeContent(reading, onNavigate)
}

/** Diseño sin ViewModel: así se puede ver en @Preview. */
@Composable
fun HomeContent(reading: ContinueReading?, onNavigate: (String) -> Unit) {
    val quote = remember { GameRepository.quotes.random() }

    Column(Modifier.fillMaxSize()) {
        HorrorTopBar(title = "Misión actual: sobrevivir", actions = {
            Icon(Icons.Filled.Favorite, null, tint = BloodRed, modifier = Modifier.padding(end = 12.dp))
        })

        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        ) {
            Label("Continuar lectura")
            if (reading != null) ContinueCard(reading) { onNavigate(reading.route) }

            Spacer(Modifier.height(24.dp))
            Label("Accesos rápidos")

            // Rejilla 2x2 hecha con filas: así no anidamos un LazyGrid dentro de un scroll.
            quickAccess.chunked(2).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    row.forEach { item ->
                        HorrorCard(Modifier.weight(1f).height(120.dp), onClick = { onNavigate(item.route) }) {
                            Column(Modifier.padding(14.dp)) {
                                IconBox(item.icon, size = 36)
                                Spacer(Modifier.height(12.dp))
                                Text(item.title, style = MaterialTheme.typography.titleMedium)
                                Text(item.subtitle, style = MaterialTheme.typography.bodySmall, color = TextMuted)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            Spacer(Modifier.height(24.dp))
            Text(
                quote,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontFamily = HorrorFont,
                fontStyle = FontStyle.Italic,
                color = TextMuted,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun Label(text: String) {
    Text(
        text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = TextMuted,
        modifier = Modifier.padding(bottom = 10.dp),
    )
}

@Composable
private fun IconBox(icon: ImageVector, size: Int) {
    Box(
        Modifier.size(size.dp).background(SurfaceHigh, RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, null, tint = BloodRed, modifier = Modifier.size((size / 2).dp))
    }
}

@Preview(showSystemUi = true, showBackground = true, backgroundColor = 0xFF0B0A0A)
@Composable
fun HomeScreenPreview() {
    Identity_DIssolution_AppMovilTheme {
        HomeContent(
            reading = ContinueReading("El Mudo", "Bestiario • Nivel de peligro: A", route = Routes.BESTIARY),
            onNavigate = {},
        )
    }
}

/** Tarjeta con el retrato de lo último que se leyó. */
@Composable
private fun ContinueCard(item: ContinueReading, onClick: () -> Unit) {
    HorrorCard(Modifier.fillMaxWidth(), onClick = onClick) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceHigh)
                    .border(1.dp, DarkRed, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center,
            ) {
                // Acercamos la imagen hacia la cara para que se vea como un retrato.
                val zoom = if (item.imageUrl != null) 3.2f else 1f
                val face = Modifier.fillMaxSize().graphicsLayer {
                    scaleX = zoom; scaleY = zoom
                    // Desplaza la imagen para que la cara (faceY) quede en el centro del recuadro.
                    translationY = (0.5f - item.faceY) * size.height * zoom
                }
                when {
                    item.imageUrl != null -> AsyncImage(
                        model = item.imageUrl, contentDescription = item.title,
                        contentScale = ContentScale.Crop, modifier = face,
                    )
                    item.imageRes != null -> Image(
                        painterResource(item.imageRes), item.title,
                        contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize(),
                    )
                    else -> Icon(Icons.Filled.SentimentVeryDissatisfied, null, tint = BloodRed)
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(item.title, style = MaterialTheme.typography.titleMedium)
                Text(item.subtitle, style = MaterialTheme.typography.bodySmall, color = TextMuted)
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = BloodRed)
        }
    }
}
