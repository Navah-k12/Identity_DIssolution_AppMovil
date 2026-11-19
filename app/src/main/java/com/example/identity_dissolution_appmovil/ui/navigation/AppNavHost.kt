package com.example.identity_dissolution_appmovil.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.identity_dissolution_appmovil.ui.screens.ARScreen
import com.example.identity_dissolution_appmovil.ui.screens.ArticleDetailScreen
import com.example.identity_dissolution_appmovil.ui.screens.BestiaryScreen
import com.example.identity_dissolution_appmovil.ui.screens.ControlsScreen
import com.example.identity_dissolution_appmovil.ui.screens.EncyclopediaScreen
import com.example.identity_dissolution_appmovil.ui.screens.HistoryScreen
import com.example.identity_dissolution_appmovil.ui.screens.HomeScreen
import com.example.identity_dissolution_appmovil.ui.screens.InventoryScreen
import com.example.identity_dissolution_appmovil.ui.screens.Model3DScreen
import com.example.identity_dissolution_appmovil.ui.screens.SettingsScreen
import com.example.identity_dissolution_appmovil.ui.screens.Welcome
import com.example.identity_dissolution_appmovil.ui.theme.Background
import com.example.identity_dissolution_appmovil.ui.theme.BloodRed
import com.example.identity_dissolution_appmovil.ui.theme.DarkRed
import com.example.identity_dissolution_appmovil.ui.theme.Surface
import com.example.identity_dissolution_appmovil.ui.theme.TextMuted

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route
    val showBottomBar = BottomTab.entries.any { it.route == currentRoute }

    Scaffold(
        containerColor = Background,
        bottomBar = {
            if (showBottomBar) BottomBar(currentRoute) { route -> navController.navigateToTab(route) }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.WELCOME,
            modifier = Modifier.padding(padding),
        ) {
            composable(Routes.WELCOME) {
                Welcome(onStart = {
                    // Quitamos la bienvenida de la pila: "atrás" desde Inicio cierra la app.
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.WELCOME) { inclusive = true }
                    }
                })
            }
            composable(Routes.HOME) {
                HomeScreen(onNavigate = { navController.navigate(it) })
            }
            composable(Routes.ENCYCLOPEDIA) {
                EncyclopediaScreen(
                    onBack = { navController.popBackStack() },
                    onArticleClick = { navController.navigate(Routes.article(it)) },
                )
            }
            composable(
                Routes.ARTICLE,
                arguments = listOf(navArgument("id") { type = NavType.IntType }),
            ) { entry ->
                ArticleDetailScreen(
                    articleId = entry.arguments?.getInt("id") ?: -1,
                    onBack = { navController.popBackStack() },
                )
            }
            composable(
                Routes.BESTIARY_PATTERN,
                arguments = listOf(navArgument("focus") { type = NavType.IntType; defaultValue = -1 }),
            ) { entry ->
                BestiaryScreen(
                    focusId = entry.arguments?.getInt("focus") ?: -1,
                    onBack = { navController.popBackStack() },
                    onView3D = { navController.navigate(Routes.model3d(it)) },
                )
            }
            composable(
                Routes.MODEL_3D,
                arguments = listOf(navArgument("id") { type = NavType.IntType }),
            ) {
                // Model3DViewModel lee el "id" de la ruta con SavedStateHandle.
                Model3DScreen(
                    onBack = { navController.popBackStack() },
                    onOpenAR = { navController.navigate(Routes.ar(it)) },
                )
            }
            composable(
                Routes.AR,
                arguments = listOf(navArgument("id") { type = NavType.IntType }),
            ) {
                // Reutiliza Model3DViewModel: también lee el "id" de la ruta.
                ARScreen(onBack = { navController.popBackStack() })
            }
            composable(Routes.CONTROLS) { ControlsScreen(onBack = { navController.popBackStack() }) }
            composable(Routes.HISTORY) { HistoryScreen(onBack = { navController.popBackStack() }) }
            composable(Routes.INVENTORY) { InventoryScreen() }
            composable(Routes.SETTINGS) { SettingsScreen() }
        }
    }
}

private fun NavHostController.navigateToTab(route: String) {
    navigate(route) {
        // Patrón estándar de pestañas: no apilar copias y recordar el estado de cada una.
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
private fun BottomBar(currentRoute: String?, onSelect: (String) -> Unit) {
    NavigationBar(containerColor = Surface) {
        BottomTab.entries.forEach { tab ->
            NavigationBarItem(
                selected = currentRoute == tab.route,
                onClick = { onSelect(tab.route) },
                icon = { Icon(tab.icon, contentDescription = tab.label) },
                label = { Text(tab.label.uppercase()) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = BloodRed,
                    selectedTextColor = BloodRed,
                    indicatorColor = DarkRed.copy(alpha = 0.35f),
                    unselectedIconColor = TextMuted,
                    unselectedTextColor = TextMuted,
                ),
            )
        }
    }
}
