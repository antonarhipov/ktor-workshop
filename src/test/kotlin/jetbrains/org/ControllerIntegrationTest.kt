package jetbrains.org

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import jetbrains.org.db.UsersRepository
import jetbrains.org.db.UsersRepositoryImpl
import jetbrains.org.model.User
import jetbrains.org.model.UserType
import jetbrains.org.plugins.configureSerialization
import jetbrains.org.routing.configureRouting
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.Test
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.testcontainers.junit.jupiter.Testcontainers
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@Testcontainers
class ControllerIntegrationTest : AbstractDatabaseTest() {

    private val usersRepository = UsersRepositoryImpl()

    private val testApp = TestApplication {
        install(Koin) {
            modules(
                module {
                    single<UsersRepository> { usersRepository }
                }
            )
        }
        application {
            configureSerialization()
            configureRouting()
        }
    }

    private val client = testApp.createClient {
        install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
            json()
        }
    }

    @Test
    fun `get all data`(): Unit = runBlocking {
        client.get("/users").apply {
            assertEquals(HttpStatusCode.OK, status)
            val data = Json.decodeFromString<List<User>>(bodyAsText())
            assertEquals(0, data.size)
        }
    }

    @Test
    fun `post data instance`(): Unit = runBlocking {
        val user = User(1, UserType.REGISTERED, "A", "a.com", aboutMe = "AAA")

        val response = client.post("/users") {
            contentType(ContentType.Application.Json)
            setBody(user)
        }
        assertEquals(HttpStatusCode.Created, response.status)
        assertEquals("Data added successfully", response.bodyAsText())

        //validate the result via the service layer
        assertEquals(user.name, usersRepository.find(1)?.name)
    }


}