package com.example.identity_dissolution_appmovil.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.identity_dissolution_appmovil.data.repository.LastRead
import com.example.identity_dissolution_appmovil.data.repository.MonsterRepository
import com.example.identity_dissolution_appmovil.data.repository.ReadingRepository
import com.example.identity_dissolution_appmovil.model.Enemy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.nio.ByteBuffer

data class BestiaryUiState(
    val monsters: List<Enemy> = emptyList(),
    val isLoading: Boolean = false,
    /** Mensaje si falló la API. Si hay copia local, se siguen mostrando los datos guardados. */
    val error: String? = null,
)

class BestiaryViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = MonsterRepository.get(app)
    private val reading = ReadingRepository.get(app)
    private val isLoading = MutableStateFlow(false)
    private val error = MutableStateFlow<String?>(null)

    val state: StateFlow<BestiaryUiState> =
        combine(repo.monsters, isLoading, error) { monsters, loading, err ->
            BestiaryUiState(monsters, loading, err)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), BestiaryUiState(isLoading = true))

    init { refresh() }

    fun markRead(monsterId: Int) {
        viewModelScope.launch { reading.save(LastRead.Monster(monsterId)) }
    }

    fun refresh() {
        if (isLoading.value) return
        viewModelScope.launch {
            isLoading.value = true
            error.value = repo.refresh().exceptionOrNull()?.let {
                "No se pudo conectar con la API. ${it.message ?: ""}".trim()
            }
            isLoading.value = false
        }
    }
}

sealed interface ModelState {
    data object Loading : ModelState
    data class Ready(val buffer: ByteBuffer) : ModelState
    data class Error(val message: String) : ModelState
}

/** Descarga el modelo 3D de un monstruo. El id llega por la ruta de navegación. */
class Model3DViewModel(app: Application, savedState: SavedStateHandle) : AndroidViewModel(app) {

    private val repo = MonsterRepository.get(app)
    private val id: Int = checkNotNull(savedState["id"])

    val monster = MutableStateFlow<Enemy?>(null)
    val model = MutableStateFlow<ModelState>(ModelState.Loading)

    init { load() }

    fun load() {
        viewModelScope.launch {
            model.value = ModelState.Loading
            val enemy = repo.monster(id).also { monster.value = it }
            val url = enemy?.modelUrl
            model.value = if (url == null) {
                ModelState.Error("Este monstruo aún no tiene modelo 3D.")
            } else {
                runCatching { repo.loadModel(id, url) }.fold(
                    onSuccess = { ModelState.Ready(it) },
                    onFailure = { ModelState.Error("No se pudo descargar el modelo: ${it.message}") },
                )
            }
        }
    }
}
