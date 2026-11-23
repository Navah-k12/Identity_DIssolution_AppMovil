package com.example.identity_dissolution_appmovil.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.navigation.NavBackStackEntry
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import com.example.identity_dissolution_appmovil.ui.components.tr
import com.example.identity_dissolution_appmovil.ui.components.t
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
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
            // La barra sube despacio al salir de la pantalla de carga, en vez de aparecer de golpe.
            AnimatedVisibility(
                visible = selectedTab != null,
                enter = slideInVertically(tween(600, delayMillis = 250, easing = Emphasized)) { it } + fadeIn(tween(500, delayMillis = 250)),
                exit = fadeOut(tween(150)),
            ) {
                // Durante la salida selectedTab ya es null: se recuerda la última pestaña.
                var lastTab by remember { mutableStateOf(BottomTab.HOME) }
                if (selectedTab != null) lastTab = selectedTab
                BottomBar(lastTab, guideBadge = newCount) { navController.navigateToTab(it.route) }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.WELCOME,
            modifier = Modifier.padding(padding),
            // Animación tipo Material "shared axis": al avanzar, la nueva pantalla entra deslizándose un poco
            // desde la derecha mientras la vieja se aparta hacia la izquierda; al regresar, al revés.
            // Entre pestañas de la barra inferior solo hay un fundido corto (no hay "adelante" ni "atrás").
            enterTransition = { if (fromWelcome()) welcomeIn else if (isTabSwitch()) tabIn else slideFadeIn(forward = true) },
            exitTransition = { if (fromWelcome()) welcomeOut else if (isTabSwitch()) tabOut else slideFadeOut(forward = true) },
            popEnterTransition = { if (isTabSwitch()) tabIn else slideFadeIn(forward = false) },
            popExitTransition = { if (isTabSwitch()) tabOut else slideFadeOut(forward = false) },
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
                    onBack = { navController.safePopBackStack() },
                )
            }
            composable(Routes.BESTIARY) {
                BestiaryScreen(
                    onBack = { navController.safePopBackStack() },
                    onMonsterClick = { navController.navigate(Routes.monster(it)) },
                )
            }
            composable(
                Routes.MONSTER,
                arguments = listOf(navArgument("id") { type = NavType.IntType }),
            ) { entry ->
                MonsterDetailScreen(
                    monsterId = entry.arguments?.getInt("id") ?: -1,
                    onBack = { navController.safePopBackStack() },
                    onSummon = { navController.navigate(Routes.ar(it)) },
                )
            }
            composable(
                Routes.MODEL_3D,
                arguments = listOf(navArgument("id") { type = NavType.IntType }),
            ) {
                // Model3DViewModel lee el "id" de la ruta con SavedStateHandle.
                Model3DScreen(
                    onBack = { navController.safePopBackStack() },
                    onOpenAR = { navController.navigate(Routes.ar(it)) },
                )
            }
            composable(
                Routes.AR,
                arguments = listOf(navArgument("id") { type = NavType.IntType }),
            ) {
                // Reutiliza Model3DViewModel: también lee el "id" de la ruta.
                ARScreen(onBack = { navController.safePopBackStack() })
            }
            composable(Routes.CONTROLS) { ControlsScreen(onBack = { navController.safePopBackStack() }) }
            composable(Routes.HISTORY) { HistoryScreen(onBack = { navController.safePopBackStack() }) }
            composable(Routes.INVENTORY) { InventoryScreen() }
            composable(Routes.SETTINGS) { SettingsScreen() }
        }
    }
}

// ---------- Transiciones ----------
private const val NAV_MS = 320
// Curva "emphasized" de Material 3: arranca rápido y frena suave, como un movimiento real.
private val Emphasized = CubicBezierEasing(0.2f, 0f, 0f, 1f)
private val tabRoutes = BottomTab.entries.map { it.route }.toSet() + Routes.WELCOME

private fun AnimatedContentTransitionScope<NavBackStackEntry>.fromWelcome() =
    initialState.destination.route == Routes.WELCOME

/**
 * Pantalla de carga → Inicio: la carga se desvanece despacio y se acerca un poco, como si entraras en ella;
 * Inicio aparece desde un poco más lejos. Más lento que el resto (700 ms) porque es la entrada a la app.
 */
private val welcomeOut = fadeOut(tween(550, easing = FastOutLinearInEasing)) +
    scaleOut(tween(700, easing = Emphasized), targetScale = 1.08f)
private val welcomeIn = fadeIn(tween(600, delayMillis = 200, easing = LinearOutSlowInEasing)) +
    scaleIn(tween(800, delayMillis = 200, easing = Emphasized), initialScale = 0.94f)

private fun AnimatedContentTransitionScope<NavBackStackEntry>.isTabSwitch() =
    initialState.destination.route in tabRoutes && targetState.destination.route in tabRoutes

/** Entra desplazándose un 8 % del ancho y apareciendo. */
private fun slideFadeIn(forward: Boolean): EnterTransition =
    slideInHorizontally(tween(NAV_MS, easing = Emphasized)) { w -> if (forward) w / 12 else -w / 12 } +
        fadeIn(tween(NAV_MS / 2, delayMillis = NAV_MS / 6, easing = LinearOutSlowInEasing))

/** Sale desplazándose al lado contrario y desapareciendo rápido, para no encimarse con la nueva. */
private fun slideFadeOut(forward: Boolean): ExitTransition =
    slideOutHorizontally(tween(NAV_MS, easing = Emphasized)) { w -> if (forward) -w / 12 else w / 12 } +
        fadeOut(tween(NAV_MS / 3, easing = FastOutLinearInEasing))

private val tabIn = fadeIn(tween(220, delayMillis = 60)) + scaleIn(tween(220, delayMillis = 60), initialScale = 0.98f)
private val tabOut = fadeOut(tween(90))

/**
 * Retrocede solo si la pantalla actual está activa. Mientras dura la animación de salida,
 * la pantalla ya no está RESUMED, así que los toques repetidos se ignoran.
 * Sin esto, varios toques sacaban también Inicio de la pila y el NavHost quedaba vacío (pantalla negra).
 */
private fun NavHostController.safePopBackStack() {
    if (currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED && previousBackStackEntry != null) {
        popBackStack()
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
                            Icon(tab.icon, tr(tab.label), tint = if (active) BloodRed else TextMuted, modifier = Modifier.size(22.dp))
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
                        tr(tab.label).uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (active) BloodRed else TextMuted,
                    )
                }
            }
        }
    }
}
