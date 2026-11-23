package com.example.identity_dissolution_appmovil.ui.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Idioma de la app (Ajustes → Idioma). Es un estado de Compose: al cambiarlo,
 * todo texto que pasa por [tr] o [t] se vuelve a dibujar en el nuevo idioma.
 */
object Lang {
    var english by mutableStateOf(false)
}

/** Texto fijo con su versión en inglés. Para textos con variables. */
fun t(es: String, en: String): String = if (Lang.english) en else es

/** Traduce un texto fijo de la interfaz. Si no está en el diccionario, se queda en español. */
fun tr(es: String): String = if (Lang.english) EN[es] ?: es else es

private val EN = mapOf(
    // Navegación y comunes
    "Inicio" to "Home", "Guía" to "Guide", "Inventario" to "Inventory", "Ajustes" to "Settings",
    "Volver" to "Back", "MOVIMIENTO" to "MOVEMENT", "PREFERENCIAS" to "PREFERENCES",
    "CONTINUAR LECTURA" to "CONTINUE READING", "LORE" to "LORE", "ARMA" to "WEAPON",
    "DESTACADO" to "FEATURED", "NUEVO" to "NEW",
    // Categorías de la Guía
    "Lore" to "Lore", "Personaje" to "Character", "Lugar" to "Place", "Mecánica" to "Mechanic",
    "Objeto" to "Item", "Concepto" to "Concept",
    // Inicio
    "Enciclopedia" to "Encyclopedia", "Mecánicas de juego" to "Game mechanics",
    "Bestiario" to "Bestiary", "Amenazas registradas" to "Recorded threats", "Nuevo" to "New",
    "Controles" to "Controls", "Teclado y mando" to "Keyboard & gamepad",
    "Historia" to "Story", "Cronología del lore" to "Lore timeline",
    "// Sesión activa" to "// Active session", "Continuar lectura" to "Continue reading",
    "Tráiler oficial" to "Official trailer", "Accesos rápidos" to "Quick access", "Pulso" to "Pulse",
    "Reproducir" to "Play", "Tráiler" to "Trailer", "Archivos leídos" to "Files read", "Siguiente: " to "Next: ",
    "El incidente" to "The incident", "Guía · Lore · 4 min restantes" to "Guide · Lore · 4 min left",
    "El latido" to "The heartbeat",
    // Guía
    "Registros de sangre" to "Blood records", "Ver guardados" to "View saved", "Guardados" to "Saved",
    "Archivos" to "Files", "Ningún registro coincide... algo los borró." to "No record matches... something erased them.",
    "Buscar en los archivos…" to "Search the files…", "Todo" to "All", "Destacado" to "Featured",
    "CONTINUAR" to "CONTINUE", "LEER" to "READ", "Quitar marcador" to "Remove bookmark", "Guardar" to "Save",
    "● NUEVO" to "● NEW", "✓ LEÍDO" to "✓ READ", "LEÍDO" to "READ", "última lectura" to "last read",
    "Archivo" to "File", "Archivo corrupto." to "Corrupted file.", "Cronología" to "Timeline",
    "Teclado" to "Keyboard", "Mando" to "Gamepad", "tecla física" to "physical key",
    // Bestiario
    "Sin conexión: mostrando registros guardados. Toca aquí para reintentar." to "Offline: showing saved records. Tap to retry.",
    "Registro" to "Record", "Peligro" to "Danger", "Velocidad" to "Speed", "Comportamiento" to "Behavior",
    "Estrategia" to "Strategy", "INVOCAR MONSTRUO" to "SUMMON MONSTER",
    "ARCHIVO INCOMPLETO... ENCUENTRA MÁS INFORMACIÓN EN EL JUEGO." to "INCOMPLETE FILE... FIND MORE INFORMATION IN THE GAME.",
    // Invocación
    "Invocar monstruo" to "Summon monster", "INVOCACIÓN" to "SUMMONING",
    "Necesitamos la cámara para invocar al monstruo en tu mundo." to "We need the camera to summon the monster into your world.",
    "DAR PERMISO" to "GRANT PERMISSION", "Apunta al piso y mueve el teléfono despacio…" to "Point at the floor and move the phone slowly…",
    "Piso detectado: apunta la mira e invoca" to "Floor detected: aim and summon",
    "Busca más luz o mueve el teléfono más despacio" to "Find more light or move the phone more slowly",
    "No hay piso en la mira. Apunta a una zona detectada" to "No floor in sight. Aim at a detected area",
    "RA desactivada" to "AR off", "hábitat" to "habitat", "Sin permiso de cámara: modo sin AR." to "No camera permission: AR off.",
    "Sin giroscopio: el monstruo quedará en el centro" to "No gyroscope: the monster will stay in the center",
    "Apunta a donde quieras que aparezca" to "Point where you want it to appear",
    "Sigue la flecha para encontrarlo" to "Follow the arrow to find it",
    "Sin giroscopio: el monstruo no seguirá el movimiento" to "No gyroscope: the monster won't follow your movement",
    "Modelo 3D" to "3D model", "Invocando al monstruo..." to "Summoning the monster...", "REINTENTAR" to "RETRY",
    "VER EN TU MUNDO (AR)" to "SEE IN YOUR WORLD (AR)", "Arrastra para girar • Pellizca para acercar" to "Drag to rotate • Pinch to zoom",
    // Inventario
    "Inventario crítico" to "Critical inventory", "1 espacio libre" to "1 free slot", "LIBRE" to "FREE",
    "Herramienta" to "Tool", "Médico" to "Medical", "Munición" to "Ammo", "Acceso" to "Access", "Dispositivo" to "Device",
    "Llave maestra" to "Master key", "Autoinyector" to "Autoinjector", "Aturdidor" to "Stun baton",
    "Revólver" to "Revolver", "Tarjeta de acceso" to "Access card", "Terminal" to "Terminal", "Stunject" to "Stunject",
    "Abre las puertas cerradas con llave. Lukas la usa al abrir una puerta." to "Opens locked doors. Lukas uses it when opening a door.",
    "Lukas se lo inyecta con la mano derecha para recuperar vida." to "Lukas injects it with his right hand to recover health.",
    "Arma cuerpo a cuerpo de la mano derecha. Aturde a los monstruos unos segundos. No funciona con El Mudo." to "Right-hand melee weapon. Stuns monsters for a few seconds. Doesn't work on The Mute.",
    "Inyección aturdidora. Deja quieto a un monstruo si logras acercarte. No funciona con El Mudo." to "Stun injection. Freezes a monster if you get close enough. Doesn't work on The Mute.",
    "Balas para los revólveres. Se recargan bala a bala." to "Revolver rounds. Reloaded one bullet at a time.",
    "La única arma de Lukas. Su pantalla muestra las balas que quedan en el tambor de 6." to "Lukas's only weapon. Its display shows the rounds left in the 6-shot cylinder.",
    "Abre las puertas electrónicas y las zonas restringidas." to "Opens electronic doors and restricted areas.",
    "Terminal de brazo. Lukas lo consulta para leer registros y abrir sistemas." to "Arm terminal. Lukas uses it to read records and open systems.",
    // Ajustes
    "Configuración del sistema" to "System settings", "Visual y ambiente" to "Visuals & atmosphere",
    "SOBREVIVIR" to "SURVIVE", "Efecto de glitch" to "Glitch effect",
    "Distorsión en títulos y transiciones. Desactívalo si sientes mareo o fatiga visual." to "Distortion on titles and transitions. Turn it off if you feel dizzy or eye strain.",
    "Post-procesado retro" to "Retro post-processing", "Líneas de monitor antiguo sobre toda la app." to "Old monitor scanlines over the whole app.",
    "Accesibilidad" to "Accessibility", "Tamaño de texto" to "Text size",
    "El silencio es el primer síntoma de que ya no estás solo." to "Silence is the first sign that you're no longer alone.",
    "Idioma" to "Language", "Sincronización automática" to "Auto sync", "Notificaciones de pulso" to "Pulse notifications",
    "Información" to "Information", "Acerca de la app" to "About the app",
    "Licencias de código abierto" to "Open source licenses", "Licencias" to "Licenses",
    "Jetpack Compose, Navigation y Lifecycle: Apache License 2.0." to "Jetpack Compose, Navigation and Lifecycle: Apache License 2.0.",
    "Política de privacidad" to "Privacy policy", "Privacidad" to "Privacy",
    "La app no recopila datos personales. Todo se guarda en tu dispositivo." to "The app collects no personal data. Everything is stored on your device.",
    "Datos borrados" to "Data cleared", "Los ajustes volvieron a sus valores iniciales." to "Settings were reset to their defaults.",
    "BORRAR DATOS RECOPILADOS" to "CLEAR COLLECTED DATA", "CERRAR" to "CLOSE", "Intensidad del glitch" to "Glitch intensity",
    "Derechos de autor" to "Copyright", "Equipo de desarrollo" to "Development team",
    // Pantalla de carga
    "CONECTANDO CON THE MOTHER…" to "CONNECTING TO THE MOTHER…",
    "RECUPERANDO REGISTROS DE VITALIS…" to "RECOVERING VITALIS RECORDS…",
    "RECONSTRUYENDO IDENTIDAD…" to "RECONSTRUCTING IDENTITY…",
    "ANALIZANDO AMENAZAS DEL LABORATORIO…" to "ANALYZING LAB THREATS…",
    "IDENTIDAD RESTAURADA" to "IDENTITY RESTORED", "EN LÍNEA" to "ONLINE", "ARCHIVOS DE VITALIS" to "VITALIS FILES",
    "v1.0 · TOCA PARA ENTRAR" to "v1.0 · TAP TO ENTER",
)
