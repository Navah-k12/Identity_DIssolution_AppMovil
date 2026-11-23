package com.example.identity_dissolution_appmovil.data.remote

import com.example.identity_dissolution_appmovil.BuildConfig
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

/** Lo que responde la Heartbeat API (carpeta api/ de este proyecto). */
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
    val modelUrl: String? = null,
)

/** Endpoints de la API. Retrofit genera la implementación. */
interface MonsterApi {
    @GET("api/monsters")
    suspend fun getMonsters(): List<MonsterDto>

    @GET("api/monsters/{id}")
    suspend fun getMonster(@Path("id") id: Int): MonsterDto
}

/** Un solo cliente HTTP y un solo Retrofit para toda la app. */
object ApiClient {

    val okHttp: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            // Los modelos 3D pueden tardar en descargarse.
            .readTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC
                else HttpLoggingInterceptor.Level.NONE
            })
            .build()
    }

    private val json = Json { ignoreUnknownKeys = true }

    val monsterApi: MonsterApi by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(okHttp)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(MonsterApi::class.java)
    }
}
