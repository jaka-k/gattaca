package com.gattaca.adapters.inbound.web.candidate

import com.gattaca.Candidate
import com.gattaca.repository.CandidateRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class CreateCandidateRequest(
    val name: String,
    val email: String,
    val githubProfile: String? = null
)

fun Route.candidateRoutes(candidateRepo: CandidateRepository) {
    route("/candidates") {
        get {
            call.respond(candidateRepo.findAll())
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                return@get
            }
            val candidate = candidateRepo.findById(id)
            if (candidate == null) {
                call.respond(HttpStatusCode.NotFound)
            } else {
                call.respond(candidate)
            }
        }

        post {
            val request = call.receive<CreateCandidateRequest>()
            if (candidateRepo.findByEmail(request.email) != null) {
                call.respond(HttpStatusCode.Conflict, "Candidate already exists")
                return@post
            }
            val newCandidate = Candidate(
                name = request.name,
                email = request.email,
                githubProfile = request.githubProfile
            )
            val id = candidateRepo.save(newCandidate)
            call.respond(HttpStatusCode.Created, mapOf("id" to id))
        }

        put("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                return@put
            }
            val request = call.receive<CreateCandidateRequest>()
            // Check existence
            if (candidateRepo.findById(id) == null) {
                call.respond(HttpStatusCode.NotFound)
                return@put
            }
            val updatedCandidate = Candidate(
                id = id,
                name = request.name,
                email = request.email,
                githubProfile = request.githubProfile
            )
            candidateRepo.update(updatedCandidate)
            call.respond(HttpStatusCode.OK)
        }

        delete("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                return@delete
            }
            if (candidateRepo.delete(id)) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}
