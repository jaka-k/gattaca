package com.gattaca.domain

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

// --- Models ---

@Serializable
data class Organization(val id: Uuid? = null, val name: String)

@Serializable
data class User(val id: Uuid? = null, val organizationId: Uuid, val name: String, val email: String, val passwordHash: String? = null)

@Serializable
data class UserSession(val sessionId: String, val userId: String, val accessToken: String? = null)

@Serializable
data class Exercise(val id: Uuid? = null, val creatorId: Uuid, val title: String, val description: String)

@Serializable
data class Candidate(val id: Uuid? = null, val name: String, val email: String, val githubProfile: String? = null)

@Serializable
data class Evaluation(
    val id: Uuid? = null, 
    val candidateId: Uuid, 
    val exerciseId: Uuid, 
    val score: Int, 
    val feedback: String
)

// --- Repository Interfaces (Ports) ---

interface OrganizationRepository {
    suspend fun save(org: Organization): Uuid
    suspend fun findById(id: Uuid): Organization?
    suspend fun findAll(): List<Organization>
    suspend fun update(org: Organization): Boolean
    suspend fun delete(id: Uuid): Boolean
}

interface UserRepository {
    suspend fun save(user: User): Uuid
    suspend fun findById(id: Uuid): User?
    suspend fun findAll(): List<User>
    suspend fun findByOrganizationId(orgId: Uuid): List<User>
    suspend fun findByEmail(email: String): User?
    suspend fun update(user: User): Boolean
    suspend fun delete(id: Uuid): Boolean
}

interface ExerciseRepository {
    suspend fun save(exercise: Exercise): Uuid
    suspend fun findById(id: Uuid): Exercise?
    suspend fun findAll(): List<Exercise>
    suspend fun update(exercise: Exercise): Boolean
    suspend fun delete(id: Uuid): Boolean
}

interface CandidateRepository {
    suspend fun save(candidate: Candidate): Uuid
    suspend fun findById(id: Uuid): Candidate?
    suspend fun findAll(): List<Candidate>
    suspend fun findByEmail(email: String): Candidate?
    suspend fun update(candidate: Candidate): Boolean
    suspend fun delete(id: Uuid): Boolean
}

interface EvaluationRepository {
    suspend fun save(evaluation: Evaluation): Uuid
    suspend fun findByCandidateId(candidateId: Uuid): List<Evaluation>
}

interface SessionRepository {
    suspend fun save(session: UserSession)
    suspend fun findById(sessionId: String): UserSession?
    suspend fun delete(sessionId: String)
}

// --- Exceptions ---

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

// --- Utility Interfaces ---

interface PasswordHasher {
    fun hash(password: String): String
}
