package jetbrains.org.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import jetbrains.org.User

//FIXME: this is a hack!
var users = mutableListOf<User>()

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
    }

    routing {
        route("/users") {
            get {
                call.respond(users)
            }
            post {
                val user = call.receive<User>()
                users.add(user)
                call.respond(status = HttpStatusCode.Created, message = "Data added successfully")
            }
            get("/{userId}") {
                val userId = call.parameters["userId"]?.toInt() // FIXME: this is a bit ugly
                val user = users.find { it.userId == userId }
                if (user != null) {
                    call.respond(user)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
            put("/{userId}") {
                val userId = call.parameters["userId"]?.toInt()
                val updatedUser = call.receive<User>()
                val oldUser = users.find { it.userId == userId }
                if (oldUser != null) {
                    users.remove(oldUser)
                    users.add(updatedUser)
                    call.respond(status = HttpStatusCode.OK, message = "Data updated successfully")
                } else {
                    call.respond(status = HttpStatusCode.NotFound, message = "Data to update not found")
                }
            }
            delete("/{userId}") {
                val userId = call.parameters["userId"]?.toInt()
                val deleted = users.find { it.userId == userId }
                if (deleted != null) {
                    users.remove(deleted)
                    call.respond(status = HttpStatusCode.OK, message = "Data deleted successfully")
                } else {
                    call.respond(status = HttpStatusCode.NotFound, message = "Data to delete not found")
                }
            }
        }
    }


}
