package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
  @PrimaryKey
  val id: String,
  val email: String,
  val passwordHash: String,
  val fullName: String,
  val phone: String,
  val role: String, // "DONOR", "RECEIVER", "VOLUNTEER", "FOOD_PARTNER", "ADMIN"
  val isApproved: Boolean = true, // Food partners require admin approval
  val isSuspended: Boolean = false,
  val createdAt: Long = System.currentTimeMillis()
)
