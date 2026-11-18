plugins {
    kotlin("jvm") version "2.2.10"
    kotlin("plugin.serialization") version "2.2.10"
    id("io.ktor.plugin") version "3.2.3"
}

group = "com.heartbeat"
version = "1.0.0"

application {
    mainClass.set("com.heartbeat.api.ApplicationKt")
}

kotlin { jvmToolchain(21) }

dependencies {
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-netty")
    implementation("io.ktor:ktor-server-content-negotiation")
    implementation("io.ktor:ktor-serialization-kotlinx-json")
    implementation("io.ktor:ktor-server-cors")
    implementation("io.ktor:ktor-server-call-logging")
    implementation("io.ktor:ktor-server-status-pages")
    implementation("io.ktor:ktor-client-core")
    implementation("io.ktor:ktor-client-cio")
    implementation("ch.qos.logback:logback-classic:1.5.18")

    testImplementation("io.ktor:ktor-server-test-host")
    testImplementation("io.ktor:ktor-client-mock")
    testImplementation(kotlin("test"))
}

// "./gradlew -p api buildFatJar" crea build/libs/heartbeat-api-all.jar para el Dockerfile.
ktor {
    fatJar { archiveFileName.set("heartbeat-api-all.jar") }
}
