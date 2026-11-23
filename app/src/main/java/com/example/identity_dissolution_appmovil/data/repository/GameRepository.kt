package com.example.identity_dissolution_appmovil.data.repository

import com.example.identity_dissolution_appmovil.ui.components.tr
import com.example.identity_dissolution_appmovil.ui.components.t
import com.example.identity_dissolution_appmovil.R
import com.example.identity_dissolution_appmovil.model.Article
import com.example.identity_dissolution_appmovil.model.ArticleCategory
import com.example.identity_dissolution_appmovil.model.ControlBinding
import com.example.identity_dissolution_appmovil.model.ControlSection
import com.example.identity_dissolution_appmovil.model.Enemy
import com.example.identity_dissolution_appmovil.model.InventoryItem
import com.example.identity_dissolution_appmovil.model.StoryEvent

/**
 * Fuente única de datos del juego.
 * Hoy los datos están en memoria; más adelante aquí se conecta Room (local) y Firebase (remoto)
 * sin tocar las pantallas, como pide la arquitectura MVVM de la documentación.
 */
object GameRepository {

    const val INVENTORY_SLOTS = 9

    // Artículos de la Guía, escritos a partir del GDD del juego.
    val articles = GuideContent.articles

    // Copia local de api/src/main/resources/monsters.json (Inicio y vistas previas).
    val enemies = listOf(
        Enemy(1, "El Mudo", "Cazador silencioso", "A", 0.8f, 0.7f,
            "No emite ningún sonido. Recorre los pasillos sin patrón fijo y, cuando te detecta, pasa directo a la caza: corre hacia ti y ataca con las manos sin avisar.",
            "Las descargas no lo detienen: el aturdidor y el Stunject no le hacen efecto. Vigila tu espalda en los pasillos en silencio y usa el revólver si está cerca."),
        Enemy(2, "La Boca", "Devorador", "A", 0.78f, 0.45f,
            "Patrulla los pasillos y se detiene a devorar cadáveres. Cuando te detecta, grita para avisar a los demás y ataca con mordidas y golpes.",
            "Aprovecha cuando está comiendo para pasar a su lado sin ser visto. Si te ve, aturdirlo te da unos segundos para escapar."),
        Enemy(3, "El Alto", "Coloso", "S", 0.95f, 0.35f,
            "Mide 2.5 metros. Sus brazos largos le dan un alcance enorme: lanza golpes largos con cualquiera de los dos brazos y puede agarrarte desde lejos.",
            "No dejes que te acorrale: su alcance es mayor del que parece. Es lento, así que mantén la distancia. El aturdidor lo detiene, aprovéchalo para huir."),
        Enemy(4, "El Rastreador", "Acechador", "A", 0.75f, 0.9f,
            "Se mueve agachado y sigue tu rastro. Salta sobre su presa desde lejos, la muerde y se alimenta de los cuerpos que encuentra.",
            "Es el más rápido: correr en línea recta no sirve. Busca obstáculos que corten sus saltos y atúrdelo cuando caiga cerca de ti."),
    )

    /** Arte de cada monstruo (Descargas/App-videojuego/Bestiario), por id. */
    val monsterArt = mapOf(
        1 to R.drawable.monster_mudo,
        2 to R.drawable.monster_boca,
        3 to R.drawable.monster_alto,
        4 to R.drawable.monster_rastreador,
    )

    /** Figura recortada (sin fondo) para invocar al monstruo en realidad aumentada. */
    val monsterCutout = mapOf(
        1 to R.drawable.summon_mudo,
        2 to R.drawable.summon_boca,
        3 to R.drawable.summon_alto,
        4 to R.drawable.summon_rastreador,
    )

    // Props del juego (Props/Props.blend). Usos tomados de Lukas_RH07.blend (sockets y animaciones).
    // Las imágenes son renders de esos modelos. Los props gore no van en la app.
    val inventory = listOf(
        InventoryItem(1, "Llave maestra", "🗝️", 1, "Abre las puertas cerradas con llave. Lukas la usa al abrir una puerta.", R.drawable.prop_llave_maestra, "Herramienta"),
        InventoryItem(2, "Autoinyector", "💉", 2, "Lukas se lo inyecta con la mano derecha para recuperar vida.", R.drawable.prop_autoinyector, "Médico"),
        InventoryItem(3, "Aturdidor", "⚡", 1, "Arma cuerpo a cuerpo de la mano derecha. Aturde a los monstruos unos segundos. No funciona con El Mudo.", R.drawable.prop_aturdidor, "Arma"),
        InventoryItem(4, "Stunject", "🧪", 1, "Inyección aturdidora. Deja quieto a un monstruo si logras acercarte. No funciona con El Mudo.", R.drawable.prop_stunject, "Arma"),
        InventoryItem(5, "Munición", "📦", 12, "Balas para los revólveres. Se recargan bala a bala.", R.drawable.prop_municion, "Munición"),
        InventoryItem(7, "Revólver", "🔫", 1, "La única arma de Lukas. Su pantalla muestra las balas que quedan en el tambor de 6.", R.drawable.prop_revolver, "Arma"),
        InventoryItem(8, "Tarjeta de acceso", "💳", 1, "Abre las puertas electrónicas y las zonas restringidas.", R.drawable.prop_tarjeta_acceso, "Acceso"),
        InventoryItem(9, "Terminal", "📟", 1, "Terminal de brazo. Lukas lo consulta para leer registros y abrir sistemas.", R.drawable.prop_terminal, "Dispositivo"),
    )

