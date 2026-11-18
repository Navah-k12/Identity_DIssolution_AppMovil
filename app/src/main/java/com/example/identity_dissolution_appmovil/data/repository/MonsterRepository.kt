package com.example.identity_dissolution_appmovil.data.repository

import android.content.Context
import com.example.identity_dissolution_appmovil.data.local.AppDatabase
import com.example.identity_dissolution_appmovil.data.local.MonsterDao
import com.example.identity_dissolution_appmovil.data.local.MonsterEntity
import com.example.identity_dissolution_appmovil.data.remote.ApiClient
import com.example.identity_dissolution_appmovil.data.remote.MonsterApi
import com.example.identity_dissolution_appmovil.data.remote.MonsterDto
import com.example.identity_dissolution_appmovil.model.Enemy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Fuente de verdad del Bestiario.
 * - Room guarda la copia local y la UI siempre lee de Room.
 * - refresh() pide los datos a la API y actualiza Room.
 */
class MonsterRepository(
    private val dao: MonsterDao,
    private val api: MonsterApi,
    private val http: OkHttpClient,
    private val cacheDir: File,
) {
    val monsters: Flow<List<Enemy>> = dao.observeAll().map { list -> list.map { it.toEnemy() } }

    suspend fun refresh(): Result<Unit> = runCatching {
        val remote = api.getMonsters()
        dao.replaceAll(remote.map { it.toEntity() })
    }

    suspend fun monster(id: Int): Enemy? = dao.byId(id)?.toEnemy()

    /**
     * Descarga el .glb (o lo lee de la caché) y lo devuelve como ByteBuffer,
     * que es lo que necesita SceneView/Filament para crear el modelo.
     */
    suspend fun loadModel(id: Int, url: String): ByteBuffer = withContext(Dispatchers.IO) {
        val file = File(cacheDir, "models/monster_$id.glb")
        if (!file.exists()) {
            file.parentFile?.mkdirs()
            http.newCall(Request.Builder().url(url).build()).execute().use { res ->
                if (!res.isSuccessful) throw IOException("HTTP ${res.code} al descargar el modelo")
                val tmp = File(file.path + ".part")
                res.body.byteStream().use { input -> tmp.outputStream().use { input.copyTo(it) } }
                tmp.renameTo(file)
            }
        }
        val bytes = file.readBytes()
        ByteBuffer.allocateDirect(bytes.size).order(ByteOrder.nativeOrder()).put(bytes).apply { rewind() }
    }

    companion object {
        @Volatile private var instance: MonsterRepository? = null

        fun get(context: Context): MonsterRepository =
            instance ?: synchronized(this) {
                instance ?: MonsterRepository(
                    dao = AppDatabase.get(context).monsterDao(),
                    api = ApiClient.monsterApi,
                    http = ApiClient.okHttp,
                    cacheDir = context.applicationContext.cacheDir,
                ).also { instance = it }
            }
    }
}

fun MonsterDto.toEntity() = MonsterEntity(
    id, name, enemyClass, dangerLevel, danger, speed, behavior, strategy, imageUrl, modelUrl,
)

fun MonsterEntity.toEnemy() = Enemy(
    id = id,
    name = name,
    enemyClass = enemyClass,
    dangerLevel = dangerLevel,
    danger = danger,
    speed = speed,
    behavior = behavior,
    strategy = strategy,
    imageUrl = imageUrl,
    modelUrl = modelUrl,
)
