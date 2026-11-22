package com.example.identity_dissolution_appmovil.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.identity_dissolution_appmovil.data.repository.GameRepository
import com.example.identity_dissolution_appmovil.model.Enemy
import com.example.identity_dissolution_appmovil.ui.components.CircleIconButton
import com.example.identity_dissolution_appmovil.ui.components.Tag
import com.example.identity_dissolution_appmovil.ui.theme.Background
import com.example.identity_dissolution_appmovil.ui.theme.BloodRed
import com.example.identity_dissolution_appmovil.ui.theme.DarkRed
import com.example.identity_dissolution_appmovil.ui.theme.HorrorFont
import com.example.identity_dissolution_appmovil.ui.theme.OnRed
import com.example.identity_dissolution_appmovil.ui.theme.SoftRed
import com.example.identity_dissolution_appmovil.ui.theme.TextMain
import com.example.identity_dissolution_appmovil.ui.theme.TextMuted
import com.example.identity_dissolution_appmovil.viewmodel.Model3DViewModel
import com.example.identity_dissolution_appmovil.viewmodel.ModelState
import com.google.ar.core.ArCoreApk
import com.google.ar.core.Config
import com.google.ar.core.Frame
import com.google.ar.core.Plane
import com.google.ar.core.TrackingFailureReason
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.math.Position
import io.github.sceneview.math.Size as Size3
import io.github.sceneview.node.ImageNode
import io.github.sceneview.node.ModelNode
import io.github.sceneview.node.Node
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes
import io.github.sceneview.rememberOnGestureListener
import kotlinx.coroutines.delay
import java.nio.ByteBuffer

/** Cómo se puede mostrar al monstruo en este teléfono. */
private enum class ArSupport { CHECKING, SUPPORTED, UNSUPPORTED }

/**
 * "Invocar monstruo": como en Pokémon GO, el monstruo aparece en tu mundo real a través de la cámara.
 * - Con ARCore: se coloca sobre el piso detectado. Usa el modelo 3D si existe; si no, su figura recortada.
 * - Sin ARCore: cámara normal + giroscopio (AR clásico, ver GyroSummon.kt).
 * - Sin permiso de cámara: modo sin AR, con el monstruo sobre un fondo del laboratorio.
 */
@Composable
fun ARScreen(onBack: () -> Unit, viewModel: Model3DViewModel = viewModel()) {
    val monster by viewModel.monster.collectAsState()
    val model by viewModel.model.collectAsState()
    val context = LocalContext.current
    val enemy = monster ?: GameRepository.enemies.find { it.id == viewModel.monsterId }

    var hasCamera by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        )
    }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        hasCamera = it
    }

    // ARCore responde "comprobando" al principio: se pregunta de nuevo hasta tener respuesta.
    var support by remember { mutableStateOf(ArSupport.CHECKING) }
    LaunchedEffect(Unit) {
        while (support == ArSupport.CHECKING) {
            val a = ArCoreApk.getInstance().checkAvailability(context)
            support = when {
                a.isTransient -> ArSupport.CHECKING
                a.isSupported -> ArSupport.SUPPORTED
                else -> ArSupport.UNSUPPORTED
            }
            if (support == ArSupport.CHECKING) delay(200)
        }
    }
    // Los dos modos con cámara (ARCore y giroscopio) necesitan el permiso.
    LaunchedEffect(support) {
        if (support != ArSupport.CHECKING && !hasCamera) launcher.launch(Manifest.permission.CAMERA)
    }

    Box(Modifier.fillMaxSize().background(Color.Black)) {
        when {
            enemy == null || support == ArSupport.CHECKING ->
                CircularProgressIndicator(color = BloodRed, modifier = Modifier.align(Alignment.Center))
            // Sin ARCore: cámara normal + giroscopio. Sin permiso de cámara: modo sin AR.
            support == ArSupport.UNSUPPORTED && !hasCamera -> NoArSummon(enemy)
            // Se espera a saber si hay modelo 3D antes de abrir la cámara.
            model == ModelState.Loading -> CircularProgressIndicator(color = BloodRed, modifier = Modifier.align(Alignment.Center))
            support == ArSupport.UNSUPPORTED -> GyroSummon(enemy, (model as? ModelState.Ready)?.buffer)
            !hasCamera -> CameraPermission { launcher.launch(Manifest.permission.CAMERA) }
            // Sin modelo 3D todavía: se espera a saberlo antes de abrir la cámara.
            model == ModelState.Loading -> CircularProgressIndicator(color = BloodRed, modifier = Modifier.align(Alignment.Center))
            else -> ArSummon(enemy, (model as? ModelState.Ready)?.buffer)
        }

        // Barra superior sobre la cámara.
        Row(
            Modifier.fillMaxWidth().statusBarsPadding().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CircleIconButton(Icons.AutoMirrored.Filled.ArrowBack, "Volver", onBack)
            Spacer(Modifier.width(12.dp))
            Column {
                Text("INVOCACIÓN", style = MaterialTheme.typography.labelSmall, color = SoftRed)
                Text(
                    (enemy?.name ?: "").uppercase(),
                    style = MaterialTheme.typography.titleLarge, color = TextMain,
                )
            }
        }
    }
}

