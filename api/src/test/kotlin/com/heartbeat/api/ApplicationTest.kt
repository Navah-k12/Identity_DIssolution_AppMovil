package com.heartbeat.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ApplicationTest {

    // GitHub falso: solo existe el modelo del monstruo 1.
    private fun fakeGithub() = HttpClient(MockEngine { request ->
        if (request.url.encodedPath.endsWith("Monster1.glb")) respond("", HttpStatusCode.OK)
        else respond("", HttpStatusCode.NotFound)
    })

    @Test
    fun monsters_listAll_withModelOnlyWhenItExists() = testApplication {
        application { module(MonsterService(fakeGithub())) }
        val res = client.get("/api/monsters")
        assertEquals(HttpStatusCode.OK, res.status)
        val list = Json.decodeFromString<List<MonsterDto>>(res.bodyAsText())
        assertEquals(4, list.size)
        assertNotNull(list.first { it.id == 1 }.modelUrl)
        assertNull(list.first { it.id == 2 }.modelUrl)
        // Las imágenes cinematográficas las sirve la propia API.
        assert(list.all { it.imageUrl.endsWith(".webp") && "/static/monsters/" in it.imageUrl })
        assert(list.all { it.posterUrl?.endsWith(".jpg") == true })
    }

    @Test
    fun cinematicImage_isServed() = testApplication {
        application { module(MonsterService(fakeGithub())) }
        val res = client.get("/static/monsters/monster1.webp")
        assertEquals(HttpStatusCode.OK, res.status)
    }

    @Test
    fun monster_unknownId_returns404() = testApplication {
        application { module(MonsterService(fakeGithub())) }
        assertEquals(HttpStatusCode.NotFound, client.get("/api/monsters/99").status)
    }

    @Test
    fun monster_badId_returns400() = testApplication {
        application { module(MonsterService(fakeGithub())) }
        assertEquals(HttpStatusCode.BadRequest, client.get("/api/monsters/abc").status)
    }
}
