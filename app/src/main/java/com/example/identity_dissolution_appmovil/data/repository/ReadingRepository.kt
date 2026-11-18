package com.example.identity_dissolution_appmovil.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.readingStore: DataStore<Preferences> by preferencesDataStore(name = "reading")

/** Qué leyó el usuario por última vez: un monstruo del Bestiario o un artículo de la Guía. */
sealed interface LastRead {
    data class Monster(val id: Int) : LastRead
    data class Guide(val id: Int) : LastRead
}

/** Guarda la última lectura para la tarjeta "Continuar lectura" de Inicio. */
class ReadingRepository private constructor(context: Context) {

    private val store = context.applicationContext.readingStore
    private val TYPE = stringPreferencesKey("type")
    private val ID = intPreferencesKey("id")

    val lastRead: Flow<LastRead?> = store.data.map { p ->
        val id = p[ID] ?: return@map null
        when (p[TYPE]) {
            "monster" -> LastRead.Monster(id)
            "guide" -> LastRead.Guide(id)
            else -> null
        }
    }

    suspend fun save(item: LastRead) = store.edit {
        when (item) {
            is LastRead.Monster -> { it[TYPE] = "monster"; it[ID] = item.id }
            is LastRead.Guide -> { it[TYPE] = "guide"; it[ID] = item.id }
        }
    }

    companion object {
        @Volatile private var instance: ReadingRepository? = null
        fun get(context: Context): ReadingRepository =
            instance ?: synchronized(this) { instance ?: ReadingRepository(context).also { instance = it } }
    }
}