@Composable
private fun CameraPermission(onAsk: () -> Unit) {
    Column(
        Modifier.fillMaxSize().background(Background).padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
    ) {
        Text(
            "Necesitamos la cámara para invocar al monstruo en tu mundo.",
            color = TextMuted, textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onAsk,
            colors = ButtonDefaults.buttonColors(containerColor = BloodRed, contentColor = OnRed),
        ) { Text("DAR PERMISO", fontFamily = HorrorFont, fontWeight = FontWeight.Bold) }
    }
}

/** Botón grande y redondo, como la Poké Ball: se activa cuando hay piso a la vista. */
@Composable
internal fun SummonButton(enabled: Boolean, label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val pulse by rememberInfiniteTransition(label = "pulse").animateFloat(
        1f, 1.18f, infiniteRepeatable(tween(900, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "ring",
    )
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center) {
            if (enabled) {
                Box(
                    Modifier.size(84.dp).graphicsLayer { scaleX = pulse; scaleY = pulse }
                        .border(2.dp, BloodRed.copy(alpha = 0.5f), CircleShape)
                )
            }
            Box(
                Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(if (enabled) BloodRed else DarkRed.copy(alpha = 0.5f))
                    .border(4.dp, TextMain.copy(alpha = if (enabled) 0.9f else 0.3f), CircleShape)
                    .clickable(enabled = enabled, onClick = onClick),
                contentAlignment = Alignment.Center,
            ) {
                Box(Modifier.size(20.dp).background(TextMain, CircleShape).border(3.dp, Background, CircleShape))
            }
        }
        Spacer(Modifier.height(10.dp))
        Text(
            label, fontFamily = HorrorFont, fontWeight = FontWeight.Bold, color = TextMain,
            modifier = Modifier.background(Color.Black.copy(alpha = 0.55f), RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp),
        )
    }
}

/** Mira (como en Pokémon GO) en el centro de la pantalla: ahí aparecerá el monstruo. */
@Composable
internal fun Reticle(active: Boolean, modifier: Modifier = Modifier) {
    val color = if (active) BloodRed else TextMain.copy(alpha = 0.5f)
    Canvas(modifier.size(90.dp)) {
        val r = size.minDimension / 2
        drawCircle(color, r, style = androidx.compose.ui.graphics.drawscope.Stroke(2.dp.toPx()))
        drawCircle(color, 4.dp.toPx())
    }
}

// -------------------------------------------------------------------------------------------
// Con ARCore: cámara real + piso detectado.
// -------------------------------------------------------------------------------------------

