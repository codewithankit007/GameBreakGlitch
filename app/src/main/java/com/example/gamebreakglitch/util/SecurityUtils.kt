package com.example.gamebreakglitch.util

import java.security.MessageDigest

object SecurityUtils {
    fun hashPin(pin: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(pin.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun verifyPin(inputPin: String, storedHash: String): Boolean {
        return hashPin(inputPin) == storedHash
    }
}
