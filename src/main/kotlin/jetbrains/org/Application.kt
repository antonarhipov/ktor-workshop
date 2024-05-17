package jetbrains.org

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import jetbrains.org.plugins.*
import kotlinx.serialization.Serializable

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSerialization()
    configureRouting()
}

@Serializable
data class User(
    val userId: Int,
    val userType: UserType,
    val displayName: String,
    val link: String,
    val aboutMe: String? = null
)

enum class UserType {
    REGISTERED,
    MODERATOR,
}