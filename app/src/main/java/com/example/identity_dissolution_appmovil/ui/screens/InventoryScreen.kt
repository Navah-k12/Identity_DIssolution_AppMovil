package com.example.identity_dissolution_appmovil.ui.screens

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.identity_dissolution_appmovil.data.repository.GameRepository
import com.example.identity_dissolution_appmovil.model.InventoryItem
import com.example.identity_dissolution_appmovil.ui.components.Eyebrow
import com.example.identity_dissolution_appmovil.ui.components.ScreenHeader
import com.example.identity_dissolution_appmovil.ui.components.SegmentedProgress
import com.example.identity_dissolution_appmovil.ui.components.Tag
import com.example.identity_dissolution_appmovil.ui.theme.BloodRed
import com.example.identity_dissolution_appmovil.ui.theme.Border
import com.example.identity_dissolution_appmovil.ui.theme.DarkRed
import com.example.identity_dissolution_appmovil.ui.theme.HorrorFont
import com.example.identity_dissolution_appmovil.ui.theme.Identity_DIssolution_AppMovilTheme
import com.example.identity_dissolution_appmovil.ui.theme.Surface
import com.example.identity_dissolution_appmovil.ui.theme.SurfaceHigh
import com.example.identity_dissolution_appmovil.ui.theme.TextMain
import com.example.identity_dissolution_appmovil.ui.theme.TextMuted
import com.example.identity_dissolution_appmovil.viewmodel.InventoryUiState
import com.example.identity_dissolution_appmovil.viewmodel.InventoryViewModel

@Composable
fun InventoryScreen(viewModel: InventoryViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()
    InventoryContent(state, viewModel::select)
}

/** Diseño sin ViewModel: así se puede ver en @Preview. */
@Composable
fun InventoryContent(state: InventoryUiState, onSelect: (InventoryItem?) -> Unit) {
    val used = state.items.size
    val free = state.totalSlots - used

    Column(Modifier.fillMaxSize()) {
        ScreenHeader("Inventario crítico", trailing = {
            Row(verticalAlignment = Alignment.Bottom) {
                Text("$used", style = MaterialTheme.typography.titleLarge, color = TextMain)
                Text("/${state.totalSlots}", style = MaterialTheme.typography.bodyLarge, color = TextMuted, fontFamily = HorrorFont)
            }
        })
        Column(Modifier.padding(horizontal = 20.dp)) {
            SegmentedProgress(used, state.totalSlots, height = 5, gap = 6)
            Spacer(Modifier.height(8.dp))
            Eyebrow(if (free == 1) "1 espacio libre" else "$free espacios libres")
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f),
        ) {
            items(state.totalSlots) { index ->
                // Los huecos sin objeto se pintan como slots libres.
                val item = state.items.getOrNull(index)
                if (item != null) {
                    Slot(item, selected = item == state.selected) { onSelect(item) }
                } else {
                    EmptySlot()
                }
            }
        }

        state.selected?.let { DetailSheet(it) }
    }
}

@Composable
private fun Slot(item: InventoryItem, selected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(12.dp)
    Box(
        Modifier
            .aspectRatio(0.95f)
            .clip(shape)
            .background(if (selected) SurfaceHigh else Surface)
            .border(BorderStroke(if (selected) 1.5.dp else 1.dp, if (selected) BloodRed else Border), shape)
            .clickable(onClick = onClick),
    ) {
        if (item.image != null) {
            Image(
                painter = painterResource(item.image),
                contentDescription = item.name,
                modifier = Modifier.fillMaxWidth().aspectRatio(1.3f).padding(start = 10.dp, end = 10.dp, top = 14.dp),
            )
        } else {
            Text(item.emoji, fontSize = 32.sp, modifier = Modifier.align(Alignment.Center))
        }
        Text(
            "x${item.quantity}",
            style = MaterialTheme.typography.labelSmall,
            color = TextMain,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(6.dp)
                .background(DarkRed, RoundedCornerShape(4.dp))
                .padding(horizontal = 5.dp, vertical = 1.dp),
        )
        Text(
            item.name,
            style = MaterialTheme.typography.bodySmall,
            color = TextMain,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.align(Alignment.BottomStart).padding(8.dp),
        )
    }
}

/** Hueco libre con borde punteado. */
@Composable
private fun EmptySlot() {
    Box(
        Modifier
            .aspectRatio(0.95f)
            .drawBehind {
                val r = 12.dp.toPx()
                drawRoundRect(
                    color = DarkRed,
                    cornerRadius = CornerRadius(r, r),
                    style = Stroke(width = 1.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f))),
                )
            },
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("+", color = TextMuted, fontSize = 24.sp)
            Text("LIBRE", style = MaterialTheme.typography.labelSmall, color = TextMuted)
        }
    }
}

/** Hoja inferior con el objeto elegido. */
@Composable
private fun DetailSheet(item: InventoryItem) {
    val shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    Column(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Surface)
            .border(BorderStroke(1.dp, Border), shape)
            .padding(20.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(SurfaceHigh)
                    .border(1.dp, DarkRed, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center,
            ) {
                if (item.image != null) {
                    Image(painterResource(item.image), item.name, Modifier.padding(6.dp))
                } else {
                    Text(item.emoji, fontSize = 28.sp)
                }
            }
            Spacer(Modifier.width(14.dp))
            Column {
                Text(item.name, style = MaterialTheme.typography.titleLarge, color = TextMain, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Tag(item.type)
                    Spacer(Modifier.width(8.dp))
                    Text("Cantidad x${item.quantity}", style = MaterialTheme.typography.labelMedium, color = TextMuted)
                }
            }
        }
        Spacer(Modifier.height(14.dp))
        Text(item.description, style = MaterialTheme.typography.bodyMedium, color = TextMain)
    }
}

@Preview(showSystemUi = true, showBackground = true, backgroundColor = 0xFF0A0A0A)
@Composable
fun InventoryScreenPreview() {
    Identity_DIssolution_AppMovilTheme {
        InventoryContent(state = InventoryUiState(selected = GameRepository.inventory[5]), onSelect = {})
    }
}
