package com.gattaca.adapters.inbound.web.dashboard

import com.gattaca.City
import com.gattaca.domain.ports.inbound.ICityService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.dashboardRoutes(cityService: ICityService) {
    route("/dashboard") {
        get("/stats") {
            call.respondText("Dashboard Statistics Overview")
        }

        post("/cities") {
            val city = call.receive<City>()
            val id = cityService.createCity(city)
            call.respond(HttpStatusCode.Created, mapOf("id" to id))
        }

        put("/cities/{id}") {
            val id = call.parameters["id"]?.toIntOrNull() ?: return@put call.respond(HttpStatusCode.BadRequest)
            val city = call.receive<City>()
            cityService.updateCity(id, city)
            call.respond(HttpStatusCode.OK)
        }

        delete("/cities/{id}") {
            val id = call.parameters["id"]?.toIntOrNull() ?: return@delete call.respond(HttpStatusCode.BadRequest)
            cityService.removeCity(id)
            call.respond(HttpStatusCode.OK)
        }
    }
}
