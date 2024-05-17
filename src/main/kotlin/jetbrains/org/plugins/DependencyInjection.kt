package jetbrains.org.plugins

import io.ktor.server.application.*
import jetbrains.org.db.usersDataModule
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureKoin() {
    install(Koin) {
        slf4jLogger()
        modules(usersDataModule)
    }
}