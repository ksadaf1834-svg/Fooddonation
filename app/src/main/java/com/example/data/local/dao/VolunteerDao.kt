package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.VolunteerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VolunteerDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertVolunteer(volunteer: VolunteerEntity)

  @Update
  suspend fun updateVolunteer(volunteer: VolunteerEntity)

  @Query("SELECT * FROM volunteers WHERE userId = :userId LIMIT 1")
  fun getVolunteerByUserId(userId: String): Flow<VolunteerEntity?>

  @Query("SELECT * FROM volunteers WHERE userId = :userId LIMIT 1")
  suspend fun getVolunteerByUserIdSync(userId: String): VolunteerEntity?

  @Query("SELECT * FROM volunteers ORDER BY completedTaskCount DESC, updatedAt DESC")
  fun getAllVolunteers(): Flow<List<VolunteerEntity>>

  @Query("UPDATE volunteers SET completedTaskCount = completedTaskCount + 1, updatedAt = :timestamp WHERE id = :volunteerId")
  suspend fun incrementCompletedTasks(volunteerId: String, timestamp: Long = System.currentTimeMillis())
}
