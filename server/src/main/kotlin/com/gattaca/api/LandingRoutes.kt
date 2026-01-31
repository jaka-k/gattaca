package com.gattaca.api

import com.gattaca.domain.*
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
