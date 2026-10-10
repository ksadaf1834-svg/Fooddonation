package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.DonationDao
import com.example.data.local.dao.FoodPartnerDao
import com.example.data.local.dao.FoodRequestDao
import com.example.data.local.dao.UserDao
import com.example.data.local.dao.VolunteerDao
import com.example.data.local.dao.VolunteerTaskDao
import com.example.data.local.entity.DonationEntity
import com.example.data.local.entity.FoodPartnerEntity
import com.example.data.local.entity.FoodRequestEntity
import com.example.data.local.entity.UserEntity
import com.example.data.local.entity.VolunteerEntity
import com.example.data.local.entity.VolunteerTaskEntity
import com.example.data.security.PasswordHasher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

@Database(
  entities = [
    UserEntity::class,
    DonationEntity::class,
    FoodRequestEntity::class,
    VolunteerEntity::class,
    VolunteerTaskEntity::class,
    FoodPartnerEntity::class
  ],
  version = 1,
  exportSchema = false
)
abstract class FoodShareDatabase : RoomDatabase() {
  abstract fun userDao(): UserDao
  abstract fun donationDao(): DonationDao
  abstract fun foodRequestDao(): FoodRequestDao
  abstract fun volunteerDao(): VolunteerDao
  abstract fun volunteerTaskDao(): VolunteerTaskDao
  abstract fun foodPartnerDao(): FoodPartnerDao

  companion object {
    @Volatile
    private var INSTANCE: FoodShareDatabase? = null

    fun getDatabase(context: Context): FoodShareDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          FoodShareDatabase::class.java,
          "foodshare_database"
        )
        .addCallback(object : Callback() {
          override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            seedAdminAccounts()
          }

          override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            seedAdminAccounts()
          }

          private fun seedAdminAccounts() {
            CoroutineScope(Dispatchers.IO).launch {
              // Primary Admin
              val userAdmin = UserEntity(
                id = "admin_ksadaf_primary",
                email = "ksadaf1834@gmail.com",
                passwordHash = PasswordHasher.hashPassword("ksadaf123"),
                fullName = "Administrator",
                phone = "+1-555-0199",
                role = "ADMIN",
                isApproved = true,
                isSuspended = false,
                createdAt = System.currentTimeMillis()
              )
              INSTANCE?.userDao()?.insertUser(userAdmin)
            }
          }
        })
        .fallbackToDestructiveMigration()
        .build()
        INSTANCE = instance
        instance
      }
    }
  }
}
