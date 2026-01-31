package com.gattaca.api

import com.gattaca.domain.*
import com.gattaca.service.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class CreateUserRequest(
    val organizationId: Uuid,
    val name: String,
    val email: String,
    val password: String
)

@Serializable
data class UpdateUserRequest(
    val organizationId: Uuid,
    val name: String,
    val email: String,
    val password: String? = null
)

@Serializable
data class UserResponse(
    val id: Uuid,
    val organizationId: Uuid,
    val name: String,
    val email: String
)

fun User.toResponse(): UserResponse {
    return UserResponse(
        id = this.id ?: Uuid.random(), // Should not be null if persisted
        organizationId = this.organizationId,
        name = this.name,
        email = this.email
    )
}

fun Route.userRoutes(userService: UserService) {
    route("/users") {
        get {
            call.respond(userService.findAll().map { it.toResponse() })
        }

        get("/{id}") {
            val id = try {
                Uuid.parse(call.parameters["id"]!!)
            } catch (e: Exception) {
                throw GattacaException(ErrorCode.BAD_REQUEST, "Invalid ID", status = 400)
            }
            call.respond(userService.findById(id).toResponse())
        }

        post {
            val request = call.receive<CreateUserRequest>()
            val id = userService.create(request.organizationId, request.name, request.email, request.password)
            call.respond(HttpStatusCode.Created, mapOf("id" to id))
        }

        put("/{id}") {
            val id = try {
                Uuid.parse(call.parameters["id"]!!)
            } catch (e: Exception) {
                throw GattacaException(ErrorCode.BAD_REQUEST, "Invalid ID", status = 400)
            }
            val request = call.receive<UpdateUserRequest>()
            userService.update(id, request.organizationId, request.name, request.email, request.password)
            call.respond(HttpStatusCode.OK)
        }

        delete("/{id}") {
            val id = try {
                Uuid.parse(call.parameters["id"]!!)
            } catch (e: Exception) {
                throw GattacaException(ErrorCode.BAD_REQUEST, "Invalid ID", status = 400)
            }
            userService.delete(id)
            call.respond(HttpStatusCode.NoContent)
        }
    }
}
