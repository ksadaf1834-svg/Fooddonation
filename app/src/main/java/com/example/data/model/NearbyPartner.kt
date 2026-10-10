package com.example.data.model

data class NearbyPartner(
  val id: String,
  val name: String,
  val category: String, // "Restaurant", "Bakery", "Grocery Store", "NGO"
  val address: String,
  val phone: String,
  val latitude: Double,
  val longitude: Double,
  val distanceKm: Double,
  val donationAvailability: String,
  val isRegisteredPartner: Boolean = false
)
