package com.koroliuk.security

import java.security.SecureRandom
import java.security.spec.KeySpec
import java.util.*
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

class CustomPasswordEncoder {
    private val secureRandom = SecureRandom()
    private val iterations = 65536
    private val keyLength = 256

    fun encode(password: String): String {
        val salt = ByteArray(16)
        secureRandom.nextBytes(salt)

        val keySpec: KeySpec = PBEKeySpec(password.toCharArray(), salt, iterations, keyLength)
        val secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        val hash = secretKeyFactory.generateSecret(keySpec).encoded

        return Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash)
    }

    fun matches(password: String, hashedPassword: String): Boolean {
        val parts = hashedPassword.split(":")
        val salt = Base64.getDecoder().decode(parts[0])
        val storedHash = Base64.getDecoder().decode(parts[1])

        val keySpec: KeySpec = PBEKeySpec(password.toCharArray(), salt, iterations, keyLength)
        val secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        val newHash = secretKeyFactory.generateSecret(keySpec).encoded

        return storedHash.contentEquals(newHash)
    }
}
