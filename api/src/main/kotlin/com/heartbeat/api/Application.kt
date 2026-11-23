package com.heartbeat.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.ktor.server.http.content.staticResources
import io.ktor.server.plugins.origin
import io.ktor.server.routing.RoutingCall
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.serialization.Serializable

@Serializable
data class ErrorDto(val error: String)

fun main() {
    // Render (y casi todos los hostings) indican el puerto con la variable PORT.
    val port = System.getenv("PORT")?.toIntOrNull() ?: 8080
    embeddedServer(Netty, port = port, host = "0.0.0.0") {
        module(MonsterService(HttpClient(CIO)))
    }.start(wait = true)
}

fun Application.module(service: MonsterService) {
    install(ContentNegotiation) { json() }
    install(CallLogging)
    install(CORS) {
        anyHost()
        allowMethod(HttpMethod.Get)
    }
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.application.environment.log.error("Error no controlado", cause)
            call.respond(HttpStatusCode.InternalServerError, ErrorDto("Error interno"))
        }
    }

    routing {
        get("/") { call.respond(mapOf("name" to "Heartbeat API", "version" to "1.0.0")) }
        // Imágenes cinematográficas de los monstruos (resources/static/monsters).
        staticResources("/static", "static")

        get("/health") { call.respond(mapOf("status" to "ok")) }

        get("/api/monsters") { call.respond(service.all(call.publicUrl())) }

        get("/api/monsters/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest, ErrorDto("id inválido"))
            val monster = service.byId(id, call.publicUrl())
                ?: return@get call.respond(HttpStatusCode.NotFound, ErrorDto("Monstruo $id no encontrado"))
            call.respond(monster)
        }

        // Llamar después de subir nuevos .glb al repo para que la API los detecte sin reiniciar.
        post("/api/models/refresh") {
            service.refreshModels()
            call.respond(service.all(call.publicUrl()))
        }
    }
}

/**
 * Dirección pública de la API. En producción se fija con la variable PUBLIC_URL
 * (p. ej. https://heartbeat-api.onrender.com); en local se usa el host de la petición.
 */
private fun RoutingCall.publicUrl(): String =
    System.getenv("PUBLIC_URL")?.trimEnd('/')
        ?: request.origin.let { "${it.scheme}://${it.serverHost}:${it.serverPort}" }
