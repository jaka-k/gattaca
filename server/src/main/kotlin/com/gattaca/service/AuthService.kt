package com.gattaca.service

import com.gattaca.domain.*

class AuthService(
    private val userRepo: UserRepository,
    private val passwordHasher: PasswordHasher
) {
    
    suspend fun login(email: String, password: String): User? {
        val user = userRepo.findByEmail(email)
        return if (user != null && user.passwordHash == passwordHasher.hash(password)) {
            user
        } else {
            null
        }
    }

    suspend fun register(name: String, email: String, password: String, organizationId: Int): Int {
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
}