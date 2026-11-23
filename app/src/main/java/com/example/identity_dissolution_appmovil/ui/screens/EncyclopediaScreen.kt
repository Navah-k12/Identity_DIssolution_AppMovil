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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.identity_dissolution_appmovil.data.repository.GameRepository
import com.example.identity_dissolution_appmovil.data.repository.LastRead
import com.example.identity_dissolution_appmovil.data.repository.ReadingRepository
import com.example.identity_dissolution_appmovil.model.Article
import com.example.identity_dissolution_appmovil.model.ArticleCategory
import com.example.identity_dissolution_appmovil.ui.components.CircleIconButton
import com.example.identity_dissolution_appmovil.ui.components.Eyebrow
import com.example.identity_dissolution_appmovil.ui.components.HorrorCard
import com.example.identity_dissolution_appmovil.ui.components.HorrorTopBar
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
import com.example.identity_dissolution_appmovil.ui.theme.TextFaint
import com.example.identity_dissolution_appmovil.ui.theme.TextMain
import com.example.identity_dissolution_appmovil.ui.theme.TextMuted
import com.example.identity_dissolution_appmovil.viewmodel.EncyclopediaViewModel
import com.example.identity_dissolution_appmovil.viewmodel.HomeViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

@Composable
fun EncyclopediaScreen(
    onArticleClick: (Int) -> Unit,
    viewModel: EncyclopediaViewModel = viewModel(),
) {
    val query by viewModel.query.collectAsState()
    val category by viewModel.category.collectAsState()
    val results by viewModel.results.collectAsState()

    val context = LocalContext.current
    val reading = remember { ReadingRepository.get(context) }
    val progress by reading.progress.collectAsState(initial = emptyMap())
    val bookmarks by reading.bookmarks.collectAsState(initial = emptySet())
    val scope = rememberCoroutineScope()
    var onlyBookmarks by remember { mutableStateOf(false) }

    EncyclopediaContent(
        query = query,
        category = category,
        results = if (onlyBookmarks) results.filter { it.id in bookmarks } else results,
        progress = progress,
        bookmarks = bookmarks,
        onlyBookmarks = onlyBookmarks,
        onQueryChange = viewModel::onQueryChange,
        onCategoryChange = viewModel::onCategoryChange,
        onToggleOnlyBookmarks = { onlyBookmarks = !onlyBookmarks },
        onToggleBookmark = { id -> scope.launch { reading.toggleBookmark(id) } },
        onArticleClick = onArticleClick,
    )
}

/** Diseño sin ViewModel: así se puede ver en @Preview. */
@Composable
fun EncyclopediaContent(
    query: String,
    category: ArticleCategory?,
    results: List<Article>,
    progress: Map<Int, Float>,
    bookmarks: Set<Int>,
    onlyBookmarks: Boolean,
    onQueryChange: (String) -> Unit,
    onCategoryChange: (ArticleCategory?) -> Unit,
    onToggleOnlyBookmarks: () -> Unit,
    onToggleBookmark: (Int) -> Unit,
    onArticleClick: (Int) -> Unit,
) {
    // El destacado solo aparece en la vista general (sin buscar ni filtrar).
    val featured = results.firstOrNull { it.featured }
        ?.takeIf { query.isBlank() && category == null && !onlyBookmarks }
    val list = results.filter { it != featured }

    Column(Modifier.fillMaxSize()) {
        ScreenHeader(tr("Registros de sangre"), trailing = {
            CircleIconButton(
                if (onlyBookmarks) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                tr("Ver guardados"), onToggleOnlyBookmarks,
            )
        })

        LazyColumn(
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item { SearchField(query, onQueryChange) }
            item { CategoryChips(category, onCategoryChange) }
            featured?.let { a ->
                item(key = "featured") {
                    FeaturedCard(a, progress[a.id] ?: 0f) { onArticleClick(a.id) }
                }
            }
            item {
                Row(Modifier.padding(top = 8.dp)) {
                    Eyebrow(if (onlyBookmarks) tr("Guardados") else tr("Archivos"), Modifier.weight(1f))
                    Text("${list.size} registros", style = MaterialTheme.typography.labelMedium, color = TextMuted)
                }
            }
            if (list.isEmpty()) {
                item {
                    Text("Ningún registro coincide... algo los borró.", color = TextMuted, fontFamily = HorrorFont)
                }
            }
            items(list, key = { it.id }) { article ->
                ArticleRow(
                    article,
                    progress = progress[article.id],
                    bookmarked = article.id in bookmarks,
                    onBookmark = { onToggleBookmark(article.id) },
                ) { onArticleClick(article.id) }
            }
        }
    }
}

