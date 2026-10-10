package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "volunteer_tasks")
data class VolunteerTaskEntity(
  @PrimaryKey
  val id: String,
  val donationId: String,
  val requestId: String = "",
  val taskType: String, // "Food Pickup", "Food Delivery", "Food Distribution"
  val title: String,
  val foodDetails: String,
  val pickupLocation: String,
  val pickupLat: Double = 0.0,
  val pickupLng: Double = 0.0,
  val deliveryLocation: String,
  val deliveryLat: Double = 0.0,
  val deliveryLng: Double = 0.0,
  val requiredTime: String,
  val status: String = "Available", // "Available", "Accepted", "In Progress", "Completed", "Cancelled"
  val assignedVolunteerId: String? = null,
  val assignedVolunteerName: String? = null,
  val assignedVolunteerPhone: String? = null,
  val createdAt: Long = System.currentTimeMillis(),
  val updatedAt: Long = System.currentTimeMillis()
)
