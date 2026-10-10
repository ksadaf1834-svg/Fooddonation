package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "volunteers")
data class VolunteerEntity(
  @PrimaryKey
  val id: String,
  val userId: String,
  val fullName: String,
  val phone: String,
  val location: String,
  val latitude: Double = 0.0,
  val longitude: Double = 0.0,
  val availabilityStatus: String = "Available", // "Available", "Busy", "Weekends Only", "On Call"
  val preferredTaskType: String = "Food Delivery", // "Food Pickup", "Food Delivery", "Food Distribution", "Any"
  val vehicleType: String = "Car", // "Bicycle", "Motorcycle", "Car", "Van", "On Foot"
  val completedTaskCount: Int = 0,
  val updatedAt: Long = System.currentTimeMillis()
)
