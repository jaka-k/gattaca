package com.gattaca.service

import com.gattaca.domain.*
import kotlin.uuid.Uuid

class CandidateService(private val candidateRepo: CandidateRepository) {
    suspend fun findAll() = candidateRepo.findAll()
    
    suspend fun findById(id: Uuid) = candidateRepo.findById(id) 
        ?: throw GattacaException(ErrorCode.CANDIDATE_NOT_FOUND, "Candidate not found", status = 404)

    suspend fun create(name: String, email: String, githubProfile: String?): Uuid {
        if (candidateRepo.findByEmail(email) != null) {
            throw GattacaException(ErrorCode.BAD_REQUEST, "Candidate already exists", status = 409)
        }
        val candidate = Candidate(name = name, email = email, githubProfile = githubProfile)
        return candidateRepo.save(candidate)
    }

    suspend fun update(id: Uuid, name: String, email: String, githubProfile: String?) {
        val existing = candidateRepo.findById(id) ?: throw GattacaException(ErrorCode.CANDIDATE_NOT_FOUND, "Candidate not found", status = 404)
        candidateRepo.update(existing.copy(name = name, email = email, githubProfile = githubProfile))
    }

    suspend fun delete(id: Uuid) {
        if (!candidateRepo.delete(id)) {
            throw GattacaException(ErrorCode.CANDIDATE_NOT_FOUND, "Candidate not found", status = 404)
        }
    }
}