    val keyboardControls = listOf(
        ControlSection("Movimiento", listOf(
            ControlBinding("W A S D", "Mover personaje"),
            ControlBinding("Shift", "Esprintar"),
            ControlBinding("Espacio", "Saltar / Escalar"),
        )),
        ControlSection("Combate y acción", listOf(
            ControlBinding("Clic L", "Atacar / Disparar"),
            ControlBinding("Clic R", "Apuntar / Bloquear"),
            ControlBinding("R", "Recargar arma"),
        )),
        ControlSection("Interacción", listOf(
            ControlBinding("E", "Interactuar / Usar"),
            ControlBinding("F", "Linterna"),
            ControlBinding("Tab", "Inventario"),
        )),
    )

    val gamepadControls = listOf(
        ControlSection("Movimiento", listOf(
            ControlBinding("L Stick", "Mover personaje"),
            ControlBinding("L3", "Esprintar"),
            ControlBinding("A / ✕", "Saltar / Escalar"),
        )),
        ControlSection("Combate y acción", listOf(
            ControlBinding("RT / R2", "Atacar / Disparar"),
            ControlBinding("LT / L2", "Apuntar / Bloquear"),
            ControlBinding("X / ▢", "Recargar arma"),
        )),
        ControlSection("Interacción", listOf(
            ControlBinding("B / ◯", "Interactuar / Usar"),
            ControlBinding("RB / R1", "Linterna"),
            ControlBinding("Select", "Inventario"),
        )),
    )

    // Cronología real del GDD (03 - Historia y Mundo): Sektor V-7, 2080–2083.
    val story = listOf(
        StoryEvent("2080", "Vitalis Konzern", "La megacorporación de biotecnología opera el laboratorio de Sektor V-7. Vende algo que no entiende.",
            tag = "CORPORACIÓN", image = R.drawable.gdd_menu_terminal),
        StoryEvent("2080", "Emilia oye el latido", "Durante el turno de noche en la sala 4 del subnivel C, la científica Emilia Reiner-Hellen nota un sonido grave que se repite.",
            date = "2080-11-02", tag = "DESCUBRIMIENTO", image = R.drawable.gdd_calibracion_latido),
        StoryEvent("2081", "El incidente", "Alguien transmite el latido completo por la megafonía. Quien lo escucha queda asimilado. Nunca se confirma quién lo hizo.",
            date = "2081-03-14 · 03:13", tag = "INCIDENTE", image = R.drawable.gdd_piso3, critical = true),
        StoryEvent("2081", "Instalación sellada", "Vitalis cierra Sektor V-7 \"por mantenimiento programado\". Solo The Mother sigue activa.",
            date = "2081-04-02", tag = "CIERRE", image = R.drawable.gdd_fila_capsulas),
        StoryEvent("2082", "Emilia regresa", "Emilia vuelve una sola vez a la instalación sellada para saber qué es el latido. No sale.",
            date = "2082-10-19 · 12:48", tag = "DESAPARICIÓN", image = R.drawable.gdd_terminal_documentos, critical = true),
        StoryEvent("2083", "Expediente R-H", "The Mother la reconstruye una y otra vez: R-H-03 a R-H-06, de enero a noviembre. Cada reconstrucción alimenta al laboratorio.",
            date = "2083-01-27 → 2083-11-18", tag = "SUJETOS", image = R.drawable.gdd_mother_fase2),
        StoryEvent("2083", "El mensaje", "Lukas, solo en casa, recibe una nota de voz del diario de Emilia. De fondo suena el latido.",
            date = "Diciembre 2083", tag = "SEÑAL", image = R.drawable.gdd_lukas_civil),
        StoryEvent("Hoy", "Sujeto R-H-07", "Lukas entra con su acceso de técnico. The Mother lo registra como el séptimo sujeto.",
            tag = "EN CURSO", image = R.drawable.gdd_lukas_r_h07, current = true),
    )

    val quotes = listOf(
        "\"El silencio es el primer síntoma de que ya no estás solo.\"",
        "\"Si lo escuchas respirar, ya es tarde.\"",
        "\"Tu corazón es tu peor enemigo.\"",
    )
}
