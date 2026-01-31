package com.gattaca.adapters.inbound.web.user

import com.gattaca.User
import com.gattaca.repository.UserRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import java.security.MessageDigest

@Serializable
data class CreateUserRequest(
    val organizationId: Int,
    val name: String,
    val email: String,
    val password: String
)

@Serializable
data class UpdateUserRequest(
    val organizationId: Int,
    val name: String,
    val email: String,
    val password: String? = null
)

@Serializable
data class UserResponse(
    val id: Int,
    val organizationId: Int,
    val name: String,
    val email: String
)

fun User.toResponse(): UserResponse {
    return UserResponse(
        id = this.id ?: 0,
        organizationId = this.organizationId,
        name = this.name,
        email = this.email
    )
}

fun Route.userRoutes(userRepo: UserRepository) {
    route("/users") {
        get {
            call.respond(userRepo.findAll().map { it.toResponse() })
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                return@get
            }
            val user = userRepo.findById(id)
            if (user == null) {
                call.respond(HttpStatusCode.NotFound)
            } else {
                call.respond(user.toResponse())
            }
        }

        post {
            val request = call.receive<CreateUserRequest>()
            if (userRepo.findByEmail(request.email) != null) {
                call.respond(HttpStatusCode.Conflict, "User already exists")
                return@post
            }
            val newUser = User(
                organizationId = request.organizationId,
                name = request.name,
                email = request.email,
                passwordHash = hashPassword(request.password)
            )
            val id = userRepo.save(newUser)
            call.respond(HttpStatusCode.Created, mapOf("id" to id))
        }

        put("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                return@put
            }
            val request = call.receive<UpdateUserRequest>()
            
            val existingUser = userRepo.findById(id)
            if (existingUser == null) {
                call.respond(HttpStatusCode.NotFound)
                return@put
            }

            val newPasswordHash = if (!request.password.isNullOrBlank()) {
                hashPassword(request.password)
            } else {
                existingUser.passwordHash
            }

            val updatedUser = existingUser.copy(
                organizationId = request.organizationId,
                name = request.name,
                email = request.email,
                passwordHash = newPasswordHash
            )
            
            userRepo.update(updatedUser)
            call.respond(HttpStatusCode.OK)
        }

        delete("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                return@delete
            }
            if (userRepo.delete(id)) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}

// Simple SHA-256 hashing (Duplicated from AuthController - refactor to common util later)
private fun hashPassword(password: String): String {
    val bytes = password.toByteArray()
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(bytes)
    return digest.fold("") { str, it -> str + "%02x".format(it) }
}
