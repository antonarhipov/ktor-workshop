package jetbrains.org

import jetbrains.org.db.UserTable
import jetbrains.org.db.UsersRepositoryImpl
import jetbrains.org.model.User
import jetbrains.org.model.UserType
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Assert.*
import org.junit.Test
import org.testcontainers.junit.jupiter.Testcontainers
import kotlin.test.BeforeTest

@Testcontainers
class DatabaseIntegrationTest : AbstractDatabaseTest() {

    private val userRepository = UsersRepositoryImpl()

    @BeforeTest
    fun startup(): Unit = clearDatabaseContents()

    @Test
    fun testFindAll() = runBlocking {
        val users = userRepository.findAll()
        assertNotNull(users)
    }

    @Test
    fun testSave() = runBlocking {
        val user = User(
            userId = 1L,
            userType = UserType.REGISTERED,
            name = "Test",
            email = "test@example.com",
            link = "https://example.com",
            aboutMe = "About test"
        )
        userRepository.save(user)
        val savedUser = userRepository.find(1L)
        assertNotNull(savedUser)
        assertEquals("Test", savedUser?.name)
    }

    @Test
    fun testUpdate() = runBlocking {
        val u1 = User(
            userId = 1L,
            userType = UserType.REGISTERED,
            name = "Test",
            email = "test@example.com",
            link = "https://example.com",
            aboutMe = "About test"
        )

        userRepository.save(u1)
        assertNotNull(userRepository.find(1L))

        val newName = "Test Update"
        val u2 = u1.copy(name = newName)
        assertTrue(userRepository.update(u2))
        assertEquals(newName, userRepository.find(1L)?.name)
    }

    private fun clearDatabaseContents() {
        transaction {
            UserTable.deleteAll()
        }
    }
}
