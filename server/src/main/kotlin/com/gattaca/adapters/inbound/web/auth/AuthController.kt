package com.gattaca.adapters.inbound.web.auth

import com.gattaca.User
import com.gattaca.UserSession
import com.gattaca.repository.UserRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.serialization.Serializable
import java.security.MessageDigest

@Serializable
data class LoginRequest(val email: String, val password: String)

@Serializable
data class RegisterRequest(val name: String, val email: String, val password: String, val organizationId: Int)

fun Route.authRoutes(userRepo: UserRepository) {
    route("/auth") {
        post("/login") {
            val request = call.receive<LoginRequest>()
            val user = userRepo.findByEmail(request.email)
            
            if (user != null && user.passwordHash == hashPassword(request.password)) {
                call.sessions.set(UserSession(user.id.toString())) // Storing user ID in session
                call.respond(HttpStatusCode.OK, "Logged in")
            } else {
                call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")
            }
        }

        post("/logout") {
            call.sessions.clear<UserSession>()
            call.respond(HttpStatusCode.OK, "Logged out")
        }

        post("/register") {
            val request = call.receive<RegisterRequest>()
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
    }
}

// Simple SHA-256 hashing (In production, use BCrypt or Argon2)
private fun hashPassword(password: String): String {
    val bytes = password.toByteArray()
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(bytes)
    return digest.fold("") { str, it -> str + "%02x".format(it) }
}
