package com.example.identity_dissolution_appmovil.ui.screens

import androidx.compose.ui.tooling.preview.Preview
import com.example.identity_dissolution_appmovil.ui.theme.Identity_DIssolution_AppMovilTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.example.identity_dissolution_appmovil.data.repository.LastRead
import com.example.identity_dissolution_appmovil.data.repository.ReadingRepository
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.identity_dissolution_appmovil.data.repository.GameRepository
import com.example.identity_dissolution_appmovil.model.Article
import com.example.identity_dissolution_appmovil.ui.components.HorrorCard
import com.example.identity_dissolution_appmovil.ui.components.HorrorTopBar
import com.example.identity_dissolution_appmovil.ui.theme.Background
import com.example.identity_dissolution_appmovil.ui.theme.BloodRed
import com.example.identity_dissolution_appmovil.ui.theme.Border
import com.example.identity_dissolution_appmovil.ui.theme.DarkRed
import com.example.identity_dissolution_appmovil.ui.theme.HorrorFont
import com.example.identity_dissolution_appmovil.ui.theme.TextMuted
import com.example.identity_dissolution_appmovil.viewmodel.EncyclopediaViewModel

@Composable
fun EncyclopediaScreen(
    onBack: () -> Unit,
    onArticleClick: (Int) -> Unit,
    viewModel: EncyclopediaViewModel = viewModel(),
) {
    val query by viewModel.query.collectAsState()
    val results by viewModel.results.collectAsState()
    EncyclopediaContent(query, results, viewModel::onQueryChange, onBack, onArticleClick)
}

/** Diseño sin ViewModel: así se puede ver en @Preview. */
@Composable
fun EncyclopediaContent(
    query: String,
    results: List<Article>,
    onQueryChange: (String) -> Unit,
    onBack: () -> Unit,
    onArticleClick: (Int) -> Unit,
) {
    Column(Modifier.fillMaxSize()) {
        HorrorTopBar("Registros de sangre", onBack = onBack)

        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("Buscar en los archivos...", fontFamily = HorrorFont) },
            leadingIcon = { Icon(Icons.Filled.Search, null, tint = BloodRed) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BloodRed,
                unfocusedBorderColor = DarkRed,
            ),
            modifier = Modifier.fillMaxWidth().padding(16.dp),
        )

        LazyColumn(
            contentPadding = PaddingValues(16.dp, 0.dp, 16.dp, 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (results.isEmpty()) {
                item {
                    Text("Ningún registro coincide... algo los borró.", color = TextMuted, fontFamily = HorrorFont)
                }
            }
            items(results, key = { it.id }) { article ->
                if (article.featured && query.isBlank()) {
                    FeaturedCard(article) { onArticleClick(article.id) }
                } else {
                    ArticleRow(article) { onArticleClick(article.id) }
                }
            }
        }
    }
}

@Composable
private fun FeaturedCard(article: Article, onClick: () -> Unit) {
    HorrorCard(Modifier.fillMaxWidth(), onClick = onClick) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(170.dp)
                .background(DarkRed.copy(alpha = 0.3f)),
            contentAlignment = Alignment.BottomStart,
        ) {
            if (article.image != null) {
                Image(
                    painterResource(article.image), null,
                    contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize(),
                )
            }
            // Degradado para leer el texto sobre la imagen.
            Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Background))))
            Column(Modifier.padding(14.dp)) {
                Text(
                    "DESTACADO: ${article.title.uppercase()}",
                    color = BloodRed, fontFamily = HorrorFont, fontWeight = FontWeight.Bold,
                )
                Text(article.summary, style = MaterialTheme.typography.bodySmall, color = TextMuted)
            }
        }
    }
}

@Composable
private fun ArticleRow(article: Article, onClick: () -> Unit) {
    HorrorCard(Modifier.fillMaxWidth(), onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Barra roja a la izquierda, como en el diseño.
            Box(Modifier.width(4.dp).height(84.dp).background(DarkRed))
            if (article.image != null) {
                Image(
                    painterResource(article.image), null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.padding(start = 10.dp).size(64.dp).clip(RoundedCornerShape(8.dp)),
                )
            }
            Column(Modifier.weight(1f).padding(14.dp)) {
                Text(
                    "${article.category.label.uppercase()}: ${article.title.uppercase()}",
                    color = BloodRed, fontFamily = HorrorFont, fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(Modifier.height(4.dp))
                Text(article.summary, style = MaterialTheme.typography.bodySmall, color = TextMuted)
            }
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight, null,
                tint = BloodRed, modifier = Modifier.padding(end = 8.dp),
            )
        }
    }
}

@Composable
fun ArticleDetailScreen(
    articleId: Int,
    onBack: () -> Unit,
    viewModel: EncyclopediaViewModel = viewModel(),
) {
    // Abrir un artículo lo guarda como "última lectura" para Inicio.
    val context = LocalContext.current
    LaunchedEffect(articleId) { ReadingRepository.get(context).save(LastRead.Guide(articleId)) }
    ArticleDetailContent(viewModel.article(articleId), onBack)
}

@Composable
fun ArticleDetailContent(article: Article?, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize()) {
        HorrorTopBar(article?.category?.label ?: "Archivo", onBack = onBack)
        Column(Modifier.verticalScroll(rememberScrollState()).padding(20.dp)) {
            if (article == null) {
                Text("Archivo corrupto.", color = TextMuted)
                return@Column
            }
            if (article.image != null) {
                Image(
                    painterResource(article.image), article.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f).clip(RoundedCornerShape(12.dp)),
                )
                Spacer(Modifier.height(16.dp))
            }
            Text(
                article.title.uppercase(),
                color = BloodRed, fontFamily = HorrorFont, fontWeight = FontWeight.Black,
                style = MaterialTheme.typography.headlineSmall,
            )
            Spacer(Modifier.height(8.dp))
            Text(article.summary, color = TextMuted, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(16.dp))
            Box(Modifier.fillMaxWidth().height(1.dp).background(Border))
            Spacer(Modifier.height(16.dp))
            Text(article.body, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Preview(showSystemUi = true, showBackground = true, backgroundColor = 0xFF0B0A0A)
@Composable
fun EncyclopediaScreenPreview() {
    Identity_DIssolution_AppMovilTheme { EncyclopediaContent(query = "", results = GameRepository.articles, onQueryChange = {}, onBack = {}, onArticleClick = {}) }
}

@Preview(showSystemUi = true, showBackground = true, backgroundColor = 0xFF0B0A0A)
@Composable
fun ArticleDetailScreenPreview() {
    Identity_DIssolution_AppMovilTheme { ArticleDetailContent(article = GameRepository.articles[1], onBack = {}) }
}
