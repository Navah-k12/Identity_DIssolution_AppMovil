package com.example.identity_dissolution_appmovil.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

/** Copia local de cada monstruo para que el Bestiario funcione sin internet. */
@Entity(tableName = "monsters")
data class MonsterEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val enemyClass: String,
    val dangerLevel: String,
    val danger: Float,
    val speed: Float,
    val behavior: String,
    val strategy: String,
    val imageUrl: String,
    val modelUrl: String?,
)

@Dao
interface MonsterDao {
    @Query("SELECT * FROM monsters ORDER BY id")
    fun observeAll(): Flow<List<MonsterEntity>>

    @Query("SELECT * FROM monsters WHERE id = :id")
    suspend fun byId(id: Int): MonsterEntity?

    @Upsert
    suspend fun upsertAll(monsters: List<MonsterEntity>)

    @Query("DELETE FROM monsters WHERE id NOT IN (:keepIds)")
    suspend fun deleteAllExcept(keepIds: List<Int>)

    /** Deja la tabla igual que la respuesta de la API, en una sola transacción. */
    @Transaction
    suspend fun replaceAll(monsters: List<MonsterEntity>) {
        upsertAll(monsters)
        deleteAllExcept(monsters.map { it.id })
    }
}

@Database(entities = [MonsterEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun monsterDao(): MonsterDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        /** Singleton: abrir varias bases sobre el mismo archivo da problemas. */
        fun get(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "heartbeat.db",
                ).build().also { instance = it }
            }
    }
}
