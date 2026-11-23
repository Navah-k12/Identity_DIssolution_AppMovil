package com.example.identity_dissolution_appmovil.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.identity_dissolution_appmovil.data.repository.Review
import com.example.identity_dissolution_appmovil.ui.components.HorrorCard
import com.example.identity_dissolution_appmovil.ui.components.HorrorTopBar
import com.example.identity_dissolution_appmovil.ui.components.t
import com.example.identity_dissolution_appmovil.ui.theme.BloodRed
import com.example.identity_dissolution_appmovil.ui.theme.Border
import com.example.identity_dissolution_appmovil.ui.theme.DarkRed
import com.example.identity_dissolution_appmovil.ui.theme.HorrorFont
import com.example.identity_dissolution_appmovil.ui.theme.SurfaceHigh
import com.example.identity_dissolution_appmovil.ui.theme.TextMain
import com.example.identity_dissolution_appmovil.ui.theme.TextMuted
import com.example.identity_dissolution_appmovil.viewmodel.ReviewsUiState
import com.example.identity_dissolution_appmovil.viewmodel.ReviewsViewModel

/**
 * Comentarios de la app con calificación de 1 a 5 estrellas.
 * CRUD contra la API REST de Firestore: GET (lista y filtro), POST (crear), PATCH (editar), DELETE (borrar).
 * Extra: estadísticas (promedio y distribución), filtro por estrellas y búsqueda.
 */
@Composable
fun ReviewsScreen(onBack: () -> Unit, onLogin: () -> Unit = {}, viewModel: ReviewsViewModel = viewModel()) {
    val s by viewModel.state.collectAsState()
    // null = cerrado; Review vacío (id "") = nuevo; Review existente = editar.
    var editing by remember { mutableStateOf<Review?>(null) }
    var deleting by remember { mutableStateOf<Review?>(null) }
    var askLogin by remember { mutableStateOf(false) }

    // Al volver de Ajustes (por ejemplo, después de iniciar sesión) se recarga la sesión y la lista.
    val lifecycle = androidx.lifecycle.compose.LocalLifecycleOwner.current.lifecycle
    androidx.compose.runtime.DisposableEffect(lifecycle) {
        val obs = androidx.lifecycle.LifecycleEventObserver { _, e -> if (e == androidx.lifecycle.Lifecycle.Event.ON_RESUME) viewModel.refresh() }
        lifecycle.addObserver(obs)
        onDispose { lifecycle.removeObserver(obs) }
    }

    Column(Modifier.fillMaxSize()) {
        HorrorTopBar(t("Comentarios", "Reviews"), onBack = onBack)

        if (!s.available) {
            Text(
                t("Firebase no está configurado.", "Firebase is not configured."),
                color = TextMuted, modifier = Modifier.padding(24.dp),
            )
            return@Column
        }

        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item { Summary(s) }
            item {
                // El botón siempre se ve. Sin sesión, avisa y lleva a Ajustes para iniciarla.
                Button(
                    onClick = { if (s.myUid != null) editing = Review("", "", "", 5, "", "") else askLogin = true },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkRed, contentColor = Color.White),
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                ) { Text(t("✎  ESCRIBIR COMENTARIO", "✎  WRITE A REVIEW"), fontWeight = FontWeight.Bold) }
                if (s.myUid == null) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        t("Necesitas iniciar sesión para comentar.", "You need to sign in to write a review."),
                        color = TextMuted, style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
            item { Filters(s, viewModel::setStarFilter, viewModel::setQuery) }
            s.error?.let { err ->
                item { Text(err, color = BloodRed, style = MaterialTheme.typography.bodySmall, modifier = Modifier.clickable { viewModel.clearError() }) }
            }
            when {
                s.loading -> item {
                    Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = BloodRed) }
                }
                s.shown.isEmpty() -> item {
                    Text(t("Sin comentarios todavía.", "No reviews yet."), color = TextMuted, modifier = Modifier.padding(8.dp))
                }
                else -> items(s.shown, key = { it.id }) { r ->
                    ReviewCard(r, mine = r.uid == s.myUid, onEdit = { editing = r }, onDelete = { deleting = r })
                }
            }
        }
    }

    editing?.let { r ->
        ReviewDialog(
            initial = r, saving = s.saving,
            onDismiss = { editing = null },
            onSave = { stars, text -> viewModel.save(r.id.ifEmpty { null }, stars, text) { editing = null } },
        )
    }
    if (askLogin) {
        AlertDialog(
            onDismissRequest = { askLogin = false },
            title = { Text(t("Inicia sesión", "Sign in")) },
            text = { Text(t("Para comentar y calificar, inicia sesión con tu correo o con Google.", "To review and rate, sign in with your email or Google.")) },
            confirmButton = { TextButton(onClick = { askLogin = false; onLogin() }) { Text(t("IR A INICIAR SESIÓN", "GO TO SIGN IN"), color = BloodRed, fontWeight = FontWeight.Bold) } },
            dismissButton = { TextButton(onClick = { askLogin = false }) { Text(t("CANCELAR", "CANCEL"), color = TextMuted) } },
        )
    }
    deleting?.let { r ->
        AlertDialog(
            onDismissRequest = { deleting = null },
            title = { Text(t("¿Borrar comentario?", "Delete review?")) },
            text = { Text(t("Esta acción no se puede deshacer.", "This can't be undone.")) },
            confirmButton = { TextButton(onClick = { viewModel.delete(r.id); deleting = null }) { Text(t("BORRAR", "DELETE"), color = BloodRed) } },
            dismissButton = { TextButton(onClick = { deleting = null }) { Text(t("CANCELAR", "CANCEL"), color = TextMuted) } },
        )
    }
}

