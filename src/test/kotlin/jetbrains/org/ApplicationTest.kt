package jetbrains.org

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import jetbrains.org.db.UsersRepository
import jetbrains.org.db.fake.FakeUsersRepository
import jetbrains.org.model.User
import jetbrains.org.model.UserType
import jetbrains.org.plugins.configureSerialization
import jetbrains.org.routing.configureRouting
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import kotlin.test.*

class ApplicationTest {

    var fakeData = mutableListOf(
        User(1, UserType.REGISTERED, "Anton", "x.com/antonarhipov", aboutMe = "I speak Kotlin"),
        User(2, UserType.REGISTERED, "Leonid", "x.com/___e5l", aboutMe = "I make Ktor"),
        User(3, UserType.REGISTERED, "John Doe", "en-wp.org/wiki/John_Doe", aboutMe = "I am John Doe"),
    )

    private val testApp = TestApplication {
        install(Koin) {
            modules(
                module {
                    single<UsersRepository> {
                        FakeUsersRepository().apply { storage = fakeData }
                        //we can also just implement UserRepository interface here
                        //object : UsersRepository {}
                    }
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
    fun `test root endpoint`(): Unit = runBlocking {
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Hello World!", bodyAsText())
        }
    }

    @Test
    fun `get all data`(): Unit = runBlocking {
        client.get("/users").apply {
            assertEquals(HttpStatusCode.OK, status)
            val data = Json.decodeFromString<List<User>>(bodyAsText())
            assertEquals(fakeData.size, data.size)
        }
    }

    @Test
    fun `post data instance`(): Unit = runBlocking {
        val response = client.post("/users") {
            contentType(ContentType.Application.Json)
            setBody(User(123, UserType.REGISTERED, "A", "a.com", aboutMe = "AAA"))
        }
        assertEquals(HttpStatusCode.Created, response.status)
        assertEquals("Data added successfully", response.bodyAsText())
    }

    @Test
    fun `put data instance`(): Unit = runBlocking {
        val user = fakeData.first()
        val updatedDataResponse = client.put("/users/${user.userId}") {
            contentType(ContentType.Application.Json)
            setBody(user.copy(name = "Mr. ${user.name}"))
        }
        assertEquals(HttpStatusCode.OK, updatedDataResponse.status)
        assertEquals("Data updated successfully", updatedDataResponse.bodyAsText())
    }

    @Test
    fun `delete data instance`(): Unit = runBlocking {
        client.delete("/users/1").apply {
            // Assertions to confirm the successful deletion of the Data instance
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Data deleted successfully", bodyAsText())
        }

        client.get("/users/1").apply {
            // Assertions to confirm the successful fetching of the updated Data instances
            assertEquals(HttpStatusCode.NotFound, status)
        }
    }


}
