package com.example.identity_dissolution_appmovil.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.identity_dissolution_appmovil.model.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Un solo DataStore para toda la app (crear dos sobre el mismo archivo lanza una excepción).
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/** Guarda los ajustes en el dispositivo para que sobrevivan al cerrar la app. */
class SettingsRepository(context: Context) {

    private val store = context.applicationContext.dataStore

    private object Keys {
        val GLITCH = booleanPreferencesKey("glitch_effect")
        val RETRO = booleanPreferencesKey("retro_post_process")
        val LANGUAGE = stringPreferencesKey("language")
        val AUTO_SYNC = booleanPreferencesKey("auto_sync")
        val PULSE = booleanPreferencesKey("pulse_notifications")
        val INTENSITY = floatPreferencesKey("glitch_intensity")
        val TEXT_SIZE = intPreferencesKey("text_size")
        val LIGHT = booleanPreferencesKey("light_theme")
    }

    val settings: Flow<Settings> = store.data.map { p ->
        val d = Settings()
        Settings(
            glitchEffect = p[Keys.GLITCH] ?: d.glitchEffect,
            retroPostProcess = p[Keys.RETRO] ?: d.retroPostProcess,
            language = p[Keys.LANGUAGE] ?: d.language,
            autoSync = p[Keys.AUTO_SYNC] ?: d.autoSync,
            pulseNotifications = p[Keys.PULSE] ?: d.pulseNotifications,
            glitchIntensity = p[Keys.INTENSITY] ?: d.glitchIntensity,
            textSize = p[Keys.TEXT_SIZE] ?: d.textSize,
            lightTheme = p[Keys.LIGHT] ?: d.lightTheme,
        )
    }

    suspend fun setGlitch(v: Boolean) = store.edit { it[Keys.GLITCH] = v }
    suspend fun setRetro(v: Boolean) = store.edit { it[Keys.RETRO] = v }
    suspend fun setLanguage(v: String) = store.edit { it[Keys.LANGUAGE] = v }
    suspend fun setAutoSync(v: Boolean) = store.edit { it[Keys.AUTO_SYNC] = v }
    suspend fun setPulse(v: Boolean) = store.edit { it[Keys.PULSE] = v }
    suspend fun setGlitchIntensity(v: Float) = store.edit { it[Keys.INTENSITY] = v }
    suspend fun setTextSize(v: Int) = store.edit { it[Keys.TEXT_SIZE] = v }
    suspend fun setLightTheme(v: Boolean) = store.edit { it[Keys.LIGHT] = v }
    suspend fun clear() = store.edit { it.clear() }
}
