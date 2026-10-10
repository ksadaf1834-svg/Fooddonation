package com.example.data.security

import java.security.MessageDigest

object PasswordHasher {
  private const val SALT = "FoodShareSecureSalt2026#"

  fun hashPassword(password: String): String {
    val md = MessageDigest.getInstance("SHA-256")
    val salted = password + SALT
    val bytes = md.digest(salted.toByteArray(Charsets.UTF_8))
    return bytes.joinToString("") { "%02x".format(it) }
  }

  fun verifyPassword(password: String, storedHash: String): Boolean {
    return hashPassword(password) == storedHash
  }
}
