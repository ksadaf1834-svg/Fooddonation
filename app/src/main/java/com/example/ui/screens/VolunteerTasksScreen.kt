package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.FoodShareViewModel
import com.example.ui.components.VolunteerTaskCard
import com.example.ui.theme.FoodShareAccentOrange
import com.example.ui.theme.FoodShareGreenContainer
import com.example.ui.theme.FoodShareGreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VolunteerTasksScreen(
  viewModel: FoodShareViewModel,
  onNavigateToAuth: () -> Unit,
  modifier: Modifier = Modifier
) {
  val currentUser by viewModel.currentUser.collectAsState()
  val availableTasks by viewModel.availableTasks.collectAsState()
  val myAcceptedTasks by viewModel.myAcceptedTasks.collectAsState()
  val myProfile by viewModel.myVolunteerProfile.collectAsState()

  var selectedTab by remember { mutableIntStateOf(0) } // 0: Available Tasks, 1: My Accepted Tasks, 2: My Profile
  var showEditProfileDialog by remember { mutableStateOf(false) }

  // Profile Edit fields
  var editPhone by remember { mutableStateOf(myProfile?.phone ?: currentUser?.phone ?: "") }
  var editLocation by remember { mutableStateOf(myProfile?.location ?: "Mumbai, Maharashtra") }
  var editAvailability by remember { mutableStateOf(myProfile?.availabilityStatus ?: "Available") }
  var editAvailExpanded by remember { mutableStateOf(false) }
  var editPreferredTask by remember { mutableStateOf(myProfile?.preferredTaskType ?: "Food Delivery") }
  var editTaskExpanded by remember { mutableStateOf(false) }
  var editVehicle by remember { mutableStateOf(myProfile?.vehicleType ?: "Car") }
  var editVehicleExpanded by remember { mutableStateOf(false) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFFF7FBF7))
  ) {
    // Volunteer Profile Header Banner
    if (currentUser?.role == "VOLUNTEER" || currentUser?.role == "ADMIN") {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Row(
          modifier = Modifier.padding(16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(48.dp)
              .background(FoodShareGreenContainer, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Favorite, contentDescription = null, tint = FoodShareGreenPrimary)
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = currentUser?.fullName ?: "Volunteer Hero",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = FoodShareGreenPrimary
            )
            Text(
              text = "Status: ${myProfile?.availabilityStatus ?: "Active"} • Tasks Done: ${myProfile?.completedTaskCount ?: 0}",
              fontSize = 12.sp,
              color = Color(0xFF666666)
            )
            Text(
              text = "Prefers: ${myProfile?.preferredTaskType ?: "Food Delivery"} (${myProfile?.vehicleType ?: "Car"})",
              fontSize = 11.sp,
              color = FoodShareAccentOrange,
              fontWeight = FontWeight.Medium
            )
          }

          IconButton(
            onClick = {
              editPhone = myProfile?.phone ?: currentUser?.phone ?: ""
              editLocation = myProfile?.location ?: "Mumbai, Maharashtra"
              editAvailability = myProfile?.availabilityStatus ?: "Available"
              editPreferredTask = myProfile?.preferredTaskType ?: "Food Delivery"
              editVehicle = myProfile?.vehicleType ?: "Car"
              showEditProfileDialog = true
            },
            modifier = Modifier.testTag("volunteer_edit_profile_button")
          ) {
            Icon(Icons.Default.Edit, contentDescription = "Edit Profile", tint = FoodShareGreenPrimary)
          }
        }
      }
    }

    TabRow(
      selectedTabIndex = selectedTab,
      containerColor = Color.White,
      contentColor = FoodShareGreenPrimary
    ) {
      Tab(
        selected = selectedTab == 0,
        onClick = { selectedTab = 0 },
        text = { Text("Available Tasks (${availableTasks.size})", fontWeight = FontWeight.Bold) },
        modifier = Modifier.testTag("volunteer_tab_available")
      )
      Tab(
        selected = selectedTab == 1,
        onClick = { selectedTab = 1 },
        text = { Text("My Tasks (${myAcceptedTasks.size})", fontWeight = FontWeight.Bold) },
        modifier = Modifier.testTag("volunteer_tab_my_tasks")
      )
    }

    when (selectedTab) {
      0 -> {
        // Available Tasks List
        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          if (currentUser == null || currentUser?.role != "VOLUNTEER") {
            item {
              Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = FoodShareGreenContainer),
                shape = RoundedCornerShape(12.dp)
              ) {
                Row(
                  modifier = Modifier.padding(14.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(Icons.Default.Favorite, contentDescription = null, tint = FoodShareGreenPrimary)
                  Spacer(modifier = Modifier.width(10.dp))
                  Column {
                    Text(
                      text = "Join as a Volunteer",
                      fontWeight = FontWeight.Bold,
                      color = FoodShareGreenPrimary
                    )
                    Text(
                      text = "Log in or register with the Volunteer role to accept delivery & pickup tasks.",
                      fontSize = 12.sp,
                      color = Color(0xFF1B5E20)
                    )
                  }
                }
              }
            }
          }

          if (availableTasks.isEmpty()) {
            item {
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
              ) {
                Column(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  Text(
                    text = "No open volunteer tasks right now.",
                    fontWeight = FontWeight.Bold,
                    color = FoodShareGreenPrimary
                  )
                  Spacer(modifier = Modifier.height(6.dp))
                  Text(
                    text = "When donors accept food requests, automated pickup & delivery tasks appear here.",
                    fontSize = 12.sp,
                    color = Color(0xFF757575)
                  )
                }
              }
            }
          } else {
            items(availableTasks) { task ->
              VolunteerTaskCard(
                task = task,
                currentVolunteerId = currentUser?.id,
                onAcceptTask = {
                  if (currentUser == null) {
                    onNavigateToAuth()
                  } else {
                    viewModel.acceptVolunteerTask(task.id)
                  }
                }
              )
            }
          }
        }
      }

      1 -> {
        // My Accepted Tasks
        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          if (currentUser == null) {
            item {
              Text(
                text = "Please log in to view your assigned volunteer tasks.",
                color = Color(0xFF666666)
              )
            }
          } else if (myAcceptedTasks.isEmpty()) {
            item {
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
              ) {
                Column(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  Text(
                    text = "You have no active tasks.",
                    fontWeight = FontWeight.Bold,
                    color = FoodShareGreenPrimary
                  )
                  Spacer(modifier = Modifier.height(6.dp))
                  Text(
                    text = "Accept a task from the Available Tasks tab to start helping your community.",
                    fontSize = 12.sp,
                    color = Color(0xFF757575)
                  )
                }
              }
            }
          } else {
            items(myAcceptedTasks) { task ->
              VolunteerTaskCard(
                task = task,
                currentVolunteerId = currentUser?.id,
                onStartDelivery = {
                  viewModel.updateTaskStatus(task.id, "In Progress")
                },
                onCompleteTask = {
                  viewModel.updateTaskStatus(task.id, "Completed")
                }
              )
            }
          }
        }
      }
    }
  }

  // Edit Profile Dialog
  if (showEditProfileDialog) {
    AlertDialog(
      onDismissRequest = { showEditProfileDialog = false },
      title = { Text("Update Volunteer Profile", fontWeight = FontWeight.Bold, color = FoodShareGreenPrimary) },
      text = {
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedTextField(
            value = editPhone,
            onValueChange = { editPhone = it },
            label = { Text("Contact Phone") },
            modifier = Modifier.fillMaxWidth().testTag("volunteer_profile_phone_input"),
            singleLine = true
          )

          OutlinedTextField(
            value = editLocation,
            onValueChange = { editLocation = it },
            label = { Text("Operating Area / Neighborhood") },
            modifier = Modifier.fillMaxWidth().testTag("volunteer_profile_location_input"),
            singleLine = true
          )

          // Availability Dropdown
          ExposedDropdownMenuBox(
            expanded = editAvailExpanded,
            onExpandedChange = { editAvailExpanded = !editAvailExpanded }
          ) {
            OutlinedTextField(
              value = editAvailability,
              onValueChange = {},
              readOnly = true,
              label = { Text("Availability Status") },
              trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = editAvailExpanded) },
              modifier = Modifier.fillMaxWidth().menuAnchor().testTag("volunteer_profile_avail_select")
            )
            ExposedDropdownMenu(
              expanded = editAvailExpanded,
              onDismissRequest = { editAvailExpanded = false }
            ) {
              listOf("Available", "Busy", "Weekends Only", "On Call").forEach { status ->
                DropdownMenuItem(
                  text = { Text(status) },
                  onClick = { editAvailability = status; editAvailExpanded = false }
                )
              }
            }
          }

          // Preferred Task Dropdown
          ExposedDropdownMenuBox(
            expanded = editTaskExpanded,
            onExpandedChange = { editTaskExpanded = !editTaskExpanded }
          ) {
            OutlinedTextField(
              value = editPreferredTask,
              onValueChange = {},
              readOnly = true,
              label = { Text("Preferred Task Type") },
              trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = editTaskExpanded) },
              modifier = Modifier.fillMaxWidth().menuAnchor().testTag("volunteer_profile_task_select")
            )
            ExposedDropdownMenu(
              expanded = editTaskExpanded,
              onDismissRequest = { editTaskExpanded = false }
            ) {
              listOf("Food Pickup", "Food Delivery", "Food Distribution", "Any").forEach { t ->
                DropdownMenuItem(
                  text = { Text(t) },
                  onClick = { editPreferredTask = t; editTaskExpanded = false }
                )
              }
            }
          }

          // Vehicle Type Dropdown
          ExposedDropdownMenuBox(
            expanded = editVehicleExpanded,
            onExpandedChange = { editVehicleExpanded = !editVehicleExpanded }
          ) {
            OutlinedTextField(
              value = editVehicle,
              onValueChange = {},
              readOnly = true,
              label = { Text("Vehicle Type") },
              trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = editVehicleExpanded) },
              modifier = Modifier.fillMaxWidth().menuAnchor().testTag("volunteer_profile_vehicle_select")
            )
            ExposedDropdownMenu(
              expanded = editVehicleExpanded,
              onDismissRequest = { editVehicleExpanded = false }
            ) {
              listOf("Bicycle", "Motorcycle", "Car", "Van / Cargo", "On Foot").forEach { v ->
                DropdownMenuItem(
                  text = { Text(v) },
                  onClick = { editVehicle = v; editVehicleExpanded = false }
                )
              }
            }
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.updateVolunteerProfile(
              phone = editPhone,
              location = editLocation,
              availability = editAvailability,
              preferredTask = editPreferredTask,
              vehicleType = editVehicle,
              onSuccess = { showEditProfileDialog = false }
            )
          },
          colors = ButtonDefaults.buttonColors(containerColor = FoodShareGreenPrimary),
          modifier = Modifier.testTag("volunteer_save_profile_button")
        ) {
          Text("Save Profile")
        }
      },
      dismissButton = {
        OutlinedButton(onClick = { showEditProfileDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }
}
