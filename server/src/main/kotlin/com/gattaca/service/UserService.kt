package com.gattaca.service

import com.gattaca.domain.*

class UserService(
    private val userRepo: UserRepository,
    private val passwordHasher: PasswordHasher
) {
    suspend fun findAll() = userRepo.findAll()

    suspend fun findById(id: Int) = userRepo.findById(id)
        ?: throw GattacaException(ErrorCode.USER_NOT_FOUND, "User not found", status = 404)

    suspend fun create(organizationId: Int, name: String, email: String, password: String): Int {
        if (userRepo.findByEmail(email) != null) {
            throw GattacaException(ErrorCode.BAD_REQUEST, "User already exists", status = 409)
        }
        val newUser = User(
            organizationId = organizationId,
            name = name,
            email = email,
            passwordHash = passwordHasher.hash(password)
        )
        return userRepo.save(newUser)
    }

    suspend fun update(id: Int, organizationId: Int, name: String, email: String, password: String?) {
        val existing = userRepo.findById(id) ?: throw GattacaException(ErrorCode.USER_NOT_FOUND, "User not found", status = 404)
        
        val newPasswordHash = if (!password.isNullOrBlank()) {
            passwordHasher.hash(password)
        } else {
            existing.passwordHash
        }

        userRepo.update(existing.copy(
            organizationId = organizationId,
            name = name,
            email = email,
            passwordHash = newPasswordHash
        ))
    }

    suspend fun delete(id: Int) {
        if (!userRepo.delete(id)) {
            throw GattacaException(ErrorCode.USER_NOT_FOUND, "User not found", status = 404)
        }
    }
}
