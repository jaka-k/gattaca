package com.gattaca.config

import com.gattaca.api.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*

fun Application.configureRouting(deps: AppDependencies) {
    routing {
        staticResources("/static", "static")
        
        authRoutes(deps.authService)
        organizationRoutes(deps.orgRepo)
        userRoutes(deps.userService)
        exerciseRoutes(deps.exerciseRepo)
        candidateRoutes(deps.candidateService)
        landingRoutes(deps.exerciseRepo)
        dashboardRoutes(
            deps.orgRepo,
            deps.userRepo,
            deps.exerciseRepo,
            deps.candidateRepo,
            deps.evaluationRepo
        )
    }
}
