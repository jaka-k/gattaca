package com.gattaca.adapters.inbound.web.landing

import com.gattaca.repository.ExerciseRepository
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.landingRoutes(exerciseRepository: ExerciseRepository) {
    route("/landing") {
        get("/exercises") {
            val exercises = exerciseRepository.findAll()
            call.respond(exercises)
        }
    }
}