package com.example.identity_dissolution_appmovil.ui.screens

import androidx.compose.ui.tooling.preview.Preview
import com.example.identity_dissolution_appmovil.ui.theme.Identity_DIssolution_AppMovilTheme
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.example.identity_dissolution_appmovil.R
import com.example.identity_dissolution_appmovil.ui.components.GlitchText
import com.example.identity_dissolution_appmovil.ui.components.ScreenHeader
import com.example.identity_dissolution_appmovil.ui.components.retroScanlines
import com.example.identity_dissolution_appmovil.ui.theme.Background
import com.example.identity_dissolution_appmovil.ui.theme.HorrorFont
import com.example.identity_dissolution_appmovil.ui.theme.OnRed
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

private val BloodFont = FontFamily(Font(R.font.rubik_wet_paint))

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
        onIntensity = viewModel::setGlitchIntensity,
        onTextSize = viewModel::setTextSize,
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
    onIntensity: (Float) -> Unit = {},
    onTextSize: (Int) -> Unit = {},
    onReset: () -> Unit = {},
) {
    var dialog by remember { mutableStateOf<Pair<String, String>?>(null) }

    Column(Modifier.fillMaxSize()) {
        ScreenHeader("Configuración del sistema")
        Column(Modifier.verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 4.dp)) {

            SectionLabel("Visual y ambiente")
            HorrorCard(Modifier.fillMaxWidth()) {
                // Vista previa en vivo del glitch y del post-procesado.
                Box(
                    Modifier.fillMaxWidth().height(96.dp).background(Background).retroScanlines(s.retroPostProcess),
                    contentAlignment = Alignment.Center,
                ) {
                    GlitchText("SOBREVIVIR", style = TextStyle(fontFamily = BloodFont, fontSize = 30.sp, letterSpacing = 2.sp), color = BloodRed)
                }
                ToggleRow(
                    "Efecto de glitch", s.glitchEffect, onGlitch,
                    subtitle = "Distorsión en títulos y transiciones. Desactívalo si sientes mareo o fatiga visual.",
                )
                Divider()
                ToggleRow("Post-procesado retro", s.retroPostProcess, onRetro,
                    subtitle = "Líneas de monitor antiguo sobre toda la app.")
                Divider()
                IntensityRow(s.glitchIntensity, enabled = s.glitchEffect, onChange = onIntensity)
            }

            Spacer(Modifier.height(16.dp))
            SectionLabel("Accesibilidad")
            HorrorCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Tamaño de texto", style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("A", "A+", "A++").forEachIndexed { i, label ->
                            val active = s.textSize == i
                            Box(
                                Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (active) BloodRed else Background)
                                    .border(1.dp, if (active) BloodRed else Border, RoundedCornerShape(10.dp))
                                    .clickable { onTextSize(i) },
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(label, fontFamily = HorrorFont, fontWeight = FontWeight.Bold, color = if (active) OnRed else TextMain)
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Text("El silencio es el primer síntoma de que ya no estás solo.", style = MaterialTheme.typography.bodyLarge, color = TextMuted)
                }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IntensityRow(value: Float, enabled: Boolean, onChange: (Float) -> Unit) {
    // Valor local mientras se arrastra; se guarda en DataStore al soltar.
    var local by remember(value) { mutableFloatStateOf(value) }
    Column(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
        Row {
            Text("Intensidad del glitch", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
            Text("${(local * 100).toInt()}%", style = MaterialTheme.typography.labelMedium, color = BloodRed)
        }
        Slider(
            value = local,
            onValueChange = { local = it },
            onValueChangeFinished = { onChange(local) },
            enabled = enabled,
            // Pista fina y pulgar redondo, como en el diseño.
            thumb = {
                Box(Modifier.size(20.dp).background(if (enabled) BloodRed else TextMuted, CircleShape))
            },
            track = {
                Box(Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)).background(Border)) {
                    Box(Modifier.fillMaxWidth(local).fillMaxHeight().background(if (enabled) BloodRed else TextMuted))
                }
            },
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
                checkedBorderColor = BloodRed,
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
