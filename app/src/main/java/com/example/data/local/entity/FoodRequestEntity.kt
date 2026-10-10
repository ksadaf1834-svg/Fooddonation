package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_requests")
data class FoodRequestEntity(
  @PrimaryKey
  val id: String,
  val donationId: String,
  val foodTitle: String,
  val receiverId: String,
  val receiverName: String,
  val receiverPhone: String,
  val organizationName: String,
  val deliveryAddress: String,
  val quantityRequested: String,
  val urgency: String = "MEDIUM", // HIGH, MEDIUM, LOW
  val notes: String = "",
  val status: String = "PENDING", // PENDING, ACCEPTED, REJECTED, CANCELLED, COMPLETED
  val createdAt: Long = System.currentTimeMillis()
)
