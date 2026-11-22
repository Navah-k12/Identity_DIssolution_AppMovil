package com.example.identity_dissolution_appmovil.ui.screens

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.identity_dissolution_appmovil.data.repository.GameRepository
import com.example.identity_dissolution_appmovil.model.Enemy
import com.example.identity_dissolution_appmovil.ui.theme.BloodRed
import com.example.identity_dissolution_appmovil.ui.theme.HorrorFont
import com.example.identity_dissolution_appmovil.ui.theme.TextMain
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.sqrt
import dev.romainguy.kotlin.math.Float3
import io.github.sceneview.Scene
import io.github.sceneview.math.Scale
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberCameraNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberEnvironment
import io.github.sceneview.rememberEnvironmentLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes
import java.nio.ByteBuffer

/** Hacia dónde apunta la cámara trasera, en radianes. */
private class Aim(
    val heading: Float,
    val elevation: Float,
    val roll: Float,
    /** Matriz de rotación del teléfono (teléfono → mundo), para mover la cámara 3D. */
    val matrix: FloatArray = FloatArray(9).also { it[0] = 1f; it[4] = 1f; it[8] = 1f },
)

// Campo de visión aproximado de la cámara trasera de un teléfono en vertical.
private const val H_FOV = (60 * PI / 180).toFloat()
private const val V_FOV = (78 * PI / 180).toFloat()

/**
 * AR clásico (como Pokémon GO antes de ARCore): cámara normal + giroscopio.
 * Al invocarlo, el monstruo queda fijo en la dirección a la que apuntabas;
 * si giras el teléfono, sale de la pantalla como si estuviera en la habitación.
 */
