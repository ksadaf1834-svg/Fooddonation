package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertUser(user: UserEntity)

  @Update
  suspend fun updateUser(user: UserEntity)

  @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
  suspend fun getUserByEmail(email: String): UserEntity?

  @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
  suspend fun getUserById(id: String): UserEntity?

  @Query("SELECT * FROM users ORDER BY createdAt DESC")
  fun getAllUsers(): Flow<List<UserEntity>>

  @Query("UPDATE users SET isSuspended = :isSuspended WHERE id = :userId")
  suspend fun setSuspension(userId: String, isSuspended: Boolean)

  @Query("UPDATE users SET isApproved = :isApproved WHERE id = :userId")
  suspend fun setApproval(userId: String, isApproved: Boolean)

  @Query("UPDATE users SET role = :role WHERE id = :userId")
  suspend fun updateRole(userId: String, role: String)
}
