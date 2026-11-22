package com.example.identity_dissolution_appmovil.viewmodel

import android.app.Application
import androidx.annotation.DrawableRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.identity_dissolution_appmovil.data.repository.GameRepository
import com.example.identity_dissolution_appmovil.data.repository.LastRead
import com.example.identity_dissolution_appmovil.data.repository.MonsterRepository
import com.example.identity_dissolution_appmovil.data.repository.ReadingRepository
import com.example.identity_dissolution_appmovil.ui.navigation.Routes
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Lo que muestra la tarjeta "Continuar lectura". */
data class ContinueReading(
    val title: String,
    val subtitle: String,
    @DrawableRes val imageRes: Int? = null,
    /** null si no aplica (monstruos). */
    val progress: Float? = null,
    val route: String,
)

data class HomeUiState(
    val reading: ContinueReading? = null,
    val filesRead: Int = 0,
    val filesTotal: Int = GameRepository.articles.size,
    /** Primer artículo sin terminar, para "Siguiente: ...". */
    val nextTitle: String? = GameRepository.articles.firstOrNull()?.title,
)

class HomeViewModel(app: Application) : AndroidViewModel(app) {

    private val reading = ReadingRepository.get(app)
    private val monsters = MonsterRepository.get(app)

    val state: StateFlow<HomeUiState> =
        combine(reading.lastRead, reading.progress, monsters.monsters) { last, progress, list ->
            val articles = GameRepository.articles
            HomeUiState(
                reading = continueCard(last, progress, list.map { it.id to it.name }.toMap()),
                filesRead = articles.count { (progress[it.id] ?: 0f) >= READ_THRESHOLD },
                filesTotal = articles.size,
                nextTitle = articles.firstOrNull { (progress[it.id] ?: 0f) < READ_THRESHOLD }?.title,
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    private fun continueCard(last: LastRead?, progress: Map<Int, Float>, names: Map<Int, String>): ContinueReading? =
        when (last) {
            is LastRead.Monster -> {
                val local = GameRepository.enemies.find { it.id == last.id }
                ContinueReading(
                    title = names[last.id] ?: local?.name ?: "Registro",
                    subtitle = "Bestiario · Peligro ${local?.dangerLevel ?: "?"}",
                    imageRes = GameRepository.monsterArt[last.id],
                    route = Routes.monster(last.id),
                )
            }
            // Sin lectura guardada se sugiere el primer artículo de la Guía.
            is LastRead.Guide, null -> {
                val id = (last as? LastRead.Guide)?.id
                val article = GameRepository.articles.find { it.id == id } ?: GameRepository.articles.firstOrNull()
                article?.let {
                    val p = progress[it.id] ?: 0f
                    val left = (it.readMinutes * (1f - p)).toInt().coerceAtLeast(1)
                    ContinueReading(
                        title = it.title,
                        subtitle = "Guía · ${it.category.label} · $left min restantes",
                        imageRes = it.image,
                        progress = p,
                        route = Routes.article(it.id),
                    )
                }
            }
        }

    init {
        // Si el Bestiario aún no se abrió, Room está vacío: pedimos los monstruos a la API.
        viewModelScope.launch { monsters.refresh() }
    }

    companion object {
        /** A partir de este progreso un artículo cuenta como leído. */
        const val READ_THRESHOLD = 0.95f
    }
}
