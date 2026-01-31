package com.gattaca.api

import com.gattaca.domain.*
import com.gattaca.service.CandidateService
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class CreateCandidateRequest(
    val name: String,
    val email: String,
    val githubProfile: String? = null
)

fun Route.candidateRoutes(candidateService: CandidateService) {
    route("/candidates") {
        get {
            call.respond(candidateService.findAll())
        }

        get("/{id}") {
            val id = try {
                Uuid.parse(call.parameters["id"]!!)
            } catch (e: Exception) {
                throw GattacaException(ErrorCode.BAD_REQUEST, "Invalid ID", status = 400)
            }
            call.respond(candidateService.findById(id))
        }

        post {
            val request = call.receive<CreateCandidateRequest>()
            val id = candidateService.create(request.name, request.email, request.githubProfile)
            call.respond(HttpStatusCode.Created, mapOf("id" to id))
        }

        put("/{id}") {
            val id = try {
                Uuid.parse(call.parameters["id"]!!)
            } catch (e: Exception) {
                throw GattacaException(ErrorCode.BAD_REQUEST, "Invalid ID", status = 400)
            }
            val request = call.receive<CreateCandidateRequest>()
            candidateService.update(id, request.name, request.email, request.githubProfile)
            call.respond(HttpStatusCode.OK)
        }

        delete("/{id}") {
            val id = try {
                Uuid.parse(call.parameters["id"]!!)
            } catch (e: Exception) {
                throw GattacaException(ErrorCode.BAD_REQUEST, "Invalid ID", status = 400)
            }
            candidateService.delete(id)
            call.respond(HttpStatusCode.NoContent)
        }
    }
}