package jetbrains.org.routing

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import jetbrains.org.db.fake.FakeUsersRepository
import jetbrains.org.model.User

val repository = FakeUsersRepository()

fun Application.configureRouting() {
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
            post {
                val user = call.receive<User>()
                repository.save(user)
                call.respond(status = HttpStatusCode.Created, message = "Data added successfully")
            }
            get("/{userId}") {
                call.parameters["userId"]?.let {
                    val user = repository.find(it.toInt())
                    if (user != null) {
                        call.respond(user)
                    } else {
                        call.respond(HttpStatusCode.NotFound)
                    }
                }
            }
            put("/{userId}") {
                val userId = call.parameters["userId"]?.toInt()

                val updatedUser = call.receive<User>()
                if (userId != null) {
                    val oldUser = repository.find(userId)
                    if (oldUser != null) {
                        repository.delete(oldUser)
                        repository.save(updatedUser)
                        call.respond(status = HttpStatusCode.OK, message = "Data updated successfully")
                    } else {
                        call.respond(status = HttpStatusCode.NotFound, message = "Data to update not found")
                    }
                }
            }
            delete("/{userId}") {
                val userId = call.parameters["userId"]?.toInt()
                if (userId != null) {
                    val deleted = repository.find(userId)
                    if (deleted != null) {
                        repository.delete(deleted)
                        call.respond(status = HttpStatusCode.OK, message = "Data deleted successfully")
                    } else {
                        call.respond(status = HttpStatusCode.NotFound, message = "Data to delete not found")
                    }
                }
            }
        }
    }
}
