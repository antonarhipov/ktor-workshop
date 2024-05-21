package jetbrains.org.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import jetbrains.org.model.User
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.dsl.module

val usersDataModule = module {
    single<UsersRepository> { UsersRepositoryImpl() }
}

interface UsersRepository {
    suspend fun findAll(): List<User>
    suspend fun save(user: User)
    suspend fun find(id: Long): User?
    suspend fun update(user: User) : Boolean
}

class UsersRepositoryImpl : UsersRepository {

    override suspend fun findAll(): List<User> = suspendTransaction {
        UserDAO.all()
            .orderBy()
            .map(UserDAO::toUser)
    }

    override suspend fun update(user: User): Boolean = suspendTransaction {
        UserDAO.findByIdAndUpdate(user.userId) {
            it.name = user.name
            it.email = user.email
            it.link = user.link
            it.aboutMe = user.aboutMe
        }?.also { userDao ->
            user.content.forEach { content ->
                ContentDAO.findByIdAndUpdate(content.contentId) {
                    it.text = content.text
                    it.createdAt = content.createdAt
                    it.author = userDao
                }
            }
        } != null
    }

    override suspend fun save(user: User) = suspendTransaction {
        val newUser: UserDAO = UserDAO.new {
            this.name = user.name
            this.userType = user.userType
            this.link = user.link
            this.email = user.email
            this.aboutMe = user.aboutMe
        }

        user.content.forEach {
            ContentDAO.new(id = it.contentId) {
                text = it.text
                createdAt = it.createdAt
                author = newUser
            }
        }
    }

    override suspend fun find(id: Long): User? = suspendTransaction {
        UserDAO.findById(id)?.toUser()
    }

}

suspend fun <T> suspendTransaction(block: suspend () -> T): T =
    newSuspendedTransaction(Dispatchers.IO) { block() }

// by using with(environment.config) we can get access to the application.conf configuration file
fun Application.connectToDatabase() /* = with(environment.config) */ {
    val dataSource = Database.connect(configureDataSource(false))
    transaction(dataSource) {
        SchemaUtils.createMissingTablesAndColumns(UserTable, ContentTable)
    }
}

fun configureDataSource(embedded: Boolean): HikariDataSource = HikariDataSource(HikariConfig().apply {
    setPoolName("Database pool")
    addDataSourceProperty("cachePrepStmts", "true")
    addDataSourceProperty("prepStmtCacheSize", "250")
    addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
    setMaximumPoolSize(20)

    if (embedded) {
        username = "root"
        password = ""
        driverClassName = "org.h2.Driver"
        jdbcUrl = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"
    } else {
        Class.forName("org.postgresql.Driver")
        username = "test" //System.getenv("postgres.user")
        password = "test" //System.getenv("postgres.password")
        driverClassName = "org.postgresql.Driver"
        jdbcUrl = "jdbc:postgresql://localhost:5432/test" //System.getenv("postgres.url")
    }
})