@Composable
internal fun GyroSummon(enemy: Enemy, model: ByteBuffer? = null) {
    // Con modelo 3D se dibuja en una escena 3D cuya cámara gira igual que el teléfono.
    if (model != null) {
        GyroSummon3D(enemy, model)
        return
    }
    val aim = rememberCameraAim()
    var summonAim by remember { mutableStateOf<Aim?>(null) }
    var viewSize by remember { mutableStateOf(IntSize.Zero) }
    val appear = remember { Animatable(0f) }
    LaunchedEffect(summonAim) {
        if (summonAim != null) appear.animateTo(1f, tween(700, easing = FastOutSlowInEasing))
    }
    val idle by rememberInfiniteTransition(label = "idle").animateFloat(
        0f, 1f, infiniteRepeatable(tween(1800, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "bob",
    )
    val density = LocalDensity.current
    val image = GameRepository.monsterCutout[enemy.id] ?: GameRepository.monsterArt[enemy.id]

    // Dónde cae el monstruo en la pantalla, según cuánto giró el teléfono desde la invocación.
    val start = summonAim
    val now = aim
    var dx = 0f
    var dy = 0f
    if (start != null && now != null) {
        dx = wrap(start.heading - now.heading) / H_FOV * viewSize.width
        dy = (now.elevation - start.elevation) / V_FOV * viewSize.height
    }
    val rollDeg = (now?.roll ?: 0f) * 180f / PI.toFloat()
    val monsterHeight = with(density) { (viewSize.height * 0.55f).toDp() }
    val offScreen = start != null && (abs(dx) > viewSize.width * 0.6f || abs(dy) > viewSize.height * 0.6f)

    Box(Modifier.fillMaxSize().onSizeChanged { viewSize = it }) {
        CameraPreview(Modifier.fillMaxSize())

        if (start != null && image != null) {
            Box(
                Modifier
                    .align(Alignment.Center)
                    .graphicsLayer {
                        translationX = dx
                        translationY = dy - idle * 8.dp.toPx()
                        rotationZ = rollDeg
                        val a = appear.value
                        scaleX = a; scaleY = a; alpha = a
                        transformOrigin = TransformOrigin(0.5f, 1f)
                    },
                contentAlignment = Alignment.BottomCenter,
            ) {
                // Sombra bajo los pies.
                Canvas(Modifier.size(monsterHeight * 0.55f, 24.dp)) {
                    drawOval(Color.Black.copy(alpha = 0.45f), Offset.Zero, Size(size.width, size.height))
                }
                Image(
                    painterResource(image), enemy.name,
                    // FillHeight: la imagen ocupa toda la altura, así los pies tocan la sombra.
                    contentScale = ContentScale.FillHeight,
                    modifier = Modifier.height(monsterHeight).padding(bottom = 10.dp),
                )
            }
        }

        // Si el monstruo quedó fuera de la pantalla, una flecha indica hacia dónde girar.
        if (offScreen) {
            Canvas(Modifier.fillMaxSize()) {
                val c = Offset(size.width / 2, size.height / 2)
                val angle = atan2(dy, dx)
                val r = size.minDimension * 0.38f
                val tip = Offset(c.x + r * kotlin.math.cos(angle), c.y + r * kotlin.math.sin(angle))
                val back = 26.dp.toPx()
                val side = 16.dp.toPx()
                val bx = tip.x - back * kotlin.math.cos(angle)
                val by = tip.y - back * kotlin.math.sin(angle)
                val path = Path().apply {
                    moveTo(tip.x, tip.y)
                    lineTo(bx - side * kotlin.math.sin(angle), by + side * kotlin.math.cos(angle))
                    lineTo(bx + side * kotlin.math.sin(angle), by - side * kotlin.math.cos(angle))
                    close()
                }
                drawPath(path, BloodRed)
            }
        }

        if (start == null) Reticle(false, Modifier.align(Alignment.Center))

        Column(
            Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val hint = when {
                now == null -> "Sin giroscopio: el monstruo quedará en el centro"
                start == null -> "Apunta a donde quieras que aparezca"
                offScreen -> "Sigue la flecha para encontrarlo"
                else -> "¡${enemy.name} apareció! Gira el teléfono a su alrededor"
            }
            Text(
                hint, color = TextMain, fontFamily = HorrorFont, textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp)).padding(10.dp),
            )
            if (start == null) {
                Spacer(Modifier.height(16.dp))
                SummonButton(
                    enabled = true,
                    label = "INVOCAR MONSTRUO",
                    // Sin sensor se usa una dirección fija: el monstruo queda en el centro.
                    onClick = { summonAim = aim ?: Aim(0f, 0f, 0f) },
                )
            }
        }
    }
}

/** Ángulo en el rango -π..π (así girar de 359° a 1° cuenta como 2°, no como 358°). */
private fun wrap(a: Float): Float {
    var x = a
    while (x > PI) x -= (2 * PI).toFloat()
    while (x < -PI) x += (2 * PI).toFloat()
    return x
}

/**
 * Lee el sensor de rotación y calcula hacia dónde mira la cámara trasera.
 * Devuelve null si el teléfono no tiene giroscopio.
 */
@Composable
private fun rememberCameraAim(): Aim? {
    val context = LocalContext.current
    var aim by remember { mutableStateOf<Aim?>(null) }
    // Suavizado para que el monstruo no tiemble.
    var heading by remember { mutableFloatStateOf(Float.NaN) }

    DisposableEffect(Unit) {
        val sm = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        // GAME_ROTATION_VECTOR usa giroscopio + acelerómetro (sin brújula): más estable en interiores.
        val sensor = sm.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR)
            ?: sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        val r = FloatArray(9)
        val listener = object : SensorEventListener {
            override fun onSensorChanged(e: SensorEvent) {
                SensorManager.getRotationMatrixFromVector(r, e.values)
                // La cámara trasera mira hacia -Z del teléfono. r convierte del teléfono al mundo
                // (X = este, Y = norte, Z = arriba), así que la dirección de la cámara es -(3.ª columna).
                val fx = -r[2]; val fy = -r[5]; val fz = -r[8]
                val h = atan2(fx, fy)
                val elevation = asin(fz.coerceIn(-1f, 1f))
                // Inclinación lateral: cuánto baja el lado derecho del teléfono respecto al horizonte.
                val roll = asin(r[6].coerceIn(-1f, 1f))
                heading = if (heading.isNaN()) h else heading + wrap(h - heading) * 0.35f
                aim = Aim(heading, elevation, roll, r.copyOf())
            }
            override fun onAccuracyChanged(s: Sensor?, a: Int) = Unit
        }
        if (sensor != null) sm.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_GAME)
        onDispose { sm.unregisterListener(listener) }
    }
    return aim
}

