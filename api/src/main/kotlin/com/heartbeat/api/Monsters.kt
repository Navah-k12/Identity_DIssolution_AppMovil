package com.heartbeat.api

import io.ktor.client.HttpClient
import io.ktor.client.request.head
import io.ktor.http.isSuccess
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

/** Cómo está guardado cada monstruo en monsters.json (datos + rutas dentro del repo de modelos). */
@Serializable
data class MonsterRecord(
    val id: Int,
    val name: String,
    val enemyClass: String,
    val dangerLevel: String,
    val danger: Float,
    val speed: Float,
    val behavior: String,
    val strategy: String,
    val folder: String,
    val image: String,
    val model: String,
    /** Opcional: URL completa del .glb si el modelo está fuera del repo de modelos. */
    val modelUrl: String? = null,
    /** Opcional: imagen cinematográfica servida por esta API (resources/static/monsters). */
    val cinematic: String? = null,
)

/** Lo que la API devuelve a la app. */
@Serializable
data class MonsterDto(
    val id: Int,
    val name: String,
    val enemyClass: String,
    val dangerLevel: String,
    val danger: Float,
    val speed: Float,
    val behavior: String,
    val strategy: String,
    val imageUrl: String,
    /** null mientras el .glb no exista en el repositorio. */
    val modelUrl: String?,
    /** Imagen fija (jpg) del mismo cuadro, para vistas previas o móviles sin soporte de animación. */
    val posterUrl: String? = null,
)

/**
 * Lee los monstruos de monsters.json y arma las URLs hacia el repo de modelos 3D.
 * Los archivos del repo están en Git LFS, por eso se usa media.githubusercontent.com
 * (raw.githubusercontent.com solo devolvería el "puntero" LFS de 130 bytes).
 */
class MonsterService(
    private val http: HttpClient,
    private val baseUrl: String = DEFAULT_BASE_URL,
    private val records: List<MonsterRecord> = loadRecords(),
) {
    private val log = LoggerFactory.getLogger(MonsterService::class.java)

    // Recuerda qué modelos existen para no preguntar a GitHub en cada petición.
    private val modelExists = ConcurrentHashMap<Int, Boolean>()

    /** [publicUrl] es la dirección de esta API (p. ej. http://localhost:8080), para armar las URLs de imágenes propias. */
    suspend fun all(publicUrl: String): List<MonsterDto> = records.map { it.toDto(publicUrl) }

    suspend fun byId(id: Int, publicUrl: String): MonsterDto? = records.find { it.id == id }?.toDto(publicUrl)

    /** Vuelve a revisar en GitHub qué .glb ya se subieron. */
    suspend fun refreshModels() {
        records.forEach { r ->
            val ok = runCatching { http.head(r.modelUrl ?: url(r, r.model)).status.isSuccess() }.getOrDefault(false)
            modelExists[r.id] = ok
        }
        log.info("Modelos disponibles: {}", modelExists.filterValues { it }.keys.sorted())
    }

    private suspend fun MonsterRecord.toDto(publicUrl: String): MonsterDto {
        if (modelExists[id] == null) refreshModels()
        return MonsterDto(
            id = id,
            name = name,
            enemyClass = enemyClass,
            dangerLevel = dangerLevel,
            danger = danger,
            speed = speed,
            behavior = behavior,
            strategy = strategy,
            // La imagen cinematográfica (WebP animado) tiene prioridad sobre la hoja del repo.
            imageUrl = cinematic?.let { "$publicUrl/static/monsters/$it" } ?: url(this, image),
            posterUrl = cinematic?.let { "$publicUrl/static/monsters/${it.substringBeforeLast('.')}.jpg" },
            modelUrl = if (modelExists[id] == true) (modelUrl ?: url(this, model)) else null,
        )
    }

    private fun url(r: MonsterRecord, file: String) = "$baseUrl/${r.folder}/$file"

    companion object {
        const val DEFAULT_BASE_URL =
            "https://media.githubusercontent.com/media/Navah-k12/Low-Poly-3D-Models-for-a-Video-Game-Event/main"

        private val json = Json { ignoreUnknownKeys = true }

        fun loadRecords(): List<MonsterRecord> {
            val text = MonsterService::class.java.getResource("/monsters.json")!!.readText(Charsets.UTF_8)
            return json.decodeFromString(text)
        }
    }
}
