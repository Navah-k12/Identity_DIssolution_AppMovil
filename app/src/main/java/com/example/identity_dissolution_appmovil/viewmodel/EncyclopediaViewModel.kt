package com.example.identity_dissolution_appmovil.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.identity_dissolution_appmovil.data.repository.GameRepository
import com.example.identity_dissolution_appmovil.model.Article
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class EncyclopediaViewModel : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    // La lista se recalcula sola cada vez que cambia la búsqueda.
    val results: StateFlow<List<Article>> = _query
        .map { q ->
            if (q.isBlank()) GameRepository.articles
            else GameRepository.articles.filter {
                it.title.contains(q, ignoreCase = true) ||
                    it.summary.contains(q, ignoreCase = true) ||
                    it.category.label.contains(q, ignoreCase = true)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), GameRepository.articles)

    fun onQueryChange(text: String) {
        _query.value = text
    }

    fun article(id: Int): Article? = GameRepository.articles.find { it.id == id }
}
