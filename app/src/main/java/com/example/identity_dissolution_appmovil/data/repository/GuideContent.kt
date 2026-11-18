package com.example.identity_dissolution_appmovil.data.repository

import com.example.identity_dissolution_appmovil.R
import com.example.identity_dissolution_appmovil.model.Article
import com.example.identity_dissolution_appmovil.model.ArticleCategory.CHARACTER
import com.example.identity_dissolution_appmovil.model.ArticleCategory.CONCEPT
import com.example.identity_dissolution_appmovil.model.ArticleCategory.ITEM
import com.example.identity_dissolution_appmovil.model.ArticleCategory.LORE
import com.example.identity_dissolution_appmovil.model.ArticleCategory.MECHANIC
import com.example.identity_dissolution_appmovil.model.ArticleCategory.PLACE

/**
 * Contenido de la Guía. Sale del GDD de Identity Dissolution
 * (survivalHorror-Demo/GDD). Los valores numéricos son los iniciales del GDD, sujetos a balance.
 */
object GuideContent {

    val articles = listOf(
        // ---------- Lore ----------
        Article(
            1, LORE, "El incidente",
            "En 2081 alguien transmitió el latido del laboratorio por la megafonía.",
            "El laboratorio de Vitalis Konzern tiene un latido, y ese latido es un texto. En 2081 alguien lo " +
                "transmitió por la megafonía desde la central del piso 3. Quien lo escuchó completo quedó asimilado.\n\n" +
                "Los documentos se contradicen sobre quién hizo la transmisión: señalan a Emilia, al encargado de la sala " +
                "de megafonía o a The Mother. Nunca se confirma.\n\n" +
                "Después, Vitalis selló la instalación y la dio de baja. Solo The Mother sigue activa.",
            featured = true, image = R.drawable.gdd_piso3,
        ),
        Article(
            2, LORE, "El mensaje de Emilia",
            "Una nota de voz programada lleva a Lukas a la instalación sellada.",
            "Diciembre de 2083. Lukas, solo en su casa, recibe en su teléfono una nota de voz del diario de Emilia, " +
                "su esposa desaparecida. Ella cuenta que investigaba lo que pasó en Vitalis, que algo salió mal y que " +
                "Lukas no debe buscarla.\n\n" +
                "De fondo, casi inaudible, suena el latido del laboratorio. Lukas oye el primer acto sin saberlo. " +
                "Va igual, por obstinación, y entra con su acceso de técnico.",
            image = R.drawable.gdd_lukas_civil,
        ),
        Article(
            3, LORE, "El laboratorio es la criatura",
            "No es un edificio infestado: el laboratorio es la entidad.",
            "El laboratorio es una criatura lovecraftiana que nunca se explica. Ni siquiera The Mother sabe qué es.\n\n" +
                "Se reacomoda en cada intento: la generación procedural es el laboratorio cambiando de forma. " +
                "También corrompe lo escrito y toma cosas de la memoria, como la foto imposible del piso 2.\n\n" +
                "No es un virus ni un arma biológica.",
        ),
        Article(
            4, LORE, "El latido",
            "Un sonido grave que funciona como un lenguaje.",
            "El laboratorio tiene su propio latido, distinto del de Lukas. Su patrón es un texto: quien lo escucha " +
                "completo queda asimilado. Nunca se explica qué dice.\n\n" +
                "El latido que se oye en la instalación solo repite el primer acto. Inquieta, pero no asimila. " +
                "Crece con cada muerte y cuando la salud está en Crítico. El segundo acto solo sonó completo en el incidente.",
        ),
        Article(
            5, LORE, "Reconstruye, asimila",
            "The Mother reconstruye a quien llega; el laboratorio lo asimila.",
            "The Mother reconstruye a todo el que llega y conserva su identidad. No entiende que cada " +
                "reconstrucción alimenta la asimilación.\n\n" +
                "Quien pierde su identidad por completo pasa a ser parte del laboratorio. Los enemigos son personas " +
                "que llegaron antes: personal de Vitalis, guardias de Kessler y sujetos de prueba. Lukas no es el primero.",
            image = R.drawable.gdd_enemigos,
        ),
        Article(
            6, LORE, "Código de sujeto R-H",
            "El expediente Reiner-Hellen: R-H-01 a R-H-06 son de Emilia.",
            "Para The Mother, Lukas es un sujeto más del expediente de Emilia. Ella ocupa de R-H-01 a R-H-06.\n\n" +
                "Lukas es R-H-07 en su primera reconstrucción y su código sube en 1 con cada muerte: R-H-08, R-H-09… " +
                "Cada reconstrucción le añade además una pieza ajena del laboratorio.",
            image = R.drawable.gdd_terminal_estado,
        ),

        // ---------- Personajes y facciones ----------
        Article(
            10, CHARACTER, "Lukas Reiner-Hellen",
            "Técnico de mantenimiento de Vitalis. Sujeto R-H-07.",
            "Lukas entra a la instalación siguiendo el mensaje de Emilia. Muere en un piso que no existe y The Mother " +
                "lo reconstruye. En esa primera reconstrucción la olvida: sabe que busca a alguien, pero no recuerda a quién.\n\n" +
                "Solo conserva un anillo que encontró junto a la bandeja de efectos personales. Nunca se sabe si es suyo o de Emilia.",
            image = R.drawable.gdd_lukas_r_h07,
        ),
        Article(
            11, CHARACTER, "Emilia Reiner-Hellen",
            "Científica de Vitalis y esposa de Lukas. Su destino es desconocido.",
            "Emilia descubrió el latido en 2080. Estaba dentro durante el incidente y solo oyó el primer acto. " +
                "The Mother la reconstruyó como R-H-01 y Vitalis la despidió.\n\n" +
                "Volvió una sola vez, el 2082-10-19, para saber qué es el latido, y no salió. Sus 6 documentos están " +
                "repartidos en los 3 pisos. Reunirlos todos cambia el final.",
        ),
        Article(
            12, CHARACTER, "The Mother",
            "La IA médica de Vitalis. Antagonista y jefe final.",
            "The Mother fue creada para contener y estudiar a la criatura. Su protocolo de preservación está corrupto: " +
                "reconstruye a todo el que conserva identidad y envía reportes que nadie lee.\n\n" +
                "Habla por altavoces y terminales con humor negro corporativo. Su cuerpo es un archivo de muestras " +
                "criogénicas montado en una grúa, en la central de megafonía del piso 3. No es la criatura: es solo máquina.",
            image = R.drawable.gdd_mother,
        ),
        Article(
            13, CHARACTER, "Vitalis Konzern",
            "Megacorporación de biotecnología que vendía algo que no entendía.",
            "Vitalis Konzern explotaba en el laboratorio algo que no entendía y lo vendía como medicina de regeneración. " +
                "Dentro hacía experimentos ilegales e inhumanos.\n\n" +
                "Tras el incidente selló la instalación. Hoy solo está presente en documentos, carteles en alemán y propaganda. " +
                "Nunca interviene directamente.",
        ),
        Article(
            14, CHARACTER, "Kessler Sicherheit AG",
            "Seguridad privada aliada de Vitalis. Su Werkschutz vigilaba sin preguntar.",
            "Su división de seguridad interna, el Werkschutz, tenía el contrato exclusivo de Sektor V-7. Sus guardias " +
                "estaban de servicio durante el incidente y hoy son asimilados.\n\n" +
                "De Kessler vienen el revólver de Lukas y el punzón de descarga.",
        ),

        // ---------- Lugares ----------
        Article(
            20, PLACE, "Fila de cápsulas",
            "La sala de reconstrucción del piso 1. El único refugio.",
            "Una fila de 7 o más cápsulas, algunas con restos y otras vacías. Lukas sale de la suya después de cada muerte.\n\n" +
                "Es el único refugio: los enemigos nunca entran. Aquí está la bandeja de efectos personales donde " +
                "The Mother deja el revólver descargado.",
            image = R.drawable.gdd_fila_capsulas,
        ),
        Article(
            21, PLACE, "Piso 1: mantenimiento",
            "Calderas, tuberías y talleres. El terreno de Lukas.",
            "El primer piso es el punto de reaparición y el tutorial del primer intento. Aquí aparecen El Alto y El Rastrero.\n\n" +
                "Tiene 2 documentos de Emilia y memorandos de Vitalis y partes de guardia de Kessler.",
            image = R.drawable.gdd_piso1,
        ),
        Article(
            22, PLACE, "Piso 2: clínica ilegal",
            "Experimentos inhumanos en espacios industriales.",
            "Más presión de enemigos: se suma La Boca. Aquí están los informes clínicos de The Mother y los registros R-H.\n\n" +
                "La primera vez que Lukas llega a la sala final encuentra la foto familiar de su casa con la cara de Emilia " +
                "borrosa. No debería estar ahí. Ese momento reinicia el contador de 5 muertes.",
            image = R.drawable.gdd_piso2,
        ),
        Article(
            23, PLACE, "Piso 3: núcleo",
            "Maquinaria de contención y la sala de The Mother.",
            "La máxima presión de enemigos: se suma El Mudo. Los documentos de este piso revelan la regla del mundo: " +
                "The Mother reconstruye y el laboratorio asimila.\n\n" +
                "Al final está la central de megafonía, desde donde se emitió el latido, y el combate contra The Mother.",
            image = R.drawable.gdd_piso3,
        ),

        // ---------- Mecánicas ----------
        Article(
            30, MECHANIC, "Cámara sobre el hombro",
            "La vista de todo el juego. Estrecha y sin espalda.",
            "Es la cámara predeterminada. Su campo de visión deja fuera la periferia y la espalda de Lukas.\n\n" +
                "Al apuntar (clic derecho) la cámara se acerca, aparece la retícula, la linterna concentra el haz y " +
                "Lukas se mueve un 30% más lento. Mientras apunta no puede correr.",
            image = R.drawable.gdd_camara_hombro,
        ),
        Article(
            31, MECHANIC, "Vista de vigilancia",
            "Ver la sala con los ojos de la máquina, a cambio de exponerte.",
            "Desde el terminal del brazo, en salas con cámara y sin enemigos en caza, Lukas ve la sala en isométrica, " +
                "con ruido CRT y ~0,3 s de retraso.\n\n" +
                "Mientras mira solo puede quedarse quieto o caminar muy lento. Usarla llena la atención de The Mother. " +
                "Si un enemigo cercano busca, la señal se degrada; si entra en caza, la vista se corta.",
            image = R.drawable.gdd_vigilancia_degradada,
        ),
        Article(
            32, MECHANIC, "Inventario de 6 casillas",
            "Cada objeto es una decisión, no almacenamiento.",
            "El inventario tiene 6 casillas. El revólver y la linterna no ocupan casilla.\n\n" +
                "Al morir, Lukas recupera el inventario que tenía al llegar al piso. Con el inventario lleno, " +
                "recoger algo obliga a soltar otra cosa.",
            image = R.drawable.gdd_inventario,
        ),
        Article(
            33, MECHANIC, "Linterna",
            "Ver en la oscuridad a cambio de delatarte.",
            "F la enciende y la apaga. Su carga dura ~120 s y se recarga sola tras ~3 s apagada.\n\n" +
                "La luz duplica la velocidad de alerta de los enemigos que ilumina y activa a El Mudo. " +
                "La Boca es ciega: la luz no le afecta.",
        ),
        Article(
            34, MECHANIC, "Salud: Bien, Herido y Crítico",
            "La salud no se regenera.",
            "Lukas pasa por tres estados visibles: Bien, Herido y Crítico. La salud no se regenera sola: solo la cura " +
                "devuelve un estado.\n\n" +
                "En Crítico el latido del laboratorio suena más fuerte.",
        ),
        Article(
            35, MECHANIC, "Hackeo por secuencias",
            "Una cadena de flechas en el terminal del brazo. El juego no se pausa.",
            "Los paneles se abren introduciendo una secuencia de flechas en el terminal. El juego sigue mientras tanto.\n\n" +
                "Un sellado es un hackeo corto de 2 o 3 entradas que cierra una puerta hasta que Lukas la cruza o " +
                "un enemigo la rompe en ~30 s.",
            image = R.drawable.gdd_hackeo,
        ),
        Article(
            36, MECHANIC, "Reciclaje y remate",
            "Un enemigo eliminado se levanta una vez más.",
            "El laboratorio consume el último rastro de identidad de un enemigo caído y lo levanta más rápido y más " +
                "frágil. Ocurre una sola vez.\n\n" +
                "Para evitarlo hay que rematarlo: con un golpe en el suelo o con el punzón de descarga (E sobre el cuerpo).",
            image = R.drawable.gdd_remate,
        ),
        Article(
            37, MECHANIC, "Muerte y reconstrucción",
            "Morir no es perder: es parte del ciclo.",
            "Cada muerte devuelve a Lukas al inicio del piso donde cayó, con una pieza ajena más y un código de sujeto mayor. " +
                "El laboratorio se reacomoda: las salas cambian.\n\n" +
                "Los documentos y el conocimiento persisten en el terminal del brazo. " +
                "Cinco muertes seguidas llevan al final Asimilación.",
        ),
        Article(
            38, MECHANIC, "Cierre de tránsito",
            "Escaleras y elevador se bloquean durante una caza.",
            "Mientras haya enemigos en caza, la escalera y el elevador de la sala final quedan en LOCKDOWN. " +
                "Al terminar la caza se autoguarda y se abren.",
            image = R.drawable.gdd_cierre_transito,
        ),

        // ---------- Objetos ----------
        Article(
            40, ITEM, "Revólver de Kessler",
            "La única arma. Tambor de 6 balas.",
            "Revólver de servicio del Werkschutz con contador de balas en el armazón. Clic derecho para apuntar, " +
                "clic izquierdo para disparar y R para recargar bala a bala.\n\n" +
                "Solo dispara con una identidad autorizada. The Mother lo devuelve descargado tras cada reconstrucción. " +
                "Los disparos alertan a ~20 m.",
            image = R.drawable.gdd_objetos_inventario,
        ),
        Article(
            41, ITEM, "Punzón de descarga",
            "Defensa contra agarres o remate silencioso.",
            "Equipo de contención de Kessler, de un solo uso. Se activa solo cuando un enemigo agarra a Lukas y lo aturde 2 s.\n\n" +
                "También sirve para rematar: E sobre un cuerpo quema su último rastro de identidad y evita el reciclaje. " +
                "Gastarlo así deja a Lukas sin defensa ante el próximo agarre.",
        ),
        Article(
            42, ITEM, "Cura",
            "Kit médico de Vitalis con un autoinyector dentro.",
            "Recupera un estado de salud. Se usa desde el inventario en ~1,5 s y un golpe durante la animación la cancela " +
                "sin gastarla. Lleva la etiqueta en alemán Regenerationsmittel.",
        ),
        Article(
            43, ITEM, "Munición",
            "Blísteres de 4 cartuchos sin vaina.",
            "Cada casilla guarda hasta 12 balas, que son 3 blísteres. R carga bala a bala, ~0,7 s por bala. " +
                "Se puede cancelar y se conservan las balas ya cargadas. Se pierde al morir.",
        ),
        Article(
            44, ITEM, "Llaves de acceso",
            "Tarjetas de Vitalis: obligatorias y opcionales.",
            "Hay una llave obligatoria por piso: abre la sala final. Es una placa grande con banda cian e icono de escalera. " +
                "Las opcionales, con banda gris, abren atajos o salas con recursos. Se gastan al usarlas.",
        ),
        Article(
            45, ITEM, "El anillo",
            "Lo único que Lukas sabe importante.",
            "Una alianza de acero gastada. La inscripción interior está raspada para que nunca se sepa de quién es. " +
                "No ocupa casilla: queda como registro en el terminal del brazo.",
        ),
        Article(
            46, ITEM, "Documentos",
            "6 de Emilia y 12 más. Algunos corruptos.",
            "Los documentos cuentan la historia y persisten entre muertes. Los 6 de Emilia están en salas clave " +
                "secundarias, 2 por piso, y desbloquean la decisión final.\n\n" +
                "La corrupción es un glitch del laboratorio, no una censura, y nunca oculta información necesaria para avanzar.",
            image = R.drawable.gdd_documentos,
        ),

        // ---------- Conceptos ----------
        Article(
            50, CONCEPT, "Survival horror que no se memoriza",
            "Cada intento reordena las salas.",
            "El género suele apoyarse en conocer el mapa. Aquí, morir obliga a enfrentar un entorno en parte desconocido: " +
                "las salas procedurales cambian y las salas clave cambian de posición.\n\n" +
                "Identity Dissolution no es un shooter ni una fantasía de poder: Lukas nunca se vuelve mucho más fuerte.",
        ),
        Article(
            51, CONCEPT, "Los finales",
            "Uno de derrota y tres de victoria. Ninguno ofrece salvación.",
            "Asimilación: 5 muertes seguidas y Lukas se convierte en un Mudo.\n\n" +
                "Relevo básico: vencer a The Mother sin los 6 documentos de Emilia. Lukas la reemplaza sin recordar ningún nombre.\n\n" +
                "Relevo completo y Ambiguo: con los 6 documentos, Lukas recupera los nombres y elige entre la terminal " +
                "central y la salida.",
        ),
    )
}
