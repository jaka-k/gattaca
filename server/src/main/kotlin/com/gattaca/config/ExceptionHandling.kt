package com.gattaca.config

import com.gattaca.domain.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val code: String,
    val message: String,
    val details: String? = null
)

fun Application.configureExceptionHandling() {
    val isDevelopment = environment.config.propertyOrNull("ktor.development")?.getString()?.toBoolean() ?: false

    install(StatusPages) {
        exception<GattacaException> { call, cause ->
            val response = if (isDevelopment) {
                ErrorResponse(
                    code = cause.errorCode.name,
                    message = cause.message,
                    details = cause.developerDetails ?: cause.cause?.stackTraceToString()
                )
            } else {
                ErrorResponse(
                    code = cause.errorCode.name,
                    message = cause.message
                )
            }
            call.respond(HttpStatusCode.fromValue(cause.status), response)
        }

        exception<Throwable> { call, cause ->
            val response = if (isDevelopment) {
                ErrorResponse(
                    code = ErrorCode.INTERNAL_SERVER_ERROR.name,
                    message = cause.message ?: "An unexpected error occurred",
                    details = cause.stackTraceToString()
                )
            } else {
                ErrorResponse(
                    code = ErrorCode.INTERNAL_SERVER_ERROR.name,
                    message = "An internal server error occurred"
                )
            }
            call.respond(HttpStatusCode.InternalServerError, response)
        }
    }
}