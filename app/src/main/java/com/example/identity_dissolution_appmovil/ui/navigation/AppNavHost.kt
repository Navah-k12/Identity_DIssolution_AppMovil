package com.example.identity_dissolution_appmovil.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.identity_dissolution_appmovil.data.repository.GameRepository
import com.example.identity_dissolution_appmovil.data.repository.ReadingRepository
import com.example.identity_dissolution_appmovil.ui.screens.ARScreen
import com.example.identity_dissolution_appmovil.ui.screens.ArticleDetailScreen
import com.example.identity_dissolution_appmovil.ui.screens.BestiaryScreen
import com.example.identity_dissolution_appmovil.ui.screens.ControlsScreen
import com.example.identity_dissolution_appmovil.ui.screens.EncyclopediaScreen
import com.example.identity_dissolution_appmovil.ui.screens.HistoryScreen
import com.example.identity_dissolution_appmovil.ui.screens.HomeScreen
import com.example.identity_dissolution_appmovil.ui.screens.InventoryScreen
import com.example.identity_dissolution_appmovil.ui.screens.Model3DScreen
import com.example.identity_dissolution_appmovil.ui.screens.MonsterDetailScreen
import com.example.identity_dissolution_appmovil.ui.screens.SettingsScreen
import com.example.identity_dissolution_appmovil.ui.screens.Welcome
import com.example.identity_dissolution_appmovil.ui.theme.Background
import com.example.identity_dissolution_appmovil.ui.theme.BloodRed
import com.example.identity_dissolution_appmovil.ui.theme.Border
import com.example.identity_dissolution_appmovil.ui.theme.DarkRed
import com.example.identity_dissolution_appmovil.ui.theme.Surface
import com.example.identity_dissolution_appmovil.ui.theme.TextMain
import com.example.identity_dissolution_appmovil.ui.theme.TextMuted

/** Pantallas que no son pestañas pero muestran la barra, y qué pestaña marcan. */
private val tabOf = mapOf(
    Routes.BESTIARY to BottomTab.HOME,
    Routes.MONSTER to BottomTab.HOME,
)

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route
    val selectedTab = BottomTab.entries.firstOrNull { it.route == currentRoute } ?: tabOf[currentRoute]

    // Insignia de la Guía: artículos nuevos que aún no se abren.
    val context = LocalContext.current
    val reading = remember { ReadingRepository.get(context) }
    val progress by reading.progress.collectAsState(initial = emptyMap())
    val newCount = GameRepository.articles.count { it.isNew && it.id !in progress }

    Scaffold(
        containerColor = Background,
        bottomBar = {
            if (selectedTab != null) {
                BottomBar(selectedTab, guideBadge = newCount) { navController.navigateToTab(it.route) }
            }
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
                EncyclopediaScreen(onArticleClick = { navController.navigate(Routes.article(it)) })
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
            composable(Routes.BESTIARY) {
                BestiaryScreen(
                    onBack = { navController.popBackStack() },
                    onMonsterClick = { navController.navigate(Routes.monster(it)) },
                )
            }
            composable(
                Routes.MONSTER,
                arguments = listOf(navArgument("id") { type = NavType.IntType }),
            ) { entry ->
                MonsterDetailScreen(
                    monsterId = entry.arguments?.getInt("id") ?: -1,
                    onBack = { navController.popBackStack() },
                    onSummon = { navController.navigate(Routes.ar(it)) },
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

/** Barra inferior del diseño: icono en una pastilla roja cuando está activo y etiqueta monoespaciada. */
@Composable
private fun BottomBar(selected: BottomTab, guideBadge: Int, onSelect: (BottomTab) -> Unit) {
    Column(Modifier.background(Surface)) {
        HorizontalDivider(color = Border)
        Row(Modifier.fillMaxWidth().navigationBarsPadding().height(68.dp)) {
            BottomTab.entries.forEach { tab ->
                val active = tab == selected
                Column(
                    Modifier
                        .weight(1f)
                        .clickable(remember { MutableInteractionSource() }, indication = null) { onSelect(tab) }
                        .padding(top = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box {
                        Box(
                            Modifier
                                .size(width = 56.dp, height = 30.dp)
                                .background(if (active) DarkRed.copy(alpha = 0.35f) else Surface, RoundedCornerShape(15.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(tab.icon, tab.label, tint = if (active) BloodRed else TextMuted, modifier = Modifier.size(22.dp))
                        }
                        if (tab == BottomTab.GUIDE && guideBadge > 0) {
                            Text(
                                "$guideBadge",
                                color = TextMain, fontSize = 9.sp, fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 6.dp, y = (-6).dp)
                                    .background(BloodRed, CircleShape)
                                    .padding(horizontal = 5.dp, vertical = 1.dp),
                            )
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        tab.label.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (active) BloodRed else TextMuted,
                    )
                }
            }
        }
    }
}
