package com.example.data.repository

import com.example.data.local.FoodShareDatabase
import com.example.data.local.entity.DonationEntity
import com.example.data.local.entity.FoodPartnerEntity
import com.example.data.local.entity.FoodRequestEntity
import com.example.data.local.entity.UserEntity
import com.example.data.local.entity.VolunteerEntity
import com.example.data.local.entity.VolunteerTaskEntity
import com.example.data.security.PasswordHasher
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class FoodShareRepository(private val db: FoodShareDatabase) {

  private val userDao = db.userDao()
  private val donationDao = db.donationDao()
  private val foodRequestDao = db.foodRequestDao()
  private val volunteerDao = db.volunteerDao()
  private val volunteerTaskDao = db.volunteerTaskDao()
  private val foodPartnerDao = db.foodPartnerDao()

  // --- User Authentication & Management ---

  suspend fun ensureAdminProvisioned() {
    val email = "ksadaf1834@gmail.com"
    val hash = PasswordHasher.hashPassword("ksadaf123")
    val existing = userDao.getUserByEmail(email)
    if (existing == null) {
      val admin = UserEntity(
        id = "admin_ksadaf_primary",
        email = email,
        passwordHash = hash,
        fullName = "Administrator",
        phone = "+1-555-0199",
        role = "ADMIN",
        isApproved = true,
        isSuspended = false,
        createdAt = System.currentTimeMillis()
      )
      userDao.insertUser(admin)
    } else {
      userDao.insertUser(
        existing.copy(
          passwordHash = hash,
          role = "ADMIN",
          isApproved = true,
          isSuspended = false
        )
      )
    }
  }

  suspend fun registerUser(
    email: String,
    password: String,
    fullName: String,
    phone: String,
    role: String
  ): Result<UserEntity> {
    val cleanEmail = email.trim().lowercase()
    if (userDao.getUserByEmail(cleanEmail) != null) {
      return Result.failure(Exception("An account with email '$cleanEmail' already exists."))
    }
    val user = UserEntity(
      id = UUID.randomUUID().toString(),
      email = cleanEmail,
      passwordHash = PasswordHasher.hashPassword(password),
      fullName = fullName.trim(),
      phone = phone.trim(),
      role = role,
      isApproved = if (role == "FOOD_PARTNER") false else true,
      isSuspended = false,
      createdAt = System.currentTimeMillis()
    )
    userDao.insertUser(user)

    // If registered as Volunteer, initialize volunteer profile entry
    if (role == "VOLUNTEER") {
      val vol = VolunteerEntity(
        id = UUID.randomUUID().toString(),
        userId = user.id,
        fullName = user.fullName,
        phone = user.phone,
        location = "Local Community",
        availabilityStatus = "Available",
        preferredTaskType = "Food Delivery",
        vehicleType = "Car",
        completedTaskCount = 0
      )
      volunteerDao.insertVolunteer(vol)
    }

    return Result.success(user)
  }

  suspend fun loginUser(email: String, password: String): Result<UserEntity> {
    val cleanEmail = email.trim().lowercase()
    val user = userDao.getUserByEmail(cleanEmail)
      ?: return Result.failure(Exception("No account found with this email."))

    if (!PasswordHasher.verifyPassword(password, user.passwordHash)) {
      return Result.failure(Exception("Invalid password. Please check your credentials."))
    }

    if (user.isSuspended) {
      return Result.failure(Exception("Your account has been suspended by an administrator. Please contact support."))
    }

    return Result.success(user)
  }

  fun getAllUsers(): Flow<List<UserEntity>> = userDao.getAllUsers()

  suspend fun toggleUserSuspension(userId: String, isSuspended: Boolean) {
    userDao.setSuspension(userId, isSuspended)
  }

  suspend fun updateUserRole(userId: String, role: String) {
    userDao.updateRole(userId, role)
  }

  // --- Volunteer Management ---

  suspend fun saveVolunteerProfile(volunteer: VolunteerEntity) {
    volunteerDao.insertVolunteer(volunteer)
  }

  fun getVolunteerByUserId(userId: String): Flow<VolunteerEntity?> =
    volunteerDao.getVolunteerByUserId(userId)

  fun getAllVolunteers(): Flow<List<VolunteerEntity>> =
    volunteerDao.getAllVolunteers()

  fun getAvailableTasks(): Flow<List<VolunteerTaskEntity>> =
    volunteerTaskDao.getAvailableTasks()

  fun getTasksForVolunteer(volunteerId: String): Flow<List<VolunteerTaskEntity>> =
    volunteerTaskDao.getTasksForVolunteer(volunteerId)

  fun getAllTasks(): Flow<List<VolunteerTaskEntity>> =
    volunteerTaskDao.getAllTasks()

  fun getTasksForDonation(donationId: String): Flow<List<VolunteerTaskEntity>> =
    volunteerTaskDao.getTasksForDonation(donationId)

  suspend fun getTaskById(taskId: String): VolunteerTaskEntity? =
    volunteerTaskDao.getTaskById(taskId)

  /**
   * Accepts a volunteer task atomically. Prevents two volunteers from taking the same task.
   */
  suspend fun acceptTaskAtomically(
    taskId: String,
    volunteerId: String,
    volunteerName: String,
    volunteerPhone: String
  ): Boolean {
    val affected = volunteerTaskDao.claimTaskAtomically(
      taskId = taskId,
      volunteerId = volunteerId,
      volunteerName = volunteerName,
      volunteerPhone = volunteerPhone
    )
    return affected > 0
  }

  suspend fun updateTaskStatus(taskId: String, status: String) {
    val task = volunteerTaskDao.getTaskById(taskId)
    volunteerTaskDao.updateTaskStatus(taskId, status)

    if (task != null) {
      if (status == "Completed") {
        // Mark associated donation completed
        donationDao.updateDonationStatus(task.donationId, "COMPLETED")
        // Increment volunteer completed counter
        task.assignedVolunteerId?.let { volId ->
          volunteerDao.incrementCompletedTasks(volId)
        }
      } else if (status == "In Progress") {
        donationDao.updateDonationStatus(task.donationId, "CLAIMED")
      }
    }
  }

  suspend fun createCustomTask(
    donationId: String,
    title: String,
    taskType: String,
    foodDetails: String,
    pickupLocation: String,
    deliveryLocation: String,
    requiredTime: String
  ) {
    val task = VolunteerTaskEntity(
      id = UUID.randomUUID().toString(),
      donationId = donationId,
      taskType = taskType,
      title = title,
      foodDetails = foodDetails,
      pickupLocation = pickupLocation,
      deliveryLocation = deliveryLocation,
      requiredTime = requiredTime,
      status = "Available",
      createdAt = System.currentTimeMillis()
    )
    volunteerTaskDao.insertTask(task)
  }

  // --- Food Partner Management ---

  suspend fun registerFoodPartner(partner: FoodPartnerEntity) {
    foodPartnerDao.insertPartner(partner)
  }

  fun getPartnerByUserId(userId: String): Flow<FoodPartnerEntity?> =
    foodPartnerDao.getPartnerByUserId(userId)

  fun getAllPartners(): Flow<List<FoodPartnerEntity>> =
    foodPartnerDao.getAllPartners()

  fun getApprovedPartners(): Flow<List<FoodPartnerEntity>> =
    foodPartnerDao.getApprovedPartners()

  fun getPendingPartners(): Flow<List<FoodPartnerEntity>> =
    foodPartnerDao.getPendingPartners()

  suspend fun setPartnerApproval(partnerId: String, status: String) {
    foodPartnerDao.setApprovalStatus(partnerId, status)
    val partner = foodPartnerDao.getPartnerById(partnerId)
    if (partner != null) {
      userDao.setApproval(partner.userId, status == "APPROVED")
    }
  }

  suspend fun setPartnerAvailability(partnerId: String, isAvailable: Boolean) {
    foodPartnerDao.setAvailability(partnerId, isAvailable)
  }

  // --- Donations Workflow ---

  suspend fun createDonation(donation: DonationEntity) {
    donationDao.insertDonation(donation)
  }

  fun getAllDonations(): Flow<List<DonationEntity>> =
    donationDao.getAllDonations()

  fun getAvailableDonations(): Flow<List<DonationEntity>> =
    donationDao.getAvailableDonations()

  fun getDonationsByDonor(donorId: String): Flow<List<DonationEntity>> =
    donationDao.getDonationsByDonor(donorId)

  suspend fun getDonationById(id: String): DonationEntity? =
    donationDao.getDonationById(id)

  suspend fun updateDonationStatus(id: String, status: String) {
    donationDao.updateDonationStatus(id, status)
  }

  suspend fun deleteDonation(id: String) {
    donationDao.deleteDonationById(id)
  }

  // --- Food Requests Workflow ---

  suspend fun createFoodRequest(request: FoodRequestEntity) {
    foodRequestDao.insertRequest(request)
  }

  fun getAllRequests(): Flow<List<FoodRequestEntity>> =
    foodRequestDao.getAllRequests()

  fun getRequestsForDonation(donationId: String): Flow<List<FoodRequestEntity>> =
    foodRequestDao.getRequestsForDonation(donationId)

  fun getRequestsByReceiver(receiverId: String): Flow<List<FoodRequestEntity>> =
    foodRequestDao.getRequestsByReceiver(receiverId)

  /**
   * Accepts a request, updates donation to CLAIMED, and automatically creates an associated
   * VolunteerTask for pickup and delivery.
   */
  suspend fun acceptFoodRequest(
    request: FoodRequestEntity,
    donation: DonationEntity
  ) {
    foodRequestDao.updateRequestStatus(request.id, "ACCEPTED")
    donationDao.updateDonationStatus(donation.id, "CLAIMED")

    // Automatically create VolunteerTask for pickup/delivery
    val task = VolunteerTaskEntity(
      id = UUID.randomUUID().toString(),
      donationId = donation.id,
      requestId = request.id,
      taskType = "Food Delivery",
      title = "Deliver ${donation.foodTitle} to ${request.organizationName.ifBlank { request.receiverName }}",
      foodDetails = "${donation.quantity} of ${donation.foodTitle} (${donation.foodType})",
      pickupLocation = donation.pickupAddress,
      deliveryLocation = request.deliveryAddress,
      requiredTime = donation.deadlineTime,
      status = "Available",
      createdAt = System.currentTimeMillis()
    )
    volunteerTaskDao.insertTask(task)
  }

  suspend fun rejectFoodRequest(requestId: String, donationId: String) {
    foodRequestDao.updateRequestStatus(requestId, "REJECTED")
  }
}
