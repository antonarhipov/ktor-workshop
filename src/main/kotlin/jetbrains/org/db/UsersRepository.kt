package jetbrains.org.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import jetbrains.org.model.Content
import jetbrains.org.model.User
import jetbrains.org.model.UserType
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
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
    suspend fun delete(user: User): Int
}

class UsersRepositoryImpl : UsersRepository {

    override suspend fun findAll(): List<User> = suspendTransaction {
        UserTable.selectAll()
            .orderBy(UserTable.id, SortOrder.ASC)
            .map { row: ResultRow ->
                User(
                    userId = row[UserTable.id],
                    name = row[UserTable.name],
                    userType = row[UserTable.userType],
                    link = row[UserTable.link],
                    email = row[UserTable.email],
                    aboutMe = row[UserTable.aboutMe]
                )
            }
            .map { user ->
                user.apply {
                    content += ContentTable.selectAll()
                        .where {
                            ContentTable.author eq user.userId
                        }.map { row ->
                            Content(
                                contentId = row[ContentTable.contentId],
                                text = row[ContentTable.text],
                                createdAt = row[ContentTable.createdAt],
                            )
                        }
                }
            }
    }

    override suspend fun save(user: User) = suspendTransaction {
        val insertedUserId = UserTable.insert { row ->
            row[UserTable.id] = user.userId
            row[UserTable.name] = user.name
            row[UserTable.userType] = user.userType
            row[UserTable.link] = user.link
            row[UserTable.email] = user.email
            row[UserTable.aboutMe] = user.aboutMe
        } get UserTable.id

        user.content.forEach {
            ContentTable.insert { row ->
                row[ContentTable.contentId] = it.contentId
                row[ContentTable.text] = it.text
                row[ContentTable.createdAt] = it.createdAt
                row[ContentTable.author] = insertedUserId
            }
        }
    }

    override suspend fun find(id: Long): User? = suspendTransaction {
        UserTable.selectAll().where { UserTable.id eq id }.singleOrNull()?.let { row ->
            User(
                userId = row[UserTable.id],
                name = row[UserTable.name],
                userType = UserType.REGISTERED,
                link = row[UserTable.link],
                email = row[UserTable.email],
                aboutMe = row[UserTable.aboutMe]
            )
        }?.apply {
            content += ContentTable.selectAll()
                .where {
                    ContentTable.author eq userId
                }.map { row ->
                    Content(
                        contentId = row[ContentTable.contentId],
                        text = row[ContentTable.text],
                        createdAt = row[ContentTable.createdAt],
                    )
                }
        }
    }

    override suspend fun delete(user: User): Int = suspendTransaction {
        UserTable.deleteWhere { UserTable.id eq user.userId }
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
