package jetbrains.org.routing

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import jetbrains.org.db.UsersRepository
import jetbrains.org.model.User
import org.koin.ktor.ext.inject

fun Application.configureRouting() {

    val repository by inject<UsersRepository>()

    routing {
        get("/") {
            call.respondText("Hello World!")
        }
    }

    routing {
        route("/users") {
            get {
                call.respond(repository.findAll())
            }
            get("/{userId}") {
                call.parameters["userId"]?.let {
                    val user = repository.find(it.toLong())
                    if (user != null) {
                        call.respond(user)
                    } else {
                        call.respond(HttpStatusCode.NotFound)
                    }
                }
            }
            authenticate("auth-jwt") {
                post {
                    val user = call.receive<User>()
                    repository.save(user)
                    call.respond(status = HttpStatusCode.Created, message = "Data added successfully")
                }
                put("/{userId}") {
                    val userId = call.parameters["userId"]?.toLong()
                    val updatedUser = call.receive<User>()
                    if (userId != null) {
                        val update = repository.update(updatedUser)
                        if (update) {
                            call.respond(status = HttpStatusCode.OK, message = "Data updated successfully")
                        } else {
                            call.respond(status = HttpStatusCode.NotFound, message = "Data to update not found")
                        }
                    }
                }
            }
            delete("/{userId}") {
                TODO("We don't delete anything")
            }
        }
    }
}
