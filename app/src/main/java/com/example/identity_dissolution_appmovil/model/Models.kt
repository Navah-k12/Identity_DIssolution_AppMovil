package com.example.identity_dissolution_appmovil.model

import androidx.annotation.DrawableRes

enum class ArticleCategory(val label: String) {
    LORE("Lore"), CHARACTER("Personaje"), PLACE("Lugar"), MECHANIC("Mecánica"), ITEM("Objeto"), CONCEPT("Concepto")
}

data class Article(
    val id: Int,
    val category: ArticleCategory,
    val title: String,
    val summary: String,
    val body: String,
    val featured: Boolean = false,
    /** Captura del GDD (res/drawable-nodpi/gdd_*.webp). */
    @DrawableRes val image: Int? = null,
)

data class Enemy(
    val id: Int,
    val name: String,
    val enemyClass: String,
    val dangerLevel: String,
    val danger: Float,   // 0f..1f
    val speed: Float,    // 0f..1f
    val behavior: String,
    val strategy: String,
    val imageUrl: String? = null,
    /** null mientras el modelo 3D no esté subido al repositorio. */
    val modelUrl: String? = null,
)

data class InventoryItem(
    val id: Int,
    val name: String,
    val emoji: String,
    val quantity: Int,
    val description: String,
    /** Render del modelo 3D (res/drawable-nodpi). Si es null se usa el emoji. */
    @DrawableRes val image: Int? = null,
)

data class ControlBinding(val key: String, val action: String)

data class ControlSection(val title: String, val bindings: List<ControlBinding>)

data class StoryEvent(val year: String, val title: String, val description: String)

data class Settings(
    val glitchEffect: Boolean = true,
    val retroPostProcess: Boolean = false,
    val language: String = "Español",
    val autoSync: Boolean = true,
    val pulseNotifications: Boolean = true,
)
