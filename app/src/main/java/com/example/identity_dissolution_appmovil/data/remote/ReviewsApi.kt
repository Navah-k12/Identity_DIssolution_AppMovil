package com.example.identity_dissolution_appmovil.data.remote

import com.example.identity_dissolution_appmovil.BuildConfig
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * API REST de Cloud Firestore (https://firebase.google.com/docs/firestore/use-rest-api).
 * Cada comentario es un documento de la colección "reviews". Aquí están las 4 operaciones
 * CRUD con sus métodos HTTP reales, más una consulta filtrada (runQuery).
 *
 * Firestore usa su propio formato JSON: {"fields": {"stars": {"integerValue": "5"}, ...}}.
 * La conversión a/desde la clase Review está en ReviewsRepository.
 */
interface ReviewsApi {

    /** READ (lista): GET documents/reviews */
    @GET("documents/reviews")
    suspend fun list(
        @Header("Authorization") auth: String?,
        @Query("pageSize") pageSize: Int = 100,
        @Query("orderBy") orderBy: String = "createdAt desc",
    ): JsonObject

    /** READ (uno): GET documents/reviews/{id} */
    @GET("documents/reviews/{id}")
    suspend fun get(@Header("Authorization") auth: String?, @Path("id") id: String): JsonObject

    /** CREATE: POST documents/reviews (Firestore genera el id). */
    @POST("documents/reviews")
    suspend fun create(@Header("Authorization") auth: String, @Body document: JsonObject): JsonObject

    /** UPDATE: PATCH documents/reviews/{id}. updateMask = solo los campos que cambian. */
    @PATCH("documents/reviews/{id}")
    suspend fun update(
        @Header("Authorization") auth: String,
        @Path("id") id: String,
        @Body document: JsonObject,
        @Query("updateMask.fieldPaths") fields: List<String>,
    ): JsonObject

    /** DELETE: DELETE documents/reviews/{id} */
    @DELETE("documents/reviews/{id}")
    suspend fun delete(@Header("Authorization") auth: String, @Path("id") id: String)

    /** Consulta específica (filtro por estrellas) en el servidor: POST documents:runQuery */
    // "./" es necesario: sin él, Retrofit lee "documents:" como si fuera el esquema de la URL (como "https:")
    // y falla con "Malformed URL".
    @POST("./documents:runQuery")
    suspend fun runQuery(@Header("Authorization") auth: String?, @Body query: JsonObject): JsonArray
}

object FirestoreClient {
    private val json = Json { ignoreUnknownKeys = true }

    /** Un Retrofit por proyecto de Firebase (el id sale de google-services.json). */
    fun api(projectId: String): ReviewsApi = Retrofit.Builder()
        .baseUrl("https://firestore.googleapis.com/v1/projects/$projectId/databases/(default)/")
        .client(
            ApiClient.okHttp.newBuilder()
                // En Logcat (filtro "OkHttp") se ve cada GET, POST, PATCH y DELETE con su cuerpo.
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
                    redactHeader("Authorization")
                })
                .build()
        )
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(ReviewsApi::class.java)
}

@Suppress("unused")
private val keepOkHttp: OkHttpClient? = null
