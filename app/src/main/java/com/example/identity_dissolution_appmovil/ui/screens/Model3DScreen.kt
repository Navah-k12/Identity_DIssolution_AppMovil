package com.example.identity_dissolution_appmovil.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.identity_dissolution_appmovil.ui.components.HorrorTopBar
import com.example.identity_dissolution_appmovil.ui.theme.Background
import com.example.identity_dissolution_appmovil.ui.theme.BloodRed
import com.example.identity_dissolution_appmovil.ui.theme.DarkRed
import com.example.identity_dissolution_appmovil.ui.theme.HorrorFont
import com.example.identity_dissolution_appmovil.ui.theme.TextMuted
import com.example.identity_dissolution_appmovil.viewmodel.Model3DViewModel
import com.example.identity_dissolution_appmovil.viewmodel.ModelState
import dev.romainguy.kotlin.math.Float3
import io.github.sceneview.Scene
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberCameraManipulator
import io.github.sceneview.rememberCameraNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes
import java.nio.ByteBuffer

/** Visor 3D: el usuario gira el modelo con un dedo y hace zoom con dos. */
@Composable
fun Model3DScreen(onBack: () -> Unit, onOpenAR: (Int) -> Unit, viewModel: Model3DViewModel = viewModel()) {
    val monster by viewModel.monster.collectAsState()
    val model by viewModel.model.collectAsState()

    Column(Modifier.fillMaxSize()) {
        HorrorTopBar(monster?.name ?: "Modelo 3D", onBack = onBack)

        Box(Modifier.fillMaxSize().background(Background), contentAlignment = Alignment.Center) {
            when (val m = model) {
                ModelState.Loading -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = BloodRed)
                    Spacer(Modifier.height(12.dp))
                    Text("Invocando al monstruo...", color = TextMuted, fontFamily = HorrorFont)
                }
                is ModelState.Error -> Column(
                    Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(m.message, color = TextMuted, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = viewModel::load,
                        colors = ButtonDefaults.buttonColors(containerColor = DarkRed, contentColor = Color.White),
                    ) { Text("REINTENTAR") }
                }
                is ModelState.Ready -> {
                    ModelViewer(m.buffer)
                    Button(
                        onClick = { monster?.let { onOpenAR(it.id) } },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkRed, contentColor = Color.White),
                        modifier = Modifier.align(Alignment.TopEnd).padding(16.dp),
                    ) { Text("VER EN TU MUNDO (AR)") }
                    Text(
                        "Arrastra para girar • Pellizca para acercar",
                        color = TextMuted,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.align(Alignment.BottomCenter).padding(24.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun ModelViewer(buffer: ByteBuffer) {
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    // El modelo se escala a 1 unidad y se centra para que siempre quepa en pantalla.
    val nodes = rememberNodes {
        add(
            ModelNode(
                modelInstance = modelLoader.createModelInstance(buffer),
                autoAnimate = true,
                scaleToUnits = 1.0f,
                centerOrigin = Float3(0f, 0f, 0f),
            )
        )
    }
    // Cámara a 3.2 unidades del modelo (que mide 1 unidad), mirando a su centro.
    val cameraNode = rememberCameraNode(engine) {
        position = Float3(0f, 0f, 3.2f)
        lookAt(Float3(0f, 0f, 0f))
    }
    Scene(
        modifier = Modifier.fillMaxSize(),
        engine = engine,
        modelLoader = modelLoader,
        childNodes = nodes,
        cameraNode = cameraNode,
        // La cámara orbita alrededor del centro del modelo.
        cameraManipulator = rememberCameraManipulator(
            orbitHomePosition = cameraNode.worldPosition,
            targetPosition = Float3(0f, 0f, 0f),
        ),
    )
}
