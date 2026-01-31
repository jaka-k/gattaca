package com.gattaca.api

import com.gattaca.domain.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class CreateExerciseRequest(
    val creatorId: Int,
    val title: String,
    val description: String
)

fun Route.exerciseRoutes(exerciseRepo: ExerciseRepository) {
    route("/exercises") {
        get {
            call.respond(exerciseRepo.findAll())
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                return@get
            }
            val exercise = exerciseRepo.findById(id)
            if (exercise == null) {
                call.respond(HttpStatusCode.NotFound)
            } else {
                call.respond(exercise)
            }
        }

        post {
            val request = call.receive<CreateExerciseRequest>()
            val newExercise = Exercise(
                creatorId = request.creatorId,
                title = request.title,
                description = request.description
            )
            val id = exerciseRepo.save(newExercise)
            call.respond(HttpStatusCode.Created, mapOf("id" to id))
        }

        put("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                return@put
            }
            val request = call.receive<CreateExerciseRequest>()
            // Check existence
            if (exerciseRepo.findById(id) == null) {
                call.respond(HttpStatusCode.NotFound)
                return@put
            }
            val updatedExercise = Exercise(
                id = id,
                creatorId = request.creatorId,
                title = request.title,
                description = request.description
            )
            exerciseRepo.update(updatedExercise)
            call.respond(HttpStatusCode.OK)
        }

        delete("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                return@delete
            }
            if (exerciseRepo.delete(id)) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}
