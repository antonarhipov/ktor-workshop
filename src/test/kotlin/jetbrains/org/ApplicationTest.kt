package jetbrains.org

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import jetbrains.org.plugins.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlin.test.*

class ApplicationTest {

    var fakeData = mutableListOf<User>(
        User(1, UserType.REGISTERED, "Anton", "x.com/antonarhipov", aboutMe = "I speak Kotlin"),
        User(2, UserType.REGISTERED, "Leonid", "x.com/___e5l", aboutMe = "I make Ktor"),
        User(3, UserType.REGISTERED, "John Doe", "en-wp.org/wiki/John_Doe", aboutMe = "I am John Doe"),
    )

    private val testApp = TestApplication {
        application {
            module()
        }
    }

    private val client = testApp.createClient {}

    @Test
    fun `test root endpoint`(): Unit = runBlocking {
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Hello, World!", bodyAsText())
        }
    }

    @Test
    fun `get all data`(): Unit = runBlocking {

    }

    @Test
    fun `post data instance`(): Unit = runBlocking {

    }

    @Test
    fun `put data instance`(): Unit = runBlocking {

    }

    @Test
    fun `delete data instance`(): Unit = runBlocking {
        
    }

}
