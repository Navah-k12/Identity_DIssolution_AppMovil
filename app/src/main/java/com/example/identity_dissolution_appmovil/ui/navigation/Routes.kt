package com.example.identity_dissolution_appmovil.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.ui.graphics.vector.ImageVector

object Routes {
    const val WELCOME = "welcome"
    const val HOME = "home"
    const val ENCYCLOPEDIA = "encyclopedia"
    const val ARTICLE = "article/{id}"
    const val BESTIARY = "bestiary"
    const val MONSTER = "monster/{id}"
    const val MODEL_3D = "model3d/{id}"
    const val AR = "ar/{id}"
    const val CONTROLS = "controls"
    const val INVENTORY = "inventory"
    const val HISTORY = "history"
    const val SETTINGS = "settings"

    fun article(id: Int) = "article/$id"
    fun model3d(id: Int) = "model3d/$id"
    fun ar(id: Int) = "ar/$id"
    fun monster(id: Int) = "monster/$id"

}

/** Pestañas de la barra inferior. */
enum class BottomTab(val route: String, val label: String, val icon: ImageVector) {
    HOME(Routes.HOME, "Inicio", Icons.Outlined.Home),
    GUIDE(Routes.ENCYCLOPEDIA, "Guía", Icons.AutoMirrored.Outlined.MenuBook),
    INVENTORY(Routes.INVENTORY, "Inventario", Icons.Outlined.WorkOutline),
    SETTINGS(Routes.SETTINGS, "Ajustes", Icons.Outlined.Tune),
}
