package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.VolunteerTaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VolunteerTaskDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTask(task: VolunteerTaskEntity)

  @Update
  suspend fun updateTask(task: VolunteerTaskEntity)

  @Query("SELECT * FROM volunteer_tasks ORDER BY createdAt DESC")
  fun getAllTasks(): Flow<List<VolunteerTaskEntity>>

  @Query("SELECT * FROM volunteer_tasks WHERE status = 'Available' ORDER BY createdAt DESC")
  fun getAvailableTasks(): Flow<List<VolunteerTaskEntity>>

  @Query("SELECT * FROM volunteer_tasks WHERE assignedVolunteerId = :volunteerId ORDER BY updatedAt DESC")
  fun getTasksForVolunteer(volunteerId: String): Flow<List<VolunteerTaskEntity>>

  @Query("SELECT * FROM volunteer_tasks WHERE donationId = :donationId ORDER BY createdAt DESC")
  fun getTasksForDonation(donationId: String): Flow<List<VolunteerTaskEntity>>

  @Query("SELECT * FROM volunteer_tasks WHERE id = :id LIMIT 1")
  suspend fun getTaskById(id: String): VolunteerTaskEntity?

  /**
   * Atomic task acceptance to prevent two volunteers from claiming the same task simultaneously.
   * Returns rows updated (1 if successful, 0 if already taken).
   */
  @Query("""
    UPDATE volunteer_tasks 
    SET status = 'Accepted', 
        assignedVolunteerId = :volunteerId, 
        assignedVolunteerName = :volunteerName, 
        assignedVolunteerPhone = :volunteerPhone, 
        updatedAt = :timestamp 
    WHERE id = :taskId AND status = 'Available'
  """)
  suspend fun claimTaskAtomically(
    taskId: String,
    volunteerId: String,
    volunteerName: String,
    volunteerPhone: String,
    timestamp: Long = System.currentTimeMillis()
  ): Int

  @Query("UPDATE volunteer_tasks SET status = :status, updatedAt = :timestamp WHERE id = :taskId")
  suspend fun updateTaskStatus(taskId: String, status: String, timestamp: Long = System.currentTimeMillis())
}
