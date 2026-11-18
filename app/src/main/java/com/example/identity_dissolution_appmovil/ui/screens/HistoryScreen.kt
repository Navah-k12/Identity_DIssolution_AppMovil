package com.example.identity_dissolution_appmovil.ui.screens

import androidx.compose.ui.tooling.preview.Preview
import com.example.identity_dissolution_appmovil.ui.theme.Identity_DIssolution_AppMovilTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.identity_dissolution_appmovil.data.repository.GameRepository
import com.example.identity_dissolution_appmovil.ui.components.HorrorCard
import com.example.identity_dissolution_appmovil.ui.components.HorrorTopBar
import com.example.identity_dissolution_appmovil.ui.theme.BloodRed
import com.example.identity_dissolution_appmovil.ui.theme.Border
import com.example.identity_dissolution_appmovil.ui.theme.HorrorFont
import com.example.identity_dissolution_appmovil.ui.theme.TextMuted

/** Cronología de la historia: una línea vertical con un punto por evento. */
@Composable
fun HistoryScreen(onBack: () -> Unit) {
    val events = GameRepository.story
    Column(Modifier.fillMaxSize()) {
        HorrorTopBar("Cronología", onBack = onBack)
        LazyColumn(contentPadding = PaddingValues(16.dp)) {
            itemsIndexed(events) { index, event ->
                Row {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(Modifier.size(14.dp).background(BloodRed, CircleShape))
                        if (index < events.lastIndex) {
                            Box(Modifier.width(2.dp).height(110.dp).background(Border))
                        }
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.padding(bottom = 16.dp)) {
                        Text(event.year, color = BloodRed, fontFamily = HorrorFont, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(6.dp))
                        HorrorCard {
                            Column(Modifier.padding(14.dp)) {
                                Text(event.title, style = MaterialTheme.typography.titleMedium)
                                Text(event.description, style = MaterialTheme.typography.bodySmall, color = TextMuted)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true, backgroundColor = 0xFF0B0A0A)
@Composable
fun HistoryScreenPreview() {
    Identity_DIssolution_AppMovilTheme { HistoryScreen(onBack = {}) }
}
