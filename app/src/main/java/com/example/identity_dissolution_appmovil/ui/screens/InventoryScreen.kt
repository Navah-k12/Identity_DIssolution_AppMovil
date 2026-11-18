package com.example.identity_dissolution_appmovil.ui.screens

import androidx.compose.ui.tooling.preview.Preview
import com.example.identity_dissolution_appmovil.ui.theme.Identity_DIssolution_AppMovilTheme
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.identity_dissolution_appmovil.model.InventoryItem
import com.example.identity_dissolution_appmovil.ui.components.HorrorCard
import com.example.identity_dissolution_appmovil.ui.components.HorrorTopBar
import com.example.identity_dissolution_appmovil.ui.theme.BloodRed
import com.example.identity_dissolution_appmovil.ui.theme.Border
import com.example.identity_dissolution_appmovil.ui.theme.DarkRed
import com.example.identity_dissolution_appmovil.ui.theme.HorrorFont
import com.example.identity_dissolution_appmovil.ui.theme.Surface
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
    Column(Modifier.fillMaxSize()) {
        HorrorTopBar("Inventario crítico", actions = {
            Text(
                "${state.items.size}/${state.totalSlots} SLOTS",
                style = MaterialTheme.typography.labelSmall, color = TextMuted,
                modifier = Modifier.padding(end = 12.dp),
            )
        })

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(state.totalSlots) { index ->
                // Los huecos sin objeto se pintan como slots vacíos.
                val item = state.items.getOrNull(index)
                Slot(item, selected = item != null && item == state.selected) {
                    onSelect(item)
                }
            }
        }

        state.selected?.let { item ->
            HorrorCard(Modifier.fillMaxWidth().padding(16.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        "${item.name.uppercase()}  x${item.quantity}",
                        color = BloodRed, fontFamily = HorrorFont, fontWeight = FontWeight.Bold,
                    )
                    Text(item.description, style = MaterialTheme.typography.bodyMedium, color = TextMuted)
                }
            }
        }
    }
}

@Composable
private fun Slot(item: InventoryItem?, selected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(12.dp)
    Box(
        Modifier
            .aspectRatio(1f)
            .background(Surface.copy(alpha = if (item == null) 0.5f else 1f), shape)
            .border(BorderStroke(if (selected) 2.dp else 1.dp, if (selected) BloodRed else Border), shape)
            .clickable(enabled = item != null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (item != null) {
            if (item.image != null) {
                Image(
                    painter = painterResource(item.image),
                    contentDescription = item.name,
                    modifier = Modifier.fillMaxSize().padding(10.dp),
                )
            } else {
                Text(item.emoji, fontSize = 36.sp)
            }
            Text(
                "x${item.quantity}",
                fontSize = 11.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(6.dp)
                    .background(DarkRed, RoundedCornerShape(4.dp))
                    .padding(horizontal = 5.dp, vertical = 1.dp),
            )
        } else {
            Text("+", color = Border, fontSize = 20.sp)
        }
    }
}

@Preview(showSystemUi = true, showBackground = true, backgroundColor = 0xFF0B0A0A)
@Composable
fun InventoryScreenPreview() {
    Identity_DIssolution_AppMovilTheme { InventoryContent(state = InventoryUiState(), onSelect = {}) }
}
