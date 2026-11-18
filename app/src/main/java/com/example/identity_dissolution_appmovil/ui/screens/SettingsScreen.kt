package com.example.identity_dissolution_appmovil.ui.screens

import androidx.compose.ui.tooling.preview.Preview
import com.example.identity_dissolution_appmovil.ui.theme.Identity_DIssolution_AppMovilTheme
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.identity_dissolution_appmovil.ui.components.HorrorCard
import com.example.identity_dissolution_appmovil.ui.components.HorrorTopBar
import com.example.identity_dissolution_appmovil.ui.components.SectionLabel
import com.example.identity_dissolution_appmovil.ui.theme.BloodRed
import com.example.identity_dissolution_appmovil.ui.theme.Border
import com.example.identity_dissolution_appmovil.ui.theme.DarkRed
import com.example.identity_dissolution_appmovil.ui.theme.SurfaceHigh
import com.example.identity_dissolution_appmovil.ui.theme.TextMain
import com.example.identity_dissolution_appmovil.ui.theme.TextMuted
import com.example.identity_dissolution_appmovil.model.Settings
import com.example.identity_dissolution_appmovil.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = viewModel()) {
    val s by viewModel.state.collectAsState()
    SettingsContent(
        s = s,
        onGlitch = viewModel::setGlitch,
        onRetro = viewModel::setRetro,
        onToggleLanguage = viewModel::toggleLanguage,
        onAutoSync = viewModel::setAutoSync,
        onPulse = viewModel::setPulseNotifications,
        onReset = viewModel::reset,
    )
}

/** Diseño sin ViewModel: así se puede ver en @Preview. */
@Composable
fun SettingsContent(
    s: Settings,
    onGlitch: (Boolean) -> Unit = {},
    onRetro: (Boolean) -> Unit = {},
    onToggleLanguage: () -> Unit = {},
    onAutoSync: (Boolean) -> Unit = {},
    onPulse: (Boolean) -> Unit = {},
    onReset: () -> Unit = {},
) {
    var dialog by remember { mutableStateOf<Pair<String, String>?>(null) }

    Column(Modifier.fillMaxSize()) {
        HorrorTopBar("Configuración del sistema")
        Column(Modifier.verticalScroll(rememberScrollState()).padding(16.dp)) {

            SectionLabel("Visual y ambiente")
            HorrorCard(Modifier.fillMaxWidth()) {
                ToggleRow(
                    "Efecto de glitch", s.glitchEffect, onGlitch,
                    subtitle = "Reduce el movimiento si experimentas mareos o fatiga visual.",
                )
                Divider()
                ToggleRow("Post-procesado retro", s.retroPostProcess, onRetro,
                    subtitle = "Líneas de monitor antiguo sobre toda la app.")
            }

            Spacer(Modifier.height(16.dp))
            SectionLabel("Preferencias")
            HorrorCard(Modifier.fillMaxWidth()) {
                LinkRow("Idioma", value = s.language.uppercase(), onClick = onToggleLanguage)
                Divider()
                ToggleRow("Sincronización automática", s.autoSync, onAutoSync)
                Divider()
                ToggleRow("Notificaciones de pulso", s.pulseNotifications, onPulse)
            }

            Spacer(Modifier.height(16.dp))
            SectionLabel("Información")
            HorrorCard(Modifier.fillMaxWidth()) {
                LinkRow("Acerca de la app") {
                    dialog = "Heartbeat Companion" to "Versión 1.0\nApp complementaria del videojuego de survival horror Heartbeat."
                }
                Divider()
                LinkRow("Licencias de código abierto") {
                    dialog = "Licencias" to "Jetpack Compose, Navigation y Lifecycle: Apache License 2.0."
                }
                Divider()
                LinkRow("Política de privacidad") {
                    dialog = "Privacidad" to "La app no recopila datos personales. Todo se guarda en tu dispositivo."
                }
            }

            Spacer(Modifier.height(24.dp))
            OutlinedButton(
                onClick = { dialog = "Datos borrados" to "Los ajustes volvieron a sus valores iniciales." ; onReset() },
                border = BorderStroke(1.dp, DarkRed),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp),
            ) { Text("BORRAR DATOS RECOPILADOS", color = BloodRed, fontWeight = FontWeight.Bold) }
        }
    }

    dialog?.let { (title, text) ->
        AlertDialog(
            onDismissRequest = { dialog = null },
            confirmButton = { TextButton(onClick = { dialog = null }) { Text("CERRAR", color = BloodRed) } },
            title = { Text(title) },
            text = { Text(text) },
            containerColor = SurfaceHigh,
        )
    }
}

@Composable
private fun Divider() = HorizontalDivider(color = Border, modifier = Modifier.padding(horizontal = 16.dp))

@Composable
private fun ToggleRow(title: String, checked: Boolean, onChange: (Boolean) -> Unit, subtitle: String? = null) {
    Row(
        Modifier.fillMaxWidth().clickable { onChange(!checked) }.padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyMedium)
            if (subtitle != null) Text(subtitle, style = MaterialTheme.typography.bodySmall, color = TextMuted)
        }
        Switch(
            checked = checked,
            onCheckedChange = onChange,
            colors = SwitchDefaults.colors(
                checkedTrackColor = DarkRed,
                checkedThumbColor = TextMain,
                uncheckedTrackColor = SurfaceHigh,
                uncheckedThumbColor = TextMuted,
                uncheckedBorderColor = Border,
            ),
        )
    }
}

@Composable
private fun LinkRow(title: String, value: String? = null, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onClick).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(title, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        if (value != null) Text(value, style = MaterialTheme.typography.labelSmall, color = TextMuted)
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = DarkRed)
    }
}

@Preview(showSystemUi = true, showBackground = true, backgroundColor = 0xFF0B0A0A)
@Composable
fun SettingsScreenPreview() {
    Identity_DIssolution_AppMovilTheme { SettingsContent(s = Settings()) }
}
