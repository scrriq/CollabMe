package com.example.features.auth

import org.mindrot.jbcrypt.BCrypt

object PasswordHasher {
    fun hash(rawPassword: String): String = BCrypt.hashpw(rawPassword, BCrypt.gensalt())

    fun verify(rawPassword: String, hash: String): Boolean = BCrypt.checkpw(rawPassword, hash)
}
