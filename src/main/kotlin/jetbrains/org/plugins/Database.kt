package jetbrains.org.plugins

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import jetbrains.org.db.UserTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

// by using with(environment.config) we can get access to the application.conf configuration file
fun Application.connectToDatabase() /* = with(environment.config) */ {
    val dataSource = Database.connect(configureDataSource(false))
    transaction(dataSource) {
        SchemaUtils.createMissingTablesAndColumns(UserTable)
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