package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.DonationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DonationDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertDonation(donation: DonationEntity)

  @Update
  suspend fun updateDonation(donation: DonationEntity)

  @Query("SELECT * FROM donations ORDER BY createdAt DESC")
  fun getAllDonations(): Flow<List<DonationEntity>>

  @Query("SELECT * FROM donations WHERE status = 'AVAILABLE' ORDER BY createdAt DESC")
  fun getAvailableDonations(): Flow<List<DonationEntity>>

  @Query("SELECT * FROM donations WHERE donorId = :donorId ORDER BY createdAt DESC")
  fun getDonationsByDonor(donorId: String): Flow<List<DonationEntity>>

  @Query("SELECT * FROM donations WHERE id = :id LIMIT 1")
  suspend fun getDonationById(id: String): DonationEntity?

  @Query("UPDATE donations SET status = :status WHERE id = :id")
  suspend fun updateDonationStatus(id: String, status: String)

  @Query("DELETE FROM donations WHERE id = :id")
  suspend fun deleteDonationById(id: String)
}
