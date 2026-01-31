package com.gattaca.api

import com.gattaca.domain.*
import com.gattaca.service.AuthService
import com.gattaca.config.KtorUserSession
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.serialization.Serializable
import io.ktor.util.*
import kotlin.uuid.Uuid

@Serializable
data class LoginRequest(val email: String, val password: String)

@Serializable
data class RegisterRequest(val name: String, val email: String, val password: String, val organizationId: Uuid)

fun Route.authRoutes(authService: AuthService) {
    route("/auth") {
        post("/login") {
            val request = call.receive<LoginRequest>()
            val user = authService.login(request.email, request.password)
            
            if (user != null) {
                // Generate a session ID
                val sessionId = generateNonce()
                call.sessions.set(KtorUserSession(sessionId = sessionId, userId = user.id.toString()))
                call.respond(HttpStatusCode.OK, "Logged in")
            } else {
                call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")
            }
        }

        post("/logout") {
            call.sessions.clear<KtorUserSession>()
            call.respond(HttpStatusCode.OK, "Logged out")
        }

        post("/register") {
            val request = call.receive<RegisterRequest>()
            val id = authService.register(request.name, request.email, request.password, request.organizationId)
            call.respond(HttpStatusCode.Created, mapOf("id" to id))
        }
    }
}