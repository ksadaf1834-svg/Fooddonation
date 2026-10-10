package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_partners")
data class FoodPartnerEntity(
  @PrimaryKey
  val id: String,
  val userId: String,
  val businessName: String,
  val contactPerson: String,
  val email: String,
  val phone: String,
  val businessType: String, // "Restaurant", "Bakery", "Grocery / Food Store", "Supermarket", "Community Kitchen"
  val address: String,
  val latitude: Double = 0.0,
  val longitude: Double = 0.0,
  val donationDetails: String,
  val approvalStatus: String = "PENDING", // PENDING, APPROVED, REJECTED
  val isAvailableForDonations: Boolean = true,
  val createdAt: Long = System.currentTimeMillis()
)