/**
 * Versión 3D: el modelo animado se coloca en el mundo, 3 m delante de donde apuntabas,
 * y una cámara virtual copia la rotación del teléfono. Encima de la cámara real, se ve como AR.
 */
@Composable
private fun GyroSummon3D(enemy: Enemy, buffer: ByteBuffer) {
    val aim = rememberCameraAim()
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val environmentLoader = rememberEnvironmentLoader(engine)
    val nodes = rememberNodes()
    // Lente de 17 mm (sensor de 24 mm): ~70° de vista vertical, como la cámara de un teléfono en vertical.
    // Así el monstruo se ve del mismo tamaño que tendría en la habitación.
    val cameraNode = rememberCameraNode(engine) { focalLength = 17.0 }
    var summoned by remember { mutableStateOf(false) }
    var monster by remember { mutableStateOf<ModelNode?>(null) }
    var behavior by remember { mutableStateOf<MonsterBehavior?>(null) }
    var summonStart by remember { mutableStateOf(0L) }
    // Dirección del monstruo en pantalla cuando está fuera de la vista (null = se ve).
    var offDir by remember { mutableStateOf<Offset?>(null) }

    // Ejes del teléfono en coordenadas de SceneView (X = este, Y = arriba, Z = sur).
    fun forward(m: FloatArray) = Float3(-m[2], -m[8], m[5])
    fun up(m: FloatArray) = Float3(m[1], m[7], -m[4])

    fun summon() {
        val m = aim?.matrix ?: FloatArray(9).also { it[0] = 1f; it[4] = 1f; it[8] = 1f }
        val f = forward(m)
        // Solo la parte horizontal: el monstruo queda de pie en el piso, delante de ti.
        val len = sqrt(f.x * f.x + f.z * f.z).takeIf { it > 0.1f }
        val dir = if (len != null) Float3(f.x / len, 0f, f.z / len) else Float3(0f, 0f, -1f)
        val pos = Float3(dir.x * 3f, -1.4f, dir.z * 3f) // 1.4 m: altura del teléfono sobre el piso
        nodes.toList().forEach { it.destroy() }
        nodes.clear()
        val node = ModelNode(
            modelInstance = modelLoader.createModelInstance(buffer.duplicate().rewind()),
            autoAnimate = false,
            scaleToUnits = 1.7f,
            centerOrigin = Float3(0f, -1f, 0f), // pies en el origen del nodo
        ).apply {
            position = pos
            scale = Scale(0.01f)
        }
        // Quieto respirando y, si sabe caminar, deambula cerca. Al aparecer te mira (la cámara está en el origen).
        behavior = MonsterBehavior(node, viewer = Float3(0f, pos.y, 0f)).also { it.face(-pos.x, -pos.z) }
        nodes += node
        monster = node
        summonStart = System.nanoTime()
        summoned = true
    }

    Box(Modifier.fillMaxSize()) {
        CameraPreview(Modifier.fillMaxSize())
        Scene(
            modifier = Modifier.fillMaxSize(),
            engine = engine,
            modelLoader = modelLoader,
            isOpaque = false,
            environment = rememberEnvironment(environmentLoader, isOpaque = false),
            cameraNode = cameraNode,
            cameraManipulator = null,
            childNodes = nodes,
            onFrame = { frameTime ->
                behavior?.update(frameTime)
                val m = aim?.matrix
                val f = if (m != null) forward(m) else Float3(0f, 0f, -1f)
                val u = if (m != null) up(m) else Float3(0f, 1f, 0f)
                cameraNode.position = Float3(0f, 0f, 0f)
                cameraNode.lookAt(f, u)
                monster?.let { node ->
                    // Aparición: crece en 0.7 s.
                    val t = ((System.nanoTime() - summonStart) / 700_000_000f).coerceIn(0f, 1f)
                    if (node.scale.x < 1f) node.scale = Scale(maxOf(0.01f, t))
                    // ¿Está el monstruo dentro de la vista? Si no, se calcula hacia dónde girar.
                    val p = node.worldPosition + Float3(0f, 0.8f, 0f)
                    val l = sqrt(p.x * p.x + p.y * p.y + p.z * p.z)
                    val d = Float3(p.x / l, p.y / l, p.z / l)
                    val dot = d.x * f.x + d.y * f.y + d.z * f.z
                    offDir = if (dot > 0.85f) null else {
                        val right = Float3(f.y * u.z - f.z * u.y, f.z * u.x - f.x * u.z, f.x * u.y - f.y * u.x)
                        Offset(d.x * right.x + d.y * right.y + d.z * right.z, -(d.x * u.x + d.y * u.y + d.z * u.z))
                    }
                }
            },
        )

        if (summoned) offDir?.let { DirectionArrow(it) }
        if (!summoned) Reticle(false, Modifier.align(Alignment.Center))

        Column(
            Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val hint = when {
                aim == null -> "Sin giroscopio: el monstruo no seguirá el movimiento"
                !summoned -> "Apunta a donde quieras que aparezca"
                offDir != null -> "Sigue la flecha para encontrarlo"
                else -> "¡${enemy.name} apareció! Gira el teléfono a su alrededor"
            }
            Text(
                hint, color = TextMain, fontFamily = HorrorFont, textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp)).padding(10.dp),
            )
            if (!summoned) {
                Spacer(Modifier.height(16.dp))
                SummonButton(enabled = true, label = "INVOCAR MONSTRUO", onClick = { summon() })
            }
        }
    }
}

