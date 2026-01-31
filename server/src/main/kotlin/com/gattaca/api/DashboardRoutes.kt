package com.gattaca.api

import com.gattaca.domain.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.dashboardRoutes(
    orgRepo: OrganizationRepository,
    userRepo: UserRepository,
    exerciseRepo: ExerciseRepository,
    candidateRepo: CandidateRepository,
    evaluationRepo: EvaluationRepository
) {
    route("/dashboard") {
        post("/organizations") {
            val org = call.receive<Organization>()
            val id = orgRepo.save(org)
            call.respond(HttpStatusCode.Created, mapOf("id" to id))
        }

        post("/exercises") {
            val exercise = call.receive<Exercise>()
            val id = exerciseRepo.save(exercise)
            call.respond(HttpStatusCode.Created, mapOf("id" to id))
        }

        post("/candidates") {
            val candidate = call.receive<Candidate>()
            val id = candidateRepo.save(candidate)
            call.respond(HttpStatusCode.Created, mapOf("id" to id))
        }

        post("/evaluations") {
            val evaluation = call.receive<Evaluation>()
            val id = evaluationRepo.save(evaluation)
            call.respond(HttpStatusCode.Created, mapOf("id" to id))
        }

        get("/candidates/{id}/evaluations") {
            val id = call.parameters["id"]?.toIntOrNull() ?: throw GattacaException(
                ErrorCode.BAD_REQUEST,
                "Invalid candidate ID",
                status = 400
            )
            val evaluations = evaluationRepo.findByCandidateId(id)
            if (evaluations.isEmpty()) {
                throw GattacaException(
                    ErrorCode.CANDIDATE_NOT_FOUND,
                    "No evaluations found for candidate $id",
                    developerDetails = "Database query returned empty list for candidateId=$id",
                    status = 404
                )
            }
            call.respond(evaluations)
        }
    }
}