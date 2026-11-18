package com.example.identity_dissolution_appmovil.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.vector.ImageVector

object Routes {
    const val WELCOME = "welcome"
    const val HOME = "home"
    const val ENCYCLOPEDIA = "encyclopedia"
    const val ARTICLE = "article/{id}"
    const val BESTIARY = "bestiary"
    const val MODEL_3D = "model3d/{id}"
    const val CONTROLS = "controls"
    const val INVENTORY = "inventory"
    const val HISTORY = "history"
    const val SETTINGS = "settings"

    fun article(id: Int) = "article/$id"
    fun model3d(id: Int) = "model3d/$id"

    /** Bestiario con un monstruo a la vista (desde "Continuar lectura"). */
    const val BESTIARY_PATTERN = "bestiary?focus={focus}"
    fun bestiary(focusId: Int) = "bestiary?focus=$focusId"
}

/** Pestañas de la barra inferior. */
enum class BottomTab(val route: String, val label: String, val icon: ImageVector) {
    HOME(Routes.HOME, "Inicio", Icons.Filled.Home),
    GUIDE(Routes.ENCYCLOPEDIA, "Guía", Icons.AutoMirrored.Filled.MenuBook),
    INVENTORY(Routes.INVENTORY, "Inv", Icons.Filled.Work),
    SETTINGS(Routes.SETTINGS, "Ajustes", Icons.Filled.Settings),
}
