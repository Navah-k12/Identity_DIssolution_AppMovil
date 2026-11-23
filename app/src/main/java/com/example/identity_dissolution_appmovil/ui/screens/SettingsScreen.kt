package com.example.identity_dissolution_appmovil.ui.screens

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.sp
import com.example.identity_dissolution_appmovil.viewmodel.AuthViewModel
import com.example.identity_dissolution_appmovil.viewmodel.AuthUiState
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import com.example.identity_dissolution_appmovil.ui.components.tr
import com.example.identity_dissolution_appmovil.ui.components.t
import androidx.compose.ui.tooling.preview.Preview
import com.example.identity_dissolution_appmovil.ui.theme.Identity_DIssolution_AppMovilTheme
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.example.identity_dissolution_appmovil.R
import com.example.identity_dissolution_appmovil.ui.components.GlitchText
import com.example.identity_dissolution_appmovil.ui.components.ScreenHeader
import com.example.identity_dissolution_appmovil.ui.components.retroScanlines
import com.example.identity_dissolution_appmovil.ui.theme.Background
import com.example.identity_dissolution_appmovil.ui.theme.HorrorFont
import com.example.identity_dissolution_appmovil.ui.theme.OnRed
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
fun SettingsScreen(onOpenReviews: () -> Unit = {}, viewModel: SettingsViewModel = viewModel(), authViewModel: AuthViewModel = viewModel()) {
    val s by viewModel.state.collectAsState()
    val auth by authViewModel.state.collectAsState()
    SettingsContent(
        s = s,
        onLightTheme = viewModel::setLightTheme,
        onOpenReviews = onOpenReviews,
        account = {
            val context = LocalContext.current
            AccountCard(auth, authViewModel::signIn, authViewModel::register, { authViewModel.signInWithGoogle(context) }, authViewModel::signOut, authViewModel::clearError)
        },
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
    onLightTheme: (Boolean) -> Unit = {},
    onOpenReviews: () -> Unit = {},
    account: @Composable () -> Unit = {},
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
        ScreenHeader(tr("Configuración del sistema"))
        Column(Modifier.verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 4.dp)) {

            SectionLabel(tr("Cuenta"))
            account()

            Spacer(Modifier.height(16.dp))
            SectionLabel(tr("Visual y ambiente"))
            HorrorCard(Modifier.fillMaxWidth()) {
                // Vista previa en vivo del glitch y del post-procesado.
                Box(
                    Modifier.fillMaxWidth().height(96.dp).background(Background).retroScanlines(s.retroPostProcess),
                    contentAlignment = Alignment.Center,
                ) {
                    GlitchText("SOBREVIVIR", style = TextStyle(fontFamily = BloodFont, fontSize = 30.sp, letterSpacing = 2.sp), color = BloodRed)
                }
                ToggleRow(
                    tr("Efecto de glitch"), s.glitchEffect, onGlitch,
                    subtitle = tr("Distorsión en títulos y transiciones. Desactívalo si sientes mareo o fatiga visual."),
                )
                Divider()
                ToggleRow(tr("Tema claro"), s.lightTheme, onLightTheme,
                    subtitle = tr("Papel clínico y carmesí de Vitalis. Los colores salen de las paletas del juego."))
                Divider()
                ToggleRow(tr("Post-procesado retro"), s.retroPostProcess, onRetro,
                    subtitle = tr("Líneas de monitor antiguo sobre toda la app."))
                Divider()
                IntensityRow(s.glitchIntensity, enabled = s.glitchEffect, onChange = onIntensity)
            }

            Spacer(Modifier.height(16.dp))
            SectionLabel(tr("Accesibilidad"))
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
                LinkRow(tr("Idioma"), value = s.language.uppercase(), onClick = onToggleLanguage)
                Divider()
                ToggleRow(tr("Sincronización automática"), s.autoSync, onAutoSync)
                Divider()
                ToggleRow(tr("Notificaciones de pulso"), s.pulseNotifications, onPulse)
            }

            Spacer(Modifier.height(16.dp))
            SectionLabel(tr("Información"))
            HorrorCard(Modifier.fillMaxWidth()) {
                LinkRow(t("Comentarios y calificación", "Reviews & rating"), onClick = onOpenReviews)
                Divider()
                LinkRow(tr("Acerca de la app")) {
                    dialog = "Identity Dissolution" to t("Versión 1.0\nApp complementaria del videojuego de survival horror Identity Dissolution.", "Version 1.0\nCompanion app for the survival horror game Identity Dissolution.")
                }
                Divider()
                LinkRow(tr("Licencias de código abierto")) {
                    dialog = tr("Licencias") to tr("Jetpack Compose, Navigation y Lifecycle: Apache License 2.0.")
                }
                Divider()
                LinkRow(tr("Derechos de autor")) {
                    dialog = tr("Derechos de autor") to t(
                        "© 2026 Identity Dissolution. Todos los derechos reservados.\n\nIdentity Dissolution, sus personajes, monstruos, " +
                            "modelos, imágenes y tráiler son propiedad de sus autores. Prohibida su copia o distribución sin permiso.",
                        "© 2026 Identity Dissolution. All rights reserved.\n\nIdentity Dissolution, its characters, monsters, " +
                            "models, images and trailer belong to their authors. Copying or distribution without permission is prohibited.",
                    )
                }
                Divider()
                LinkRow(tr("Equipo de desarrollo")) {
                    dialog = tr("Equipo de desarrollo") to listOf(
                        "Bryan Ivan Navarrete Canche",
                        "Christian Coronel Palma",
                        "Lian Daniel Sierra Vera",
                        "Victor Gabriel Alvarado Chay",
                        "Lenin Ramón López Córdova",
                    ).joinToString("\n")
                }
                Divider()
                LinkRow(tr("Política de privacidad")) {
                    dialog = tr("Privacidad") to tr("La app no recopila datos personales. Todo se guarda en tu dispositivo.")
                }
            }

            Spacer(Modifier.height(24.dp))
            OutlinedButton(
                onClick = { dialog = tr("Datos borrados") to tr("Los ajustes volvieron a sus valores iniciales.") ; onReset() },
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


/**
 * Cuenta con Firebase: si no hay sesión, formulario de correo y contraseña
 * (iniciar sesión o crear cuenta); si la hay, el correo y "Cerrar sesión".
 */
@Composable
private fun AccountCard(
    auth: AuthUiState,
    onSignIn: (String, String) -> Unit,
    onRegister: (String, String) -> Unit,
    onGoogle: () -> Unit,
    onSignOut: () -> Unit,
    onClearError: () -> Unit,
) {
    HorrorCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            when {
                !auth.available -> Text(
                    t("Firebase no está configurado: falta app/google-services.json.",
                        "Firebase is not configured: app/google-services.json is missing."),
                    color = TextMuted, style = MaterialTheme.typography.bodyMedium,
                )
                auth.email != null -> {
                    Text(t("Sesión iniciada", "Signed in"), style = MaterialTheme.typography.labelMedium, color = BloodRed)
                    Spacer(Modifier.height(4.dp))
                    Text(auth.email, style = MaterialTheme.typography.bodyLarge, color = TextMain)
                    Spacer(Modifier.height(12.dp))
                    TextButton(onClick = onSignOut) { Text(t("CERRAR SESIÓN", "SIGN OUT"), color = BloodRed, fontWeight = FontWeight.Bold) }
                }
                else -> {
                    var email by remember { mutableStateOf("") }
                    var password by remember { mutableStateOf("") }
                    var showPassword by remember { mutableStateOf(false) }
                    val fieldColors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BloodRed, unfocusedBorderColor = Border, cursorColor = BloodRed,
                        focusedLabelColor = BloodRed, unfocusedLabelColor = TextMuted,
                        focusedTextColor = TextMain, unfocusedTextColor = TextMain,
                    )
                    Text(t("Inicia sesión para guardar tu progreso", "Sign in to save your progress"),
                        style = MaterialTheme.typography.bodyMedium, color = TextMuted)
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        email, { email = it; onClearError() }, label = { Text(t("Correo", "Email")) }, singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = fieldColors, modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        password, { password = it; onClearError() }, label = { Text(t("Contraseña", "Password")) }, singleLine = true,
                        // Ojo para mostrar u ocultar la contraseña.
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    if (showPassword) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                    contentDescription = if (showPassword) t("Ocultar contraseña", "Hide password") else t("Mostrar contraseña", "Show password"),
                                    tint = TextMuted,
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = fieldColors, modifier = Modifier.fillMaxWidth(),
                    )
                    auth.error?.let {
                        Spacer(Modifier.height(8.dp))
                        Text(it, color = BloodRed, style = MaterialTheme.typography.bodySmall)
                    }
                    Spacer(Modifier.height(12.dp))
                    if (auth.loading) {
                        CircularProgressIndicator(color = BloodRed, modifier = Modifier.size(28.dp))
                    } else {
                        Row {
                            Button(
                                onClick = { onSignIn(email, password) },
                                colors = ButtonDefaults.buttonColors(containerColor = DarkRed, contentColor = Color.White),
                            ) { Text(t("INICIAR SESIÓN", "SIGN IN"), fontWeight = FontWeight.Bold) }
                            Spacer(Modifier.width(8.dp))
                            TextButton(onClick = { onRegister(email, password) }) {
                                Text(t("CREAR CUENTA", "SIGN UP"), color = BloodRed, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        // Separador "o".
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.weight(1f).height(1.dp).background(Border))
                            Text(t("o", "or"), color = TextMuted, modifier = Modifier.padding(horizontal = 12.dp))
                            Box(Modifier.weight(1f).height(1.dp).background(Border))
                        }
                        Spacer(Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = onGoogle,
                            border = BorderStroke(1.dp, Border),
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                        ) {
                            Text("G", color = BloodRed, fontWeight = FontWeight.Black, fontSize = 18.sp)
                            Spacer(Modifier.width(10.dp))
                            Text(t("CONTINUAR CON GOOGLE", "CONTINUE WITH GOOGLE"), color = TextMain, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
