package com.gattaca.adapters.inbound.web.landing

import com.gattaca.domain.ports.inbound.ICityService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.landingRoutes(cityService: ICityService) {
    route("/landing") {
        get("/info") {
            call.respondText("Welcome to the Landing Page. We serve data from our domain.")
        }
        
        get("/cities/{id}") {
            val id = call.parameters["id"]?.toIntOrNull() ?: return@get call.respondText("Invalid ID", status = io.ktor.http.HttpStatusCode.BadRequest)
            try {
                val city = cityService.getCity(id)
                call.respond(city)
            } catch (e: Exception) {
                call.respond(io.ktor.http.HttpStatusCode.NotFound)
            }
        }
    }
}
