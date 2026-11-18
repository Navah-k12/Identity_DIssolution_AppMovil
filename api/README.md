# Heartbeat API

API en Ktor que da los monstruos del Bestiario a la app. Las imágenes y los modelos 3D
salen del repo [Low-Poly-3D-Models-for-a-Video-Game-Event](https://github.com/Navah-k12/Low-Poly-3D-Models-for-a-Video-Game-Event).

## Endpoints

| Método | Ruta | Qué hace |
|---|---|---|
| GET | `/api/monsters` | Lista de monstruos |
| GET | `/api/monsters/{id}` | Un monstruo |
| POST | `/api/models/refresh` | Vuelve a revisar qué `.glb` ya están subidos |
| GET | `/health` | Estado del servidor |

`modelUrl` es `null` mientras el `.glb` no exista en el repo de modelos.

## Correr en local

Desde la raíz del proyecto:

```bash
./gradlew -p api run          # API en http://localhost:8080
adb reverse tcp:8080 tcp:8080 # una vez por cada arranque del emulador
```

Luego corre la app (build debug) en el emulador.

## Añadir o cambiar monstruos

1. Edita `src/main/resources/monsters.json`.
2. Sube al repo de modelos el `.glb` en la ruta `folder/model`
   (por ejemplo `Monsters/Monster1/Monster1.glb`).
3. Reinicia la API o llama a `POST /api/models/refresh`.

## Pruebas

```bash
./gradlew -p api test
```

## Desplegar (Render)

1. New → Web Service → conecta este repo.
2. Root directory: `api` · Runtime: Docker.
3. Copia la URL que te da Render en `app/build.gradle.kts` (`API_BASE_URL` de `release`).
