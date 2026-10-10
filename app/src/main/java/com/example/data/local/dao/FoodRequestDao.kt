package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.FoodRequestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodRequestDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertRequest(request: FoodRequestEntity)

  @Update
  suspend fun updateRequest(request: FoodRequestEntity)

  @Query("SELECT * FROM food_requests ORDER BY createdAt DESC")
  fun getAllRequests(): Flow<List<FoodRequestEntity>>

  @Query("SELECT * FROM food_requests WHERE donationId = :donationId ORDER BY createdAt DESC")
  fun getRequestsForDonation(donationId: String): Flow<List<FoodRequestEntity>>

  @Query("SELECT * FROM food_requests WHERE receiverId = :receiverId ORDER BY createdAt DESC")
  fun getRequestsByReceiver(receiverId: String): Flow<List<FoodRequestEntity>>

  @Query("SELECT * FROM food_requests WHERE id = :id LIMIT 1")
  suspend fun getRequestById(id: String): FoodRequestEntity?

  @Query("UPDATE food_requests SET status = :status WHERE id = :id")
  suspend fun updateRequestStatus(id: String, status: String)
}
