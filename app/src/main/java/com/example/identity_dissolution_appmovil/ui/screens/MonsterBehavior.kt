package com.example.identity_dissolution_appmovil.ui.screens

import dev.romainguy.kotlin.math.Float3
import io.github.sceneview.math.Rotation
import io.github.sceneview.node.ModelNode
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * Comportamiento del monstruo invocado: se queda quieto respirando y, si tiene animación
 * de caminar, de vez en cuando deambula a un punto cercano y vuelve a quedarse quieto.
 *
 * [viewer]: dónde está quien mira (posición local), si se conoce. Entonces el monstruo no se
 * acerca a menos de lo que estaba al aparecer y, al quedarse quieto, se gira para mirarte.
 *
 * Llama a [update] en cada frame. Trabaja con la posición local del nodo,
 * así sirve igual dentro de un AnchorNode (ARCore) o en la escena del giroscopio.
 */
internal class MonsterBehavior(private val node: ModelNode, private val viewer: Float3? = null) {

    private val animator = node.modelInstance.animator
    private val idle = find("Quieto") ?: 0.takeIf { animator.animationCount > 0 }
    private val walk = find("Patrullar", "Patrulla", "Caminar")

    /** Punto donde apareció: nunca se aleja más de [RADIUS] de aquí. */
    private val home = node.position
    private var target = home
    private var walking = false
    private var stateEnds = 0L
    private var lastFrame = 0L
    private var current: Int? = null

    init {
        play(idle)
    }

    fun update(now: Long) {
        val dt = if (lastFrame == 0L) 0f else ((now - lastFrame) / 1e9f).coerceAtMost(0.1f)
        lastFrame = now
        if (stateEnds == 0L) stateEnds = now + idleTime()

        if (walking) {
            val p = node.position
            val dx = target.x - p.x
            val dz = target.z - p.z
            val dist = sqrt(dx * dx + dz * dz)
            if (dist < 0.03f || now > stateEnds) {
                // Llegó: se queda quieto respirando.
                walking = false
                play(idle)
                viewer?.let { face(it.x - node.position.x, it.z - node.position.z) }
                stateEnds = now + idleTime()
            } else {
                val step = min(dist, SPEED * dt)
                node.position = Float3(p.x + dx / dist * step, p.y, p.z + dz / dist * step)
            }
        } else if (walk != null && now > stateEnds) {
            // Elige un punto al azar cerca de donde apareció y camina hacia él.
            target = pickTarget()
            face(target.x - node.position.x, target.z - node.position.z)
            walking = true
            play(walk)
            stateEnds = now + 8_000_000_000L // por si acaso, nunca camina más de 8 s
        }
    }

    /** Punto al azar cerca de donde apareció; si se sabe dónde está quien mira, que no se le acerque. */
    private fun pickTarget(): Float3 {
        var best = home
        repeat(12) {
            val angle = Random.nextFloat() * 2f * PI.toFloat()
            val r = 0.3f + Random.nextFloat() * (RADIUS - 0.3f)
            val t = Float3(home.x + cos(angle) * r, home.y, home.z + sin(angle) * r)
            if (viewer == null || flatDist(t, viewer) >= flatDist(home, viewer) - 0.1f) return t
            best = t
        }
        return best
    }

    private fun flatDist(a: Float3, b: Float3) = sqrt((a.x - b.x) * (a.x - b.x) + (a.z - b.z) * (a.z - b.z))

    /** Gira el monstruo para que mire en la dirección (dx, dz). Los modelos glTF miran hacia +Z. */
    fun face(dx: Float, dz: Float) {
        if (dx * dx + dz * dz < 1e-6f) return
        val yaw = atan2(dx, dz) * 180f / PI.toFloat()
        node.rotation = Rotation(0f, yaw, 0f)
    }

    private fun play(index: Int?) {
        if (index == null || index == current) return
        current?.let { node.stopAnimation(it) }
        node.playAnimation(index, 1f, true)
        current = index
    }

    private fun find(vararg names: String): Int? =
        (0 until animator.animationCount).firstOrNull { animator.getAnimationName(it) in names }

    /** Entre 4 y 7 segundos quieto. */
    private fun idleTime() = (4_000L + Random.nextLong(3_000L)) * 1_000_000L

    private companion object {
        const val SPEED = 0.35f   // metros por segundo: paso lento, de patrulla
        const val RADIUS = 0.9f   // metros alrededor del punto de aparición
    }
}
