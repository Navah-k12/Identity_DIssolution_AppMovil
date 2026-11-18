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
    /** Imagen remota (monstruos, desde la API). */
    val imageUrl: String? = null,
    /** Imagen local (artículos de la Guía). */
    @DrawableRes val imageRes: Int? = null,
    /** Altura de la cara dentro de la imagen (0 = arriba, 1 = abajo), para el recorte del retrato. */
    val faceY: Float = 0.35f,
    val route: String,
)

class HomeViewModel(app: Application) : AndroidViewModel(app) {

    private val reading = ReadingRepository.get(app)
    private val monsters = MonsterRepository.get(app)

    val continueReading: StateFlow<ContinueReading?> =
        combine(reading.lastRead, monsters.monsters) { last, list ->
            when (last) {
                is LastRead.Guide -> GameRepository.articles.find { it.id == last.id }?.let {
                    ContinueReading(
                        title = it.title,
                        subtitle = "Guía • ${it.category.label}",
                        imageRes = it.image,
                        faceY = 0.5f,
                        route = Routes.article(it.id),
                    )
                }
                // Sin lectura guardada se sugiere el primer monstruo.
                is LastRead.Monster, null -> {
                    val id = (last as? LastRead.Monster)?.id
                    val m = list.find { it.id == id } ?: list.firstOrNull()
                    val local = GameRepository.enemies.find { it.id == (m?.id ?: id) } ?: GameRepository.enemies.first()
                    ContinueReading(
                        title = m?.name ?: local.name,
                        subtitle = "Bestiario • Nivel de peligro: ${m?.dangerLevel ?: local.dangerLevel}",
                        imageUrl = m?.imageUrl,
                        faceY = FACE_Y[m?.id ?: local.id] ?: 0.35f,
                        route = Routes.bestiary(m?.id ?: local.id),
                    )
                }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    init {
        // Si el Bestiario aún no se abrió, Room está vacío: pedimos los monstruos a la API.
        viewModelScope.launch { monsters.refresh() }
    }

    private companion object {
        /** Dónde queda la cara en cada imagen cinematográfica (medido en los renders). */
        val FACE_Y = mapOf(1 to 0.34f, 2 to 0.33f, 3 to 0.30f, 4 to 0.52f)
    }
}
