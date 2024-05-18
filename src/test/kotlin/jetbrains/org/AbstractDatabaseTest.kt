package jetbrains.org

import jetbrains.org.db.ContentTable
import jetbrains.org.db.UserTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.wait.strategy.HostPortWaitStrategy

abstract class AbstractDatabaseTest {

    companion object {
        @JvmField
        val postgres = PostgreSQLContainer("postgres")
            .waitingFor(HostPortWaitStrategy())

        init {
            postgres.start()
            val url = postgres.jdbcUrl
            val user = postgres.username
            val password = postgres.password
            val database = Database.connect(url, user = user, password = password)
            transaction(database) {
                SchemaUtils.createMissingTablesAndColumns(UserTable, ContentTable)
            }
        }
    }

}