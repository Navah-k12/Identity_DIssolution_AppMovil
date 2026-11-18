package com.example.identity_dissolution_appmovil

import android.os.Build
import android.os.Bundle
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.gif.AnimatedImageDecoder
import coil3.gif.GifDecoder
import coil3.request.crossfade
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.identity_dissolution_appmovil.ui.components.LocalVisualEffects
import com.example.identity_dissolution_appmovil.ui.components.VisualEffects
import com.example.identity_dissolution_appmovil.ui.components.retroScanlines
import com.example.identity_dissolution_appmovil.ui.navigation.AppNavHost
import com.example.identity_dissolution_appmovil.ui.theme.Identity_DIssolution_AppMovilTheme
import com.example.identity_dissolution_appmovil.viewmodel.SettingsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Iconos de la barra de estado en blanco, porque la app siempre es oscura.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
        )
        setContent {
            // Coil con soporte para imágenes animadas (WebP de los monstruos en el Bestiario).
            setSingletonImageLoaderFactory { context ->
                ImageLoader.Builder(context)
                    .components {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) add(AnimatedImageDecoder.Factory())
                        else add(GifDecoder.Factory())
                    }
                    .crossfade(true)
                    .build()
            }
            // Los ajustes se leen aquí arriba para aplicar los efectos a toda la app.
            val settingsViewModel: SettingsViewModel = viewModel()
            val settings by settingsViewModel.state.collectAsState()

            Identity_DIssolution_AppMovilTheme {
                CompositionLocalProvider(LocalVisualEffects provides VisualEffects(glitch = settings.glitchEffect)) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .retroScanlines(settings.retroPostProcess)
                    ) {
                        AppNavHost()
                    }
                }
            }
        }
    }
}
