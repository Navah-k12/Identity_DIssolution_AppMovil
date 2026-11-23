package com.example.identity_dissolution_appmovil.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.identity_dissolution_appmovil.data.repository.Review
import com.example.identity_dissolution_appmovil.data.repository.ReviewsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ReviewsUiState(
    val available: Boolean = true,
    val loading: Boolean = true,
    /** Todos los comentarios: de aquí salen las estadísticas. */
    val all: List<Review> = emptyList(),
    /** Lo que se muestra: resultado del filtro de estrellas (servidor) + búsqueda (texto). */
    val shown: List<Review> = emptyList(),
    val starFilter: Int? = null,
    val query: String = "",
    val myUid: String? = null,
    val error: String? = null,
    val saving: Boolean = false,
) {
    /** Estadísticas: promedio y cuántos hay de cada estrella (índice 0 = 1 estrella). */
    val average: Float get() = if (all.isEmpty()) 0f else all.sumOf { it.stars }.toFloat() / all.size
    val distribution: List<Int> get() = (1..5).map { s -> all.count { it.stars == s } }
}

class ReviewsViewModel : ViewModel() {

    private val repo = ReviewsRepository()
    private var filtered: List<Review> = emptyList()

    private val _state = MutableStateFlow(ReviewsUiState(available = repo.available, myUid = repo.currentUid))
    val state: StateFlow<ReviewsUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    /** GET de todo (para estadísticas) y, si hay filtro, GET filtrado en el servidor. */
    fun refresh() {
        _state.update { it.copy(myUid = repo.currentUid) }
        if (!repo.available) return
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null, myUid = repo.currentUid) }
            runCatching {
                val all = repo.getAll()
                val stars = _state.value.starFilter
                filtered = if (stars == null) all else repo.getByStars(stars)
                all
            }.onSuccess { all -> _state.update { it.copy(loading = false, all = all, shown = search(filtered, it.query)) } }
                .onFailure { e -> _state.update { it.copy(loading = false, error = e.message) } }
        }
    }

    fun setStarFilter(stars: Int?) {
        _state.update { it.copy(starFilter = stars) }
        refresh()
    }

    /** Búsqueda por texto o autor (sobre el resultado ya filtrado). */
    fun setQuery(q: String) = _state.update { it.copy(query = q, shown = search(filtered, q)) }

    /** POST si [id] es null, PATCH si es un comentario existente. */
    fun save(id: String?, stars: Int, text: String, onDone: () -> Unit) {
        if (text.isBlank()) {
            _state.update { it.copy(error = "Escribe tu comentario.") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(saving = true, error = null) }
            runCatching { if (id == null) repo.create(stars, text) else repo.update(id, stars, text) }
                .onSuccess { onDone(); refresh() }
                .onFailure { e -> _state.update { it.copy(error = e.message) } }
            _state.update { it.copy(saving = false) }
        }
    }

    /** DELETE */
    fun delete(id: String) {
        viewModelScope.launch {
            runCatching { repo.delete(id) }
                .onSuccess { refresh() }
                .onFailure { e -> _state.update { it.copy(error = e.message) } }
        }
    }

    fun clearError() = _state.update { it.copy(error = null) }

    private fun search(list: List<Review>, q: String) =
        if (q.isBlank()) list else list.filter { it.text.contains(q, true) || it.author.contains(q, true) }
}
