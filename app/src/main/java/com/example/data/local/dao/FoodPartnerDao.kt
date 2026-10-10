package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.FoodPartnerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodPartnerDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertPartner(partner: FoodPartnerEntity)

  @Update
  suspend fun updatePartner(partner: FoodPartnerEntity)

  @Query("SELECT * FROM food_partners WHERE userId = :userId LIMIT 1")
  fun getPartnerByUserId(userId: String): Flow<FoodPartnerEntity?>

  @Query("SELECT * FROM food_partners WHERE userId = :userId LIMIT 1")
  suspend fun getPartnerByUserIdSync(userId: String): FoodPartnerEntity?

  @Query("SELECT * FROM food_partners ORDER BY createdAt DESC")
  fun getAllPartners(): Flow<List<FoodPartnerEntity>>

  @Query("SELECT * FROM food_partners WHERE approvalStatus = 'APPROVED' ORDER BY businessName ASC")
  fun getApprovedPartners(): Flow<List<FoodPartnerEntity>>

  @Query("SELECT * FROM food_partners WHERE approvalStatus = 'PENDING' ORDER BY createdAt DESC")
  fun getPendingPartners(): Flow<List<FoodPartnerEntity>>

  @Query("UPDATE food_partners SET approvalStatus = :status WHERE id = :partnerId")
  suspend fun setApprovalStatus(partnerId: String, status: String)

  @Query("UPDATE food_partners SET isAvailableForDonations = :isAvailable WHERE id = :partnerId")
  suspend fun setAvailability(partnerId: String, isAvailable: Boolean)

  @Query("SELECT * FROM food_partners WHERE id = :id LIMIT 1")
  suspend fun getPartnerById(id: String): FoodPartnerEntity?
}