@Composable
private fun ArSummon(enemy: Enemy, buffer: ByteBuffer?) {
    val context = LocalContext.current
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val materialLoader = rememberMaterialLoader(engine)
    val nodes = rememberNodes()
    var frame by remember { mutableStateOf<Frame?>(null) }
    var viewSize by remember { mutableStateOf(IntSize.Zero) }
    var floorFound by remember { mutableStateOf(false) }
    var summoned by remember { mutableStateOf(false) }
    var hint by remember { mutableStateOf("Apunta al piso y mueve el teléfono despacio…") }
    // Figura que mira siempre a la cámara (solo si no hay modelo 3D).
    var billboard by remember { mutableStateOf<Node?>(null) }
    // Animación de aparición: el monstruo crece desde el piso.
    var summonStart by remember { mutableStateOf(0L) }
    var monsterNode by remember { mutableStateOf<Node?>(null) }
    // Quieto / deambular (solo con modelo 3D).
    var behavior by remember { mutableStateOf<MonsterBehavior?>(null) }

    val cutout = remember(enemy.id) {
        val res = GameRepository.monsterCutout[enemy.id] ?: GameRepository.monsterArt[enemy.id]
        res?.let { BitmapFactory.decodeResource(context.resources, it) }
    }

    fun summonAt(x: Float, y: Float): Boolean {
        val hit = frame?.hitTest(x, y)?.firstOrNull {
            val t = it.trackable
            t is Plane && t.isPoseInPolygon(it.hitPose) && t.type == Plane.Type.HORIZONTAL_UPWARD_FACING
        } ?: return false
        // Un solo monstruo: si ya había uno, se mueve al nuevo punto.
        nodes.toList().forEach { it.destroy() }
        nodes.clear()
        val child: Node = if (buffer != null) {
            ModelNode(
                modelInstance = modelLoader.createModelInstance(buffer.duplicate().rewind()),
                autoAnimate = false,
                scaleToUnits = 1.6f,             // ~1.6 m de alto: tamaño de una persona
                centerOrigin = Position(y = -1f), // pies en el piso
            ).apply { isEditable = true }.also { behavior = MonsterBehavior(it) }
        } else {
            val bmp = cutout ?: return false
            val height = 1.6f
            val width = height * bmp.width / bmp.height
            ImageNode(
                materialLoader = materialLoader,
                bitmap = bmp,
                size = Size3(width, height),
                center = Position(y = height / 2f), // la base de la imagen toca el piso
            ).also { billboard = it }
        }
        monsterNode = child
        summonStart = System.nanoTime()
        child.scale = io.github.sceneview.math.Scale(0.01f)
        nodes += AnchorNode(engine, hit.createAnchor()).apply { addChildNode(child) }
        summoned = true
        hint = "¡${enemy.name} apareció! Toca el piso para moverlo"
        return true
    }

    Box(Modifier.fillMaxSize().onSizeChanged { viewSize = it }) {
        ARScene(
            modifier = Modifier.fillMaxSize(),
            engine = engine,
            modelLoader = modelLoader,
            materialLoader = materialLoader,
            childNodes = nodes,
            planeRenderer = !summoned,
            sessionConfiguration = { session, config ->
                config.planeFindingMode = Config.PlaneFindingMode.HORIZONTAL
                config.lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
                config.depthMode =
                    if (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) Config.DepthMode.AUTOMATIC
                    else Config.DepthMode.DISABLED
            },
            onSessionUpdated = { _, updatedFrame ->
                frame = updatedFrame
                behavior?.update(System.nanoTime())
                if (!floorFound) {
                    floorFound = updatedFrame.getUpdatedTrackables(Plane::class.java)
                        .any { it.type == Plane.Type.HORIZONTAL_UPWARD_FACING }
                    if (floorFound) hint = "Piso detectado: apunta la mira e invoca"
                }
                // Aparición: crece en 0.7 s con un pequeño rebote.
                monsterNode?.let { node ->
                    val t = ((System.nanoTime() - summonStart) / 700_000_000f).coerceIn(0f, 1f)
                    if (t < 1f || node.scale.x < 1f) {
                        val s = 1f + 0.15f * kotlin.math.sin(t * Math.PI.toFloat()) * (1f - t)
                        node.scale = io.github.sceneview.math.Scale(maxOf(0.01f, t * s))
                    }
                }
                // La figura 2D gira para mirar siempre a la cámara (solo en el eje vertical).
                billboard?.let { node ->
                    val cam = updatedFrame.camera.pose
                    val p = node.worldPosition
                    // lookAt apunta -Z al objetivo; la imagen mira hacia +Z, así que se usa el punto opuesto.
                    node.lookAt(Position(2 * p.x - cam.tx(), p.y, 2 * p.z - cam.tz()))
                }
            },
            onTrackingFailureChanged = { reason ->
                if (reason != null && reason != TrackingFailureReason.NONE) {
                    hint = "Busca más luz o mueve el teléfono más despacio"
                }
            },
            onGestureListener = rememberOnGestureListener(
                onSingleTapConfirmed = { event, _ -> if (floorFound) summonAt(event.x, event.y) },
            ),
        )

        if (!summoned) Reticle(floorFound, Modifier.align(Alignment.Center))

        Column(
            Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                hint, color = TextMain, fontFamily = HorrorFont, textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp)).padding(10.dp),
            )
            Spacer(Modifier.height(16.dp))
            if (!summoned) {
                SummonButton(
                    enabled = floorFound,
                    label = "INVOCAR MONSTRUO",
                    onClick = {
                        if (!summonAt(viewSize.width / 2f, viewSize.height / 2f)) {
                            hint = "No hay piso en la mira. Apunta a una zona detectada"
                        }
                    },
                )
            }
        }
    }
}

