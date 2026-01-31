package com.gattaca.repository

import com.gattaca.*

interface OrganizationRepository {
    suspend fun save(org: Organization): Int
    suspend fun findById(id: Int): Organization?
}

interface UserRepository {
    suspend fun save(user: User): Int
    suspend fun findById(id: Int): User?
    suspend fun findByOrganizationId(orgId: Int): List<User>
    suspend fun findByEmail(email: String): User?
}

interface ExerciseRepository {
    suspend fun save(exercise: Exercise): Int
    suspend fun findById(id: Int): Exercise?
    suspend fun findAll(): List<Exercise>
}

interface CandidateRepository {
    suspend fun save(candidate: Candidate): Int
    suspend fun findById(id: Int): Candidate?
    suspend fun findAll(): List<Candidate>
}

interface EvaluationRepository {
    suspend fun save(evaluation: Evaluation): Int
    suspend fun findByCandidateId(candidateId: Int): List<Evaluation>
}
