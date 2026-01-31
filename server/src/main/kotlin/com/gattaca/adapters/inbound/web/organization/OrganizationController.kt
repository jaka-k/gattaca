package com.gattaca.adapters.inbound.web.organization

import com.gattaca.Organization
import com.gattaca.repository.OrganizationRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class CreateOrganizationRequest(val name: String)

fun Route.organizationRoutes(orgRepo: OrganizationRepository) {
    route("/organizations") {
        get {
            call.respond(orgRepo.findAll())
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                return@get
            }
            val org = orgRepo.findById(id)
            if (org == null) {
                call.respond(HttpStatusCode.NotFound)
            } else {
                call.respond(org)
            }
        }

        post {
            val request = call.receive<CreateOrganizationRequest>()
            val newOrg = Organization(name = request.name)
            val id = orgRepo.save(newOrg)
            call.respond(HttpStatusCode.Created, mapOf("id" to id))
        }

        put("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                return@put
            }
            val request = call.receive<CreateOrganizationRequest>()
            // Check existence
            if (orgRepo.findById(id) == null) {
                call.respond(HttpStatusCode.NotFound)
                return@put
            }
            val updatedOrg = Organization(id = id, name = request.name)
            orgRepo.update(updatedOrg)
            call.respond(HttpStatusCode.OK)
        }

        delete("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                return@delete
            }
            if (orgRepo.delete(id)) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}
