package jetbrains.org

import io.ktor.server.application.Application
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import jetbrains.org.plugins.*
import jetbrains.org.routing.configureRouting

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureKoin()
    configureSerialization()
    connectToDatabase()
    configureRouting()
}

