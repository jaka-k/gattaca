package com.gattaca

import com.gattaca.adapters.inbound.web.dashboard.dashboardRoutes
import com.gattaca.adapters.inbound.web.landing.landingRoutes
import com.gattaca.domain.ports.inbound.ICityService
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*

fun Application.configureRouting(cityService: ICityService) {
    routing {
        get("/") {
            // call.respondText("Hello World!")
        }
        // Static plugin. Try to access `/static/index.html`
        staticResources("/static", "static")
        
        // Register Inbound Adapters (Controllers)
        landingRoutes(cityService)
        dashboardRoutes(cityService)
    }
}