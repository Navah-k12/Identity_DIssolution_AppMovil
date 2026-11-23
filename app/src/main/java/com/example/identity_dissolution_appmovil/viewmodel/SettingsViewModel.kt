package com.example.identity_dissolution_appmovil.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.identity_dissolution_appmovil.data.repository.SettingsRepository
import com.example.identity_dissolution_appmovil.model.Settings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// AndroidViewModel nos da el Application (un Context seguro) para crear el DataStore.
class SettingsViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = SettingsRepository(app)

    // La UI solo lee este StateFlow; la verdad vive en DataStore.
    val state: StateFlow<Settings> = repo.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), Settings())

    private fun save(block: suspend SettingsRepository.() -> Unit) {
        viewModelScope.launch { repo.block() }
    }

    fun setGlitch(v: Boolean) = save { setGlitch(v) }
    fun setRetro(v: Boolean) = save { setRetro(v) }
    fun setAutoSync(v: Boolean) = save { setAutoSync(v) }
    fun setPulseNotifications(v: Boolean) = save { setPulse(v) }
    fun toggleLanguage() = save {
        setLanguage(if (state.value.language == "Español") "English" else "Español")
    }
    fun setGlitchIntensity(v: Float) = save { setGlitchIntensity(v) }
    fun setTextSize(v: Int) = save { setTextSize(v) }
    fun reset() = save { clear() }
}
