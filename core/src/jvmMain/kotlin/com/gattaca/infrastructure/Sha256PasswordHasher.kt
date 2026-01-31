package com.gattaca.infrastructure

import com.gattaca.domain.PasswordHasher
import java.security.MessageDigest

class Sha256PasswordHasher : PasswordHasher {
    override fun hash(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
}
