package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.FoodShareDatabase
import com.example.data.local.entity.DonationEntity
import com.example.data.local.entity.FoodPartnerEntity
import com.example.data.local.entity.FoodRequestEntity
import com.example.data.local.entity.UserEntity
import com.example.data.local.entity.VolunteerEntity
import com.example.data.local.entity.VolunteerTaskEntity
import com.example.data.model.NearbyPartner
import com.example.data.repository.FoodShareRepository
import com.example.data.service.LocationAndPlacesService
import com.example.ui.navigation.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class FoodShareViewModel(application: Application) : AndroidViewModel(application) {

  private val database = FoodShareDatabase.getDatabase(application)
  private val repository = FoodShareRepository(database)
  private val locationService = LocationAndPlacesService(application)

  private val _currentUser = MutableStateFlow<UserEntity?>(null)
  val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

  private val _currentScreen = MutableStateFlow<Screen>(Screen.Home)
  val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

  private val _snackbarMessage = MutableSharedFlow<String>()
  val snackbarMessage: SharedFlow<String> = _snackbarMessage.asSharedFlow()

  // Location & Nearby Places (Mumbai, Maharashtra)
  private val _userCoordinates = MutableStateFlow(Pair(19.0760, 72.8777)) // Mumbai, Maharashtra
  val userCoordinates: StateFlow<Pair<Double, Double>> = _userCoordinates.asStateFlow()

  private val _nearbyPartners = MutableStateFlow<List<NearbyPartner>>(emptyList())
  val nearbyPartners: StateFlow<List<NearbyPartner>> = _nearbyPartners.asStateFlow()

  private val _isSearchingNearby = MutableStateFlow(false)
  val isSearchingNearby: StateFlow<Boolean> = _isSearchingNearby.asStateFlow()

  private val _selectedPartnerForMap = MutableStateFlow<NearbyPartner?>(null)
  val selectedPartnerForMap: StateFlow<NearbyPartner?> = _selectedPartnerForMap.asStateFlow()

  // Reactive Flows
  val availableDonations: StateFlow<List<DonationEntity>> = repository.getAvailableDonations()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allDonations: StateFlow<List<DonationEntity>> = repository.getAllDonations()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val availableTasks: StateFlow<List<VolunteerTaskEntity>> = repository.getAvailableTasks()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allTasks: StateFlow<List<VolunteerTaskEntity>> = repository.getAllTasks()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allVolunteers: StateFlow<List<VolunteerEntity>> = repository.getAllVolunteers()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allUsers: StateFlow<List<UserEntity>> = repository.getAllUsers()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allPartners: StateFlow<List<FoodPartnerEntity>> = repository.getAllPartners()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val pendingPartners: StateFlow<List<FoodPartnerEntity>> = repository.getPendingPartners()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val approvedPartners: StateFlow<List<FoodPartnerEntity>> = repository.getApprovedPartners()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allRequests: StateFlow<List<FoodRequestEntity>> = repository.getAllRequests()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Dynamic user-scoped flows
  val myDonations: StateFlow<List<DonationEntity>> = _currentUser.flatMapLatest { user ->
    if (user != null) repository.getDonationsByDonor(user.id) else flowOf(emptyList())
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val myRequests: StateFlow<List<FoodRequestEntity>> = _currentUser.flatMapLatest { user ->
    if (user != null) repository.getRequestsByReceiver(user.id) else flowOf(emptyList())
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val myVolunteerProfile: StateFlow<VolunteerEntity?> = _currentUser.flatMapLatest { user ->
    if (user != null) repository.getVolunteerByUserId(user.id) else flowOf(null)
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  val myAcceptedTasks: StateFlow<List<VolunteerTaskEntity>> = _currentUser.flatMapLatest { user ->
    if (user != null) repository.getTasksForVolunteer(user.id) else flowOf(emptyList())
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val myFoodPartner: StateFlow<FoodPartnerEntity?> = _currentUser.flatMapLatest { user ->
    if (user != null) repository.getPartnerByUserId(user.id) else flowOf(null)
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  private var hasExplicitLocation = true

  init {
    viewModelScope.launch {
      repository.ensureAdminProvisioned()
    }
    loadNearbyPartners("Mumbai, Maharashtra", "All")
  }

  fun navigateTo(screen: Screen) {
    _currentScreen.value = screen
  }

  fun showMessage(msg: String) {
    viewModelScope.launch {
      _snackbarMessage.emit(msg)
    }
  }

  // --- Auth Actions ---

  fun login(
    email: String,
    pass: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
  ) {
    if (email.isBlank() || pass.isBlank()) {
      onError("Please provide both email and password.")
      return
    }
    viewModelScope.launch {
      val res = repository.loginUser(email, pass)
      if (res.isSuccess) {
        val user = res.getOrThrow()
        _currentUser.value = user
        if (user.role == "ADMIN") {
          _currentScreen.value = Screen.AdminDashboard
        } else {
          _currentScreen.value = Screen.Dashboard
        }
        showMessage("Welcome back, ${user.fullName}!")
        onSuccess()
      } else {
        onError(res.exceptionOrNull()?.message ?: "Login failed.")
      }
    }
  }

  fun register(
    email: String,
    pass: String,
    fullName: String,
    phone: String,
    role: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
  ) {
    if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
      onError("Please enter a valid email address.")
      return
    }
    if (pass.length < 6) {
      onError("Password must be at least 6 characters long.")
      return
    }
    if (fullName.isBlank()) {
      onError("Please enter your full name.")
      return
    }
    if (phone.isBlank() || phone.length < 7) {
      onError("Please enter a valid phone number.")
      return
    }

    viewModelScope.launch {
      val res = repository.registerUser(email, pass, fullName, phone, role)
      if (res.isSuccess) {
        val user = res.getOrThrow()
        _currentUser.value = user
        _currentScreen.value = Screen.Dashboard
        showMessage("Account created successfully as ${user.role}!")
        onSuccess()
      } else {
        onError(res.exceptionOrNull()?.message ?: "Registration failed.")
      }
    }
  }

  fun registerFoodPartner(
    businessName: String,
    contactName: String,
    email: String,
    pass: String,
    phone: String,
    businessType: String,
    address: String,
    donationDetails: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
  ) {
    if (businessName.isBlank() || contactName.isBlank() || address.isBlank()) {
      onError("Please fill in all business details.")
      return
    }
    if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
      onError("Please enter a valid email address.")
      return
    }
    if (pass.length < 6) {
      onError("Password must be at least 6 characters.")
      return
    }

    viewModelScope.launch {
      val userRes = repository.registerUser(email, pass, contactName, phone, "FOOD_PARTNER")
      if (userRes.isSuccess) {
        val user = userRes.getOrThrow()
        val partner = FoodPartnerEntity(
          id = UUID.randomUUID().toString(),
          userId = user.id,
          businessName = businessName,
          contactPerson = contactName,
          email = email,
          phone = phone,
          businessType = businessType,
          address = address,
          latitude = _userCoordinates.value.first + (Math.random() - 0.5) * 0.02,
          longitude = _userCoordinates.value.second + (Math.random() - 0.5) * 0.02,
          donationDetails = donationDetails,
          approvalStatus = "PENDING",
          isAvailableForDonations = true
        )
        repository.registerFoodPartner(partner)
        _currentUser.value = user
        _currentScreen.value = Screen.Dashboard
        showMessage("Food Partner registered! Account is pending Admin approval.")
        onSuccess()
      } else {
        onError(userRes.exceptionOrNull()?.message ?: "Registration failed.")
      }
    }
  }

  fun handleFirebaseGoogleSignIn(
    email: String,
    displayName: String,
    selectedRole: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
  ) {
    viewModelScope.launch {
      try {
        val cleanEmail = email.trim().lowercase()
        val existingUser = database.userDao().getUserByEmail(cleanEmail)
        val user = if (existingUser != null) {
          existingUser
        } else {
          val newUser = UserEntity(
            id = UUID.randomUUID().toString(),
            email = cleanEmail,
            passwordHash = "FIREBASE_AUTH_PROVIDER",
            fullName = displayName.ifBlank { "Community Member" },
            phone = "+1-555-0100",
            role = selectedRole,
            isApproved = true,
            isSuspended = false,
            createdAt = System.currentTimeMillis()
          )
          database.userDao().insertUser(newUser)

          if (selectedRole == "VOLUNTEER") {
            val vol = VolunteerEntity(
              id = UUID.randomUUID().toString(),
              userId = newUser.id,
              fullName = newUser.fullName,
              phone = newUser.phone,
              location = "Local Community",
              availabilityStatus = "Available",
              preferredTaskType = "Food Delivery",
              vehicleType = "Car",
              completedTaskCount = 0
            )
            database.volunteerDao().insertVolunteer(vol)
          }
          newUser
        }

        if (user.isSuspended) {
          onError("Your account is currently suspended. Please contact Admin.")
          return@launch
        }

        _currentUser.value = user
        if (user.role == "ADMIN") {
          _currentScreen.value = Screen.AdminDashboard
        } else {
          _currentScreen.value = Screen.Dashboard
        }
        showMessage("Signed in successfully as ${user.fullName} (${user.role})")
        onSuccess()
      } catch (e: Exception) {
        onError(e.localizedMessage ?: "Failed to complete sign-in")
      }
    }
  }

  fun logout() {
    try {
      com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
    } catch (_: Exception) {}
    _currentUser.value = null
    _currentScreen.value = Screen.Home
    showMessage("Logged out successfully.")
  }

  // --- Volunteer Operations ---

  fun updateVolunteerProfile(
    phone: String,
    location: String,
    availability: String,
    preferredTask: String,
    vehicleType: String,
    onSuccess: () -> Unit
  ) {
    val user = _currentUser.value ?: return
    viewModelScope.launch {
      val existing = myVolunteerProfile.value
      val vol = VolunteerEntity(
        id = existing?.id ?: UUID.randomUUID().toString(),
        userId = user.id,
        fullName = user.fullName,
        phone = phone.ifBlank { user.phone },
        location = location,
        availabilityStatus = availability,
        preferredTaskType = preferredTask,
        vehicleType = vehicleType,
        completedTaskCount = existing?.completedTaskCount ?: 0,
        updatedAt = System.currentTimeMillis()
      )
      repository.saveVolunteerProfile(vol)
      showMessage("Volunteer profile updated!")
      onSuccess()
    }
  }

  fun acceptVolunteerTask(taskId: String) {
    val user = _currentUser.value
    if (user == null || user.role != "VOLUNTEER") {
      showMessage("Please log in as a volunteer to accept tasks.")
      return
    }

    viewModelScope.launch {
      val success = repository.acceptTaskAtomically(
        taskId = taskId,
        volunteerId = user.id,
        volunteerName = user.fullName,
        volunteerPhone = user.phone
      )
      if (success) {
        showMessage("Task accepted successfully! View it under My Accepted Tasks.")
      } else {
        showMessage("Unable to accept: this task was already accepted by another volunteer.")
      }
    }
  }

  fun updateTaskStatus(taskId: String, status: String) {
    viewModelScope.launch {
      repository.updateTaskStatus(taskId, status)
      showMessage("Task status updated to $status.")
    }
  }

  // --- Donations Operations ---

  fun createDonation(
    title: String,
    description: String,
    quantity: String,
    foodType: String,
    perishable: Boolean,
    pickupAddress: String,
    deadline: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
  ) {
    val user = _currentUser.value
    if (user == null) {
      onError("Please log in to post a donation.")
      return
    }
    if (title.isBlank() || quantity.isBlank() || pickupAddress.isBlank()) {
      onError("Please complete all required food and pickup details.")
      return
    }

    viewModelScope.launch {
      val donation = DonationEntity(
        id = UUID.randomUUID().toString(),
        donorId = user.id,
        donorName = user.fullName,
        foodTitle = title.trim(),
        description = description.trim(),
        quantity = quantity.trim(),
        foodType = foodType,
        perishable = perishable,
        pickupAddress = pickupAddress.trim(),
        latitude = _userCoordinates.value.first,
        longitude = _userCoordinates.value.second,
        deadlineTime = deadline.trim().ifBlank { "Same day pickup" },
        status = "AVAILABLE"
      )
      repository.createDonation(donation)
      showMessage("Food donation listing created successfully!")
      onSuccess()
    }
  }

  fun deleteDonation(donationId: String) {
    viewModelScope.launch {
      repository.deleteDonation(donationId)
      showMessage("Donation listing removed.")
    }
  }

  // --- Food Request Operations ---

  fun requestFood(
    donation: DonationEntity,
    quantityRequested: String,
    organizationName: String,
    deliveryAddress: String,
    urgency: String,
    notes: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
  ) {
    val user = _currentUser.value
    if (user == null) {
      onError("Please log in to request food.")
      return
    }
    if (quantityRequested.isBlank() || deliveryAddress.isBlank()) {
      onError("Please specify quantity and delivery address.")
      return
    }

    viewModelScope.launch {
      val request = FoodRequestEntity(
        id = UUID.randomUUID().toString(),
        donationId = donation.id,
        foodTitle = donation.foodTitle,
        receiverId = user.id,
        receiverName = user.fullName,
        receiverPhone = user.phone,
        organizationName = organizationName.ifBlank { user.fullName },
        deliveryAddress = deliveryAddress,
        quantityRequested = quantityRequested,
        urgency = urgency,
        notes = notes,
        status = "PENDING"
      )
      repository.createFoodRequest(request)
      showMessage("Food request submitted to the donor!")
      onSuccess()
    }
  }

  fun acceptFoodRequest(request: FoodRequestEntity, donation: DonationEntity) {
    viewModelScope.launch {
      repository.acceptFoodRequest(request, donation)
      showMessage("Request accepted! A delivery task has been created for volunteers.")
    }
  }

  fun rejectFoodRequest(request: FoodRequestEntity) {
    viewModelScope.launch {
      repository.rejectFoodRequest(request.id, request.donationId)
      showMessage("Request rejected.")
    }
  }

  // --- Location & Nearby Search ---

  fun selectPartnerForMap(partner: NearbyPartner?) {
    _selectedPartnerForMap.value = partner
  }

  fun setUserLocation(lat: Double, lon: Double) {
    hasExplicitLocation = true
    _userCoordinates.value = Pair(lat, lon)
    loadNearbyPartners(null, "All")
  }

  fun fetchDeviceLocation() {
    val loc = locationService.getLastKnownLocation()
    if (loc != null) {
      hasExplicitLocation = true
      _userCoordinates.value = loc
      loadNearbyPartners(null, "All")
      showMessage("Updated location from GPS.")
    } else {
      showMessage("Location permission not granted or GPS unavailable. You can search by area or PIN.")
    }
  }

  fun loadNearbyPartners(searchQuery: String?, categoryFilter: String = "All") {
    viewModelScope.launch {
      _isSearchingNearby.value = true
      val isExplicitSearch = !searchQuery.isNullOrBlank()
      if (isExplicitSearch) {
        hasExplicitLocation = true
      }

      val currentCoords = if (isExplicitSearch) {
        val geocoded = locationService.geocodeQuery(searchQuery!!)
        if (geocoded != null) {
          _userCoordinates.value = geocoded
          geocoded
        } else {
          _userCoordinates.value
        }
      } else {
        _userCoordinates.value
      }

      val dbPartners = locationService.mapDatabasePartners(approvedPartners.value, currentCoords.first, currentCoords.second)
      
      // Only query external OpenStreetMap places when user explicitly searches or triggers GPS
      val osmPlaces = if (hasExplicitLocation) {
        withContext(Dispatchers.IO) {
          locationService.fetchOsmNearbyPlaces(currentCoords.first, currentCoords.second)
        }
      } else {
        emptyList()
      }

      val combined = (dbPartners + osmPlaces)
        .distinctBy { "${it.name}_${it.address}" }
        .sortedBy { it.distanceKm }

      val filtered = combined.filter { item ->
        val matchesCategory = when (categoryFilter) {
          "All" -> true
          "Restaurants" -> item.category.equals("Restaurant", ignoreCase = true)
          "Bakeries" -> item.category.equals("Bakery", ignoreCase = true)
          "Grocery Stores" -> item.category.equals("Grocery Store", ignoreCase = true)
          "NGOs" -> item.category.equals("NGO", ignoreCase = true)
          else -> true
        }
        val matchesQuery = if (!searchQuery.isNullOrBlank()) {
          item.name.contains(searchQuery, ignoreCase = true) ||
          item.address.contains(searchQuery, ignoreCase = true) ||
          item.category.contains(searchQuery, ignoreCase = true)
        } else true

        matchesCategory && matchesQuery
      }

      _nearbyPartners.value = filtered
      _isSearchingNearby.value = false
    }
  }

  // --- Admin Operations ---

  fun adminApprovePartner(partnerId: String) {
    viewModelScope.launch {
      repository.setPartnerApproval(partnerId, "APPROVED")
      showMessage("Food Partner approved successfully!")
    }
  }

  fun adminRejectPartner(partnerId: String) {
    viewModelScope.launch {
      repository.setPartnerApproval(partnerId, "REJECTED")
      showMessage("Food Partner registration rejected.")
    }
  }

  fun adminToggleUserSuspension(userId: String, isSuspended: Boolean) {
    viewModelScope.launch {
      repository.toggleUserSuspension(userId, isSuspended)
      showMessage(if (isSuspended) "User suspended." else "User reactivated.")
    }
  }

  fun adminUpdateUserRole(userId: String, role: String) {
    viewModelScope.launch {
      repository.updateUserRole(userId, role)
      showMessage("User role updated to $role.")
    }
  }

  fun updatePartnerAvailability(partnerId: String, isAvailable: Boolean) {
    viewModelScope.launch {
      repository.setPartnerAvailability(partnerId, isAvailable)
      showMessage("Partner donation availability updated.")
    }
  }
}
