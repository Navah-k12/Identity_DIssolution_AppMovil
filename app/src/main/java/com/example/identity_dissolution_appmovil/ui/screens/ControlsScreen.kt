package com.example.identity_dissolution_appmovil.ui.screens

import androidx.compose.ui.tooling.preview.Preview
import com.example.identity_dissolution_appmovil.ui.theme.Identity_DIssolution_AppMovilTheme
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.identity_dissolution_appmovil.data.repository.GameRepository
import com.example.identity_dissolution_appmovil.model.ControlBinding
import com.example.identity_dissolution_appmovil.ui.components.HorrorCard
import com.example.identity_dissolution_appmovil.ui.components.HorrorTopBar
import com.example.identity_dissolution_appmovil.ui.components.SectionLabel
import com.example.identity_dissolution_appmovil.ui.theme.Border
import com.example.identity_dissolution_appmovil.ui.theme.DarkRed
import com.example.identity_dissolution_appmovil.ui.theme.Surface as SurfaceColor
import com.example.identity_dissolution_appmovil.ui.theme.SurfaceHigh
import com.example.identity_dissolution_appmovil.ui.theme.TextMain
import com.example.identity_dissolution_appmovil.ui.theme.TextMuted

@Composable
fun ControlsScreen(onBack: () -> Unit) {
    // 0 = teclado, 1 = mando. rememberSaveable lo conserva al girar la pantalla.
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val sections = if (selectedTab == 0) GameRepository.keyboardControls else GameRepository.gamepadControls

    Column(Modifier.fillMaxSize()) {
        HorrorTopBar("Controles", onBack = onBack)

        Row(
            Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .background(SurfaceColor, RoundedCornerShape(10.dp))
                .padding(4.dp),
        ) {
            listOf("Teclado", "Mando").forEachIndexed { index, label ->
                val selected = index == selectedTab
                Box(
                    Modifier
                        .weight(1f)
                        .height(40.dp)
                        .background(if (selected) DarkRed else SurfaceColor, RoundedCornerShape(8.dp))
                        .clickable { selectedTab = index },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        label.uppercase(), fontWeight = FontWeight.Bold,
                        color = if (selected) TextMain else TextMuted,
                    )
                }
            }
        }

        Column(Modifier.verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)) {
            sections.forEach { section ->
                SectionLabel(section.title)
                HorrorCard(Modifier.fillMaxWidth()) {
                    section.bindings.forEachIndexed { i, binding ->
                        if (i > 0) HorizontalDivider(color = Border, modifier = Modifier.padding(horizontal = 12.dp))
                        BindingRow(binding)
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun BindingRow(binding: ControlBinding) {
    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
        // Cada tecla se dibuja como una "tecla física".
        Row {
            binding.key.split(" ").forEach { key ->
                Surface(
                    color = SurfaceHigh,
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, DarkRed),
                    modifier = Modifier.padding(end = 6.dp),
                ) {
                    Text(
                        key, fontWeight = FontWeight.Bold,
                        modifier = Modifier.widthIn(min = 28.dp).padding(horizontal = 10.dp, vertical = 8.dp),
                    )
                }
            }
        }
        Spacer(Modifier.width(10.dp))
        Text(binding.action, style = MaterialTheme.typography.bodyMedium)
    }
}

@Preview(showSystemUi = true, showBackground = true, backgroundColor = 0xFF0B0A0A)
@Composable
fun ControlsScreenPreview() {
    Identity_DIssolution_AppMovilTheme { ControlsScreen(onBack = {}) }
}