@Composable
private fun SearchField(query: String, onQueryChange: (String) -> Unit) {
    val shape = RoundedCornerShape(12.dp)
    Row(
        Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(Surface, shape)
            .border(1.dp, Border, shape)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(Icons.Outlined.Search, null, tint = BloodRed, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(10.dp))
        Box(Modifier.weight(1f)) {
            if (query.isEmpty()) {
                Text("Buscar en los archivos…", fontFamily = HorrorFont, color = TextFaint, style = MaterialTheme.typography.bodyMedium)
            }
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = TextStyle(color = TextMain, fontFamily = HorrorFont, fontSize = MaterialTheme.typography.bodyMedium.fontSize),
                cursorBrush = SolidColor(BloodRed),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun CategoryChips(selected: ArticleCategory?, onSelect: (ArticleCategory?) -> Unit) {
    val options = listOf<ArticleCategory?>(null) + ArticleCategory.entries
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(options) { cat ->
            val active = cat == selected
            Text(
                cat?.label ?: tr("Todo"),
                fontFamily = HorrorFont,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium,
                color = if (active) OnRed else TextMain,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (active) BloodRed else Surface)
                    .border(1.dp, if (active) BloodRed else Border, CircleShape)
                    .clickable { onSelect(cat) }
                    .padding(horizontal = 18.dp, vertical = 10.dp),
            )
        }
    }
}

@Composable
private fun FeaturedCard(article: Article, progress: Float, onClick: () -> Unit) {
    val shape = RoundedCornerShape(16.dp)
    Box(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Surface)
            .border(BorderStroke(1.dp, DarkRed), shape)
            .clickable(onClick = onClick),
    ) {
        if (article.image != null) {
            Image(
                painterResource(article.image), null,
                contentScale = ContentScale.Crop,
                alpha = 0.35f,
                modifier = Modifier.matchParentSize(),
            )
        }
        Box(Modifier.matchParentSize().background(Brush.verticalGradient(listOf(Color.Transparent, Background.copy(alpha = 0.9f)))))
        Column(Modifier.padding(16.dp)) {
            Spacer(Modifier.height(24.dp))
            Tag(tr("Destacado"), filled = true)
            Spacer(Modifier.height(8.dp))
            Text(article.title.uppercase(), color = BloodRed, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(6.dp))
            Text(article.summary, style = MaterialTheme.typography.bodyMedium, color = TextMain)
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    ThinProgress(progress, Modifier.fillMaxWidth())
                    Spacer(Modifier.height(6.dp))
                    Text("${(progress * 100).toInt()}% LEÍDO", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                }
                Spacer(Modifier.width(12.dp))
                Button(
                    onClick = onClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BloodRed, contentColor = OnRed),
                    modifier = Modifier.height(44.dp),
                ) {
                    Text(if (progress > 0f) tr("CONTINUAR") else tr("LEER"), fontFamily = HorrorFont, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ArticleRow(
    article: Article,
    progress: Float?,
    bookmarked: Boolean,
    onBookmark: () -> Unit,
    onClick: () -> Unit,
) {
    HorrorCard(Modifier.fillMaxWidth(), onClick = onClick) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
            Box(
                Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(DarkRed.copy(alpha = 0.15f))
                    .border(1.dp, Border, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center,
            ) {
                if (article.image != null) {
                    Image(
                        painterResource(article.image), null,
                        contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    Icon(Icons.Outlined.Description, null, tint = DarkRed, modifier = Modifier.size(24.dp))
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Tag(article.category.label)
                    Spacer(Modifier.width(8.dp))
                    ReadStatus(article, progress)
                }
                Spacer(Modifier.height(6.dp))
                Text(article.title, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(article.summary, style = MaterialTheme.typography.bodySmall, color = TextMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(6.dp))
                Text("${article.readMinutes} min de lectura", style = MaterialTheme.typography.labelSmall, color = TextFaint)
            }
            Icon(
                if (bookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                if (bookmarked) tr("Quitar marcador") else tr("Guardar"),
                tint = if (bookmarked) BloodRed else TextMuted,
                modifier = Modifier.clip(CircleShape).clickable(onClick = onBookmark).padding(4.dp).size(22.dp),
            )
        }
    }
}

/** tr("● NUEVO"), "✓ LEÍDO" o el porcentaje leído. */
@Composable
private fun ReadStatus(article: Article, progress: Float?) {
    val style = MaterialTheme.typography.labelSmall
    when {
        progress == null && article.isNew -> Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(6.dp).background(BloodRed, CircleShape))
            Spacer(Modifier.width(4.dp))
            Text("NUEVO", style = style, color = SoftRed)
        }
        progress != null && progress >= HomeViewModel.READ_THRESHOLD -> Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Check, null, tint = TextMuted, modifier = Modifier.size(12.dp))
            Spacer(Modifier.width(4.dp))
            Text("LEÍDO", style = style, color = TextMuted)
        }
        progress != null -> Text("${(progress * 100).toInt()}%", style = style, color = TextMuted)
    }
}

@OptIn(FlowPreview::class)
@Composable
fun ArticleDetailScreen(
    articleId: Int,
    onBack: () -> Unit,
    viewModel: EncyclopediaViewModel = viewModel(),
) {
    val context = LocalContext.current
    val reading = remember { ReadingRepository.get(context) }
    val scroll = rememberScrollState()
    // Abrir un artículo lo guarda como tr("última lectura") para Inicio.
    LaunchedEffect(articleId) { reading.save(LastRead.Guide(articleId)) }
    // Guarda hasta dónde se bajó: eso es el progreso de lectura.
    LaunchedEffect(articleId, scroll) {
        snapshotFlow { if (scroll.maxValue in 1 until Int.MAX_VALUE) scroll.value.toFloat() / scroll.maxValue else if (scroll.maxValue == 0) 1f else 0f }
            .debounce(300)
            .collect { reading.saveProgress(articleId, it) }
    }
    ArticleDetailContent(viewModel.article(articleId), onBack, scroll)
}

@Composable
fun ArticleDetailContent(
    article: Article?,
    onBack: () -> Unit,
    scroll: androidx.compose.foundation.ScrollState = rememberScrollState(),
) {
    Column(Modifier.fillMaxSize()) {
        ScreenHeader(
            article?.category?.label ?: tr("Archivo"),
            leading = { CircleIconButton(Icons.AutoMirrored.Filled.ArrowBack, "Volver", onBack) },
        )
        Column(Modifier.verticalScroll(scroll).padding(horizontal = 20.dp, vertical = 8.dp)) {
            if (article == null) {
                Text("Archivo corrupto.", color = TextMuted)
                return@Column
            }
            if (article.image != null) {
                Image(
                    painterResource(article.image), article.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, Border, RoundedCornerShape(16.dp)),
                )
                Spacer(Modifier.height(16.dp))
            }
            Text(article.title.uppercase(), color = BloodRed, style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(8.dp))
            Text("${article.readMinutes} MIN DE LECTURA", style = MaterialTheme.typography.labelSmall, color = TextFaint)
            Spacer(Modifier.height(8.dp))
            Text(article.summary, color = TextMuted, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(16.dp))
            Box(Modifier.fillMaxWidth().height(1.dp).background(Border))
            Spacer(Modifier.height(16.dp))
            Text(article.body, style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Preview(showSystemUi = true, showBackground = true, backgroundColor = 0xFF0A0A0A)
@Composable
fun EncyclopediaScreenPreview() {
    Identity_DIssolution_AppMovilTheme {
        EncyclopediaContent(
            query = "", category = null, results = GameRepository.articles,
            progress = mapOf(1 to 0.62f, 3 to 1f), bookmarks = setOf(2), onlyBookmarks = false,
            onQueryChange = {}, onCategoryChange = {}, onToggleOnlyBookmarks = {}, onToggleBookmark = {}, onArticleClick = {},
        )
    }
}

@Preview(showSystemUi = true, showBackground = true, backgroundColor = 0xFF0A0A0A)
@Composable
fun ArticleDetailScreenPreview() {
    Identity_DIssolution_AppMovilTheme { ArticleDetailContent(article = GameRepository.articles[1], onBack = {}) }
}