// -------------------------------------------------------------------------------------------
// Sin ARCore: modo sin AR (como Pokémon GO con "RA desactivada").
// -------------------------------------------------------------------------------------------

@Composable
private fun NoArSummon(enemy: Enemy) {
    var summoned by remember { mutableStateOf(false) }
    val appear = remember { Animatable(0f) }
    LaunchedEffect(summoned) {
        if (summoned) appear.animateTo(1f, tween(700, easing = FastOutSlowInEasing))
    }
    // Respiración: sube y baja un poco, para que se sienta vivo.
    val idle by rememberInfiniteTransition(label = "idle").animateFloat(
        0f, 1f, infiniteRepeatable(tween(1800, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "bob",
    )
    val cutout = GameRepository.monsterCutout[enemy.id]
    val art = GameRepository.monsterArt[enemy.id]

    Box(Modifier.fillMaxSize()) {
        // Fondo: la propia escena del monstruo, oscurecida, como su "hábitat".
        if (art != null) {
            Image(
                painterResource(art), null, contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().alpha(0.35f),
            )
        }
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Background.copy(alpha = 0.6f), Color.Transparent, Background))))

        // Sombra en el piso y círculo de invocación.
        Canvas(Modifier.fillMaxSize()) {
            val c = Offset(size.width / 2, size.height * 0.72f)
            val ring = 0.6f + 0.4f * appear.value
            drawOval(
                BloodRed.copy(alpha = 0.35f + 0.25f * (1 - appear.value)),
                topLeft = Offset(c.x - 140.dp.toPx() * ring, c.y - 26.dp.toPx() * ring),
                size = Size(280.dp.toPx() * ring, 52.dp.toPx() * ring),
                style = androidx.compose.ui.graphics.drawscope.Stroke(2.dp.toPx()),
            )
            drawOval(
                Color.Black.copy(alpha = 0.6f * appear.value),
                topLeft = Offset(c.x - 90.dp.toPx(), c.y - 14.dp.toPx()),
                size = Size(180.dp.toPx(), 28.dp.toPx()),
            )
        }

        if (summoned && (cutout ?: art) != null) {
            Image(
                painterResource(cutout ?: art!!), enemy.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 200.dp)
                    .fillMaxWidth(0.85f)
                    .height(420.dp)
                    .graphicsLayer {
                        val a = appear.value
                        scaleX = a; scaleY = a
                        alpha = a
                        translationY = -idle * 10.dp.toPx()
                        transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0.5f, 1f)
                    },
            )
        }

        Column(
            Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (summoned) {
                Row {
                    Tag(enemy.enemyClass, filled = true)
                    Spacer(Modifier.width(8.dp))
                    Tag("Peligro ${enemy.dangerLevel}")
                }
                Spacer(Modifier.height(12.dp))
            }
            Text(
                "Sin permiso de cámara: modo sin AR.",
                color = TextMuted, fontFamily = HorrorFont, textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp)).padding(10.dp),
            )
            if (!summoned) {
                Spacer(Modifier.height(16.dp))
                SummonButton(enabled = true, label = "INVOCAR MONSTRUO", onClick = { summoned = true })
            }
        }
    }
}
