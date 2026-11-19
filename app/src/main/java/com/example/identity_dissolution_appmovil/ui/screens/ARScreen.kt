package com.example.identity_dissolution_appmovil.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.identity_dissolution_appmovil.ui.components.HorrorTopBar
import com.example.identity_dissolution_appmovil.ui.theme.Background
import com.example.identity_dissolution_appmovil.ui.theme.BloodRed
import com.example.identity_dissolution_appmovil.ui.theme.DarkRed
import com.example.identity_dissolution_appmovil.ui.theme.HorrorFont
import com.example.identity_dissolution_appmovil.ui.theme.TextMuted
import com.example.identity_dissolution_appmovil.viewmodel.Model3DViewModel
import com.example.identity_dissolution_appmovil.viewmodel.ModelState
import com.google.ar.core.Config
import com.google.ar.core.Frame
import com.google.ar.core.Plane
import com.google.ar.core.TrackingFailureReason
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes
import io.github.sceneview.rememberOnGestureListener
import java.nio.ByteBuffer

/** Realidad aumentada: el monstruo aparece en el piso real, visto por la cámara. */
@Composable
fun ARScreen(onBack: () -> Unit, viewModel: Model3DViewModel = viewModel()) {
    val monster by viewModel.monster.collectAsState()
    val model by viewModel.model.collectAsState()
    val context = LocalContext.current

    var hasCamera by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        )
    }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        hasCamera = it
    }
    LaunchedEffect(Unit) { if (!hasCamera) launcher.launch(Manifest.permission.CAMERA) }

    Column(Modifier.fillMaxSize()) {
        HorrorTopBar(monster?.name ?: "Realidad aumentada", onBack = onBack)
        Box(Modifier.fillMaxSize().background(Background), contentAlignment = Alignment.Center) {
            when {
                !hasCamera -> Column(Modifier.padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Necesitamos la cámara para invocar al monstruo en tu mundo.", color = TextMuted, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = { launcher.launch(Manifest.permission.CAMERA) },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkRed, contentColor = Color.White),
                    ) { Text("DAR PERMISO") }
                }
                else -> when (val m = model) {
                    ModelState.Loading -> CircularProgressIndicator(color = BloodRed)
                    is ModelState.Error -> Text(m.message, color = TextMuted, textAlign = TextAlign.Center)
                    is ModelState.Ready -> ARViewer(m.buffer)
                }
            }
        }
    }
}

@Composable
private fun ARViewer(buffer: ByteBuffer) {
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val nodes = rememberNodes()
    var frame by remember { mutableStateOf<Frame?>(null) }
    var hint by remember { mutableStateOf("Mueve el teléfono despacio para detectar el piso...") }

    Box(Modifier.fillMaxSize()) {
        ARScene(
            modifier = Modifier.fillMaxSize(),
            engine = engine,
            modelLoader = modelLoader,
            childNodes = nodes,
            planeRenderer = true,
            sessionConfiguration = { session, config ->
                // Detecta pisos (y mesas) y usa la luz real para iluminar el modelo.
                config.planeFindingMode = Config.PlaneFindingMode.HORIZONTAL
                config.lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
                config.depthMode =
                    if (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) Config.DepthMode.AUTOMATIC
                    else Config.DepthMode.DISABLED
            },
            onSessionUpdated = { _, updatedFrame ->
                frame = updatedFrame
                if (nodes.isEmpty()) {
                    val found = updatedFrame.getUpdatedTrackables(Plane::class.java)
                        .any { it.type == Plane.Type.HORIZONTAL_UPWARD_FACING }
                    if (found) hint = "Toca el piso para invocar al monstruo"
                }
            },
            onTrackingFailureChanged = { reason ->
                if (reason != null && reason != TrackingFailureReason.NONE) {
                    hint = "Busca más luz o mueve el teléfono más despacio"
                }
            },
            onGestureListener = rememberOnGestureListener(
                onSingleTapConfirmed = { event, _ ->
                    val hit = frame?.hitTest(event.x, event.y)?.firstOrNull {
                        val t = it.trackable
                        t is Plane && t.isPoseInPolygon(it.hitPose)
                    } ?: return@rememberOnGestureListener
                    // Un solo monstruo: al tocar otro punto, se mueve allí.
                    nodes.toList().forEach { it.destroy() }
                    nodes.clear()
                    val anchorNode = AnchorNode(engine, hit.createAnchor()).apply {
                        addChildNode(
                            ModelNode(
                                modelInstance = modelLoader.createModelInstance(buffer.duplicate().rewind()),
                                autoAnimate = true,
                                scaleToUnits = 0.8f, // ~80 cm de alto en el mundo real
                                centerOrigin = io.github.sceneview.math.Position(y = -1f), // pies en el piso
                            ).apply { isEditable = true } // pellizcar para escalar, girar con dos dedos
                        )
                    }
                    nodes += anchorNode
                    hint = "Toca otro punto para moverlo • Pellizca para cambiar tamaño"
                },
            ),
        )
        Text(
            hint,
            color = Color.White,
            fontFamily = HorrorFont,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(24.dp)
                .background(Color.Black.copy(alpha = 0.6f))
                .padding(12.dp),
        )
    }
}