/** Flecha roja que apunta hacia el monstruo (dirección en pantalla: x derecha, y abajo). */
@Composable
private fun DirectionArrow(d: Offset) {
    Canvas(Modifier.fillMaxSize()) {
        val c = Offset(size.width / 2, size.height / 2)
        val angle = atan2(d.y, d.x)
        val r = size.minDimension * 0.38f
        val tip = Offset(c.x + r * kotlin.math.cos(angle), c.y + r * kotlin.math.sin(angle))
        val back = 26.dp.toPx()
        val side = 16.dp.toPx()
        val bx = tip.x - back * kotlin.math.cos(angle)
        val by = tip.y - back * kotlin.math.sin(angle)
        val path = Path().apply {
            moveTo(tip.x, tip.y)
            lineTo(bx - side * kotlin.math.sin(angle), by + side * kotlin.math.cos(angle))
            lineTo(bx + side * kotlin.math.sin(angle), by - side * kotlin.math.cos(angle))
            close()
        }
        drawPath(path, BloodRed)
    }
}


/** Vista previa de la cámara trasera con CameraX. */
@Composable
private fun CameraPreview(modifier: Modifier) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
            // TextureView: deja que la escena 3D transparente se dibuje encima.
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        } }

    DisposableEffect(lifecycleOwner) {
        val future = ProcessCameraProvider.getInstance(context)
        future.addListener({
            val provider = future.get()
            val preview = Preview.Builder().build().also { it.surfaceProvider = previewView.surfaceProvider }
            runCatching {
                provider.unbindAll()
                provider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview)
            }
        }, ContextCompat.getMainExecutor(context))
        onDispose { runCatching { future.get().unbindAll() } }
    }
    AndroidView(factory = { previewView }, modifier = modifier)
}
