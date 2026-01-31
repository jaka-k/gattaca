package com.gattaca

import kotlinx.serialization.Serializable

@Serializable
enum class ErrorCode {
    ORGANIZATION_NOT_FOUND,
    USER_NOT_FOUND,
    EXERCISE_NOT_FOUND,
    CANDIDATE_NOT_FOUND,
    EVALUATION_FAILED,
    INTERNAL_SERVER_ERROR,
    UNAUTHORIZED,
    BAD_REQUEST
}

open class GattacaException(
    val errorCode: ErrorCode,
    override val message: String,
    val developerDetails: String? = null,
    val status: Int = 500,
    cause: Throwable? = null
) : Exception(message, cause)