/** Estadísticas: promedio grande, estrellas, total y barras por calificación. */
@Composable
private fun Summary(s: ReviewsUiState) {
    HorrorCard(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("%.1f".format(s.average), color = TextMain, fontFamily = HorrorFont, fontWeight = FontWeight.Black, fontSize = 40.sp)
                Stars(Math.round(s.average), size = 16)
                Spacer(Modifier.height(4.dp))
                Text(t("${s.all.size} comentarios", "${s.all.size} reviews"), color = TextMuted, fontSize = 11.sp)
            }
            Spacer(Modifier.width(20.dp))
            Column(Modifier.weight(1f)) {
                val max = (s.distribution.maxOrNull() ?: 0).coerceAtLeast(1)
                for (star in 5 downTo 1) {
                    val n = s.distribution[star - 1]
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                        Text("$star", color = TextMuted, fontSize = 11.sp, modifier = Modifier.width(12.dp))
                        Box(Modifier.weight(1f).height(6.dp).background(SurfaceHigh, RoundedCornerShape(3.dp))) {
                            Box(Modifier.fillMaxWidth(n / max.toFloat()).height(6.dp).background(BloodRed, RoundedCornerShape(3.dp)))
                        }
                        Text("$n", color = TextMuted, fontSize = 11.sp, modifier = Modifier.padding(start = 6.dp).width(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun Filters(s: ReviewsUiState, onStars: (Int?) -> Unit, onQuery: (String) -> Unit) {
    Column {
        OutlinedTextField(
            s.query, onQuery, singleLine = true,
            placeholder = { Text(t("Buscar en comentarios…", "Search reviews…")) },
            leadingIcon = { Icon(Icons.Outlined.Search, null, tint = TextMuted) },
            colors = fieldColors(), modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(8.dp))
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Chip(t("Todas", "All"), s.starFilter == null) { onStars(null) }
            for (star in 5 downTo 1) Chip("$star ★", s.starFilter == star) { onStars(star) }
        }
    }
}

@Composable
private fun Chip(label: String, selected: Boolean, onClick: () -> Unit) {
    Text(
        label,
        color = if (selected) Color.White else TextMuted, fontWeight = FontWeight.Bold, fontSize = 12.sp,
        modifier = Modifier
            .background(if (selected) DarkRed else Color.Transparent, RoundedCornerShape(16.dp))
            .border(1.dp, if (selected) DarkRed else Border, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 7.dp),
    )
}

@Composable
private fun ReviewCard(r: Review, mine: Boolean, onEdit: () -> Unit, onDelete: () -> Unit) {
    HorrorCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(r.author + if (mine) t(" (tú)", " (you)") else "", color = TextMain, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Stars(r.stars, size = 14)
                        Text(
                            "  " + r.createdAt.take(10) + if (r.edited) t(" · editado", " · edited") else "",
                            color = TextMuted, fontSize = 11.sp,
                        )
                    }
                }
                if (mine) {
                    IconButton(onClick = onEdit) { Icon(Icons.Outlined.Edit, t("Editar", "Edit"), tint = TextMuted) }
                    IconButton(onClick = onDelete) { Icon(Icons.Outlined.Delete, t("Borrar", "Delete"), tint = BloodRed) }
                }
            }
            Spacer(Modifier.height(6.dp))
            Text(r.text, color = TextMain, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun Stars(value: Int, size: Int, onSelect: ((Int) -> Unit)? = null) {
    Row {
        for (i in 1..5) {
            Icon(
                if (i <= value) Icons.Filled.Star else Icons.Outlined.StarOutline,
                contentDescription = if (onSelect != null) "$i" else null,
                tint = if (i <= value) BloodRed else TextMuted,
                modifier = Modifier.size(size.dp).then(if (onSelect != null) Modifier.clickable { onSelect(i) } else Modifier),
            )
        }
    }
}

/** Crear (POST) o editar (PATCH): selector de estrellas + texto. */
@Composable
private fun ReviewDialog(initial: Review, saving: Boolean, onDismiss: () -> Unit, onSave: (Int, String) -> Unit) {
    var stars by remember { mutableIntStateOf(initial.stars) }
    var text by remember { mutableStateOf(initial.text) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial.id.isEmpty()) t("Nuevo comentario", "New review") else t("Editar comentario", "Edit review")) },
        text = {
            Column {
                Stars(stars, size = 36) { stars = it }
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    text, { if (it.length <= 500) text = it },
                    placeholder = { Text(t("¿Qué te pareció la app?", "What did you think of the app?")) },
                    minLines = 3, colors = fieldColors(), modifier = Modifier.fillMaxWidth(),
                )
                Text("${text.length}/500", color = TextMuted, fontSize = 11.sp, modifier = Modifier.align(Alignment.End))
            }
        },
        confirmButton = {
            if (saving) CircularProgressIndicator(color = BloodRed, modifier = Modifier.size(24.dp))
            else TextButton(onClick = { onSave(stars, text) }) { Text(t("PUBLICAR", "POST"), color = BloodRed, fontWeight = FontWeight.Bold) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(t("CANCELAR", "CANCEL"), color = TextMuted) } },
    )
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = BloodRed, unfocusedBorderColor = Border, cursorColor = BloodRed,
    focusedTextColor = TextMain, unfocusedTextColor = TextMain,
)
