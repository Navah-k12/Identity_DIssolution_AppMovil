package com.example.identity_dissolution_appmovil.data.repository

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
        InventoryItem(7, "Revólver", "🔫", 2, "Lukas lleva uno en cada mano. Su pantalla muestra las balas que quedan.", R.drawable.prop_revolver, "Arma"),
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

    val story = listOf(
        StoryEvent("1979", "La fundación", "Se inaugura el Hospital Central con un ala experimental en el norte."),
        StoryEvent("1983", "El ritual", "El equipo del Dr. Varga intenta \"extirpar\" el miedo de sus pacientes."),
        StoryEvent("1984", "El incidente", "Desaparece todo el turno nocturno. El hospital se cierra."),
        StoryEvent("Hoy", "El regreso", "Despiertas en el sótano, sin recuerdos, con el pulso acelerado."),
    )

    val quotes = listOf(
        "\"El silencio es el primer síntoma de que ya no estás solo.\"",
        "\"Si lo escuchas respirar, ya es tarde.\"",
        "\"Tu corazón es tu peor enemigo.\"",
    )
}
