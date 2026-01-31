package com.gattaca.domain

import kotlinx.serialization.Serializable

// --- Models ---

@Serializable
data class Organization(val id: Int? = null, val name: String)

@Serializable
data class User(val id: Int? = null, val organizationId: Int, val name: String, val email: String, val passwordHash: String? = null)

@Serializable
data class Exercise(val id: Int? = null, val creatorId: Int, val title: String, val description: String)

@Serializable
data class Candidate(val id: Int? = null, val name: String, val email: String, val githubProfile: String? = null)

@Serializable
data class Evaluation(
    val id: Int? = null, 
    val candidateId: Int, 
    val exerciseId: Int, 
    val score: Int, 
    val feedback: String
)

// --- Repository Interfaces (Ports) ---

interface OrganizationRepository {
    suspend fun save(org: Organization): Int
    suspend fun findById(id: Int): Organization?
    suspend fun findAll(): List<Organization>
    suspend fun update(org: Organization): Boolean
    suspend fun delete(id: Int): Boolean
}

interface UserRepository {
    suspend fun save(user: User): Int
    suspend fun findById(id: Int): User?
    suspend fun findAll(): List<User>
    suspend fun findByOrganizationId(orgId: Int): List<User>
    suspend fun findByEmail(email: String): User?
    suspend fun update(user: User): Boolean
    suspend fun delete(id: Int): Boolean
}

interface ExerciseRepository {
    suspend fun save(exercise: Exercise): Int
    suspend fun findById(id: Int): Exercise?
    suspend fun findAll(): List<Exercise>
    suspend fun update(exercise: Exercise): Boolean
    suspend fun delete(id: Int): Boolean
}

interface CandidateRepository {
    suspend fun save(candidate: Candidate): Int
    suspend fun findById(id: Int): Candidate?
    suspend fun findAll(): List<Candidate>
    suspend fun findByEmail(email: String): Candidate?
    suspend fun update(candidate: Candidate): Boolean
    suspend fun delete(id: Int): Boolean
}

interface EvaluationRepository {
    suspend fun save(evaluation: Evaluation): Int
    suspend fun findByCandidateId(candidateId: Int): List<Evaluation>
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
