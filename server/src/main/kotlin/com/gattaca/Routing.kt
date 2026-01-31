package com.gattaca

import com.gattaca.adapters.inbound.web.auth.authRoutes
import com.gattaca.adapters.inbound.web.dashboard.dashboardRoutes
import com.gattaca.adapters.inbound.web.landing.landingRoutes
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*

fun Application.configureRouting(repos: AppRepositories) {
    routing {
        staticResources("/static", "static")
        
        authRoutes(repos.userRepo)
        landingRoutes(repos.exerciseRepo)
        dashboardRoutes(
            repos.orgRepo,
            repos.userRepo,
            repos.exerciseRepo,
            repos.candidateRepo,
            repos.evaluationRepo
        )
    }
}
