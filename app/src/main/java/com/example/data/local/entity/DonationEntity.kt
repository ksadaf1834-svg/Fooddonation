package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "donations")
data class DonationEntity(
  @PrimaryKey
  val id: String,
  val donorId: String,
  val donorName: String,
  val foodTitle: String,
  val description: String,
  val quantity: String,
  val foodType: String,
  val perishable: Boolean,
  val pickupAddress: String,
  val latitude: Double = 0.0,
  val longitude: Double = 0.0,
  val deadlineTime: String,
  val status: String = "AVAILABLE", // AVAILABLE, REQUESTED, CLAIMED, COMPLETED, CANCELLED, EXPIRED
  val createdAt: Long = System.currentTimeMillis()
)
