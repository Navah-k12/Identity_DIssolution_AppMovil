package com.example.identity_dissolution_appmovil.data.repository

import com.example.identity_dissolution_appmovil.data.remote.FirestoreClient
import com.example.identity_dissolution_appmovil.data.remote.ReviewsApi
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject
import kotlinx.serialization.json.addJsonObject
import java.time.Instant

/** Un comentario sobre la app con su calificación de 1 a 5 estrellas. */
data class Review(
    val id: String,
    val uid: String,
    val author: String,
    val stars: Int,
    val text: String,
    val createdAt: String,
    val edited: Boolean = false,
)

/**
 * Repository de comentarios: habla con la API REST de Firestore y convierte su JSON a [Review].
 * Para escribir hace falta sesión: se envía el token de Firebase Auth en "Authorization: Bearer …".
 */
class ReviewsRepository {

    private val auth: FirebaseAuth? =
        runCatching { if (FirebaseApp.getApps(FirebaseApp.getInstance().applicationContext).isEmpty()) null else FirebaseAuth.getInstance() }.getOrNull()

    private val api: ReviewsApi? = runCatching {
        FirebaseApp.getInstance().options.projectId?.let { FirestoreClient.api(it) }
    }.getOrNull()

    val available: Boolean get() = api != null
    val currentUid: String? get() = auth?.currentUser?.uid

    // ---------- CRUD ----------

    /** GET: todos los comentarios, más nuevos primero. */
    suspend fun getAll(): List<Review> =
        requireApi().list(token()).let { body ->
            (body["documents"] as? kotlinx.serialization.json.JsonArray).orEmpty().map { toReview(it.jsonObject) }
        }

    /** GET por estrellas, filtrado en el servidor con runQuery. */
    suspend fun getByStars(stars: Int): List<Review> {
        val query = buildJsonObject {
            putJsonObject("structuredQuery") {
                putJsonArray("from") { addJsonObject { put("collectionId", "reviews") } }
                putJsonObject("where") {
                    putJsonObject("fieldFilter") {
                        putJsonObject("field") { put("fieldPath", "stars") }
                        put("op", "EQUAL")
                        putJsonObject("value") { put("integerValue", stars.toString()) }
                    }
                }
            }
        }
        return requireApi().runQuery(token(), query)
            .mapNotNull { (it.jsonObject["document"] as? JsonObject)?.let(::toReview) }
            .sortedByDescending { it.createdAt }
    }

    /** POST: crea un comentario del usuario con sesión. */
    suspend fun create(stars: Int, text: String): Review {
        val user = auth?.currentUser ?: error("Inicia sesión para comentar.")
        val now = Instant.now().toString()
        val doc = fields {
            putJsonObject("uid") { put("stringValue", user.uid) }
            putJsonObject("author") { put("stringValue", user.displayName?.takeIf { it.isNotBlank() } ?: user.email?.substringBefore('@') ?: "Sujeto") }
            putJsonObject("stars") { put("integerValue", stars.toString()) }
            putJsonObject("text") { put("stringValue", text.trim()) }
            putJsonObject("createdAt") { put("timestampValue", now) }
        }
        return toReview(requireApi().create(requireToken(), doc))
    }

    /** PATCH: cambia solo estrellas y texto (y marca que se editó). */
    suspend fun update(id: String, stars: Int, text: String): Review {
        val doc = fields {
            putJsonObject("stars") { put("integerValue", stars.toString()) }
            putJsonObject("text") { put("stringValue", text.trim()) }
            putJsonObject("edited") { put("booleanValue", true) }
        }
        return toReview(requireApi().update(requireToken(), id, doc, listOf("stars", "text", "edited")))
    }

    /** DELETE: borra el comentario. */
    suspend fun delete(id: String) = requireApi().delete(requireToken(), id)

    // ---------- Conversión Firestore JSON <-> Review ----------

    private fun fields(block: kotlinx.serialization.json.JsonObjectBuilder.() -> Unit) =
        buildJsonObject { putJsonObject("fields", block) }

    private fun toReview(doc: JsonObject): Review {
        val f = doc["fields"]?.jsonObject ?: JsonObject(emptyMap())
        fun str(k: String) = f[k]?.jsonObject?.values?.firstOrNull()?.jsonPrimitive?.content.orEmpty()
        return Review(
            id = doc["name"]!!.jsonPrimitive.content.substringAfterLast('/'),
            uid = str("uid"),
            author = str("author"),
            stars = str("stars").toIntOrNull()?.coerceIn(1, 5) ?: 1,
            text = str("text"),
            createdAt = str("createdAt"),
            edited = str("edited") == "true",
        )
    }

    private fun requireApi() = api ?: error("Firebase no está configurado.")

    private suspend fun token(): String? =
        auth?.currentUser?.getIdToken(false)?.await()?.token?.let { "Bearer $it" }

    private suspend fun requireToken(): String = token() ?: error("Inicia sesión para comentar.")
}

@Suppress("unused")
private fun JsonObject.intOrNull(k: String) = this[k]?.jsonPrimitive?.int
