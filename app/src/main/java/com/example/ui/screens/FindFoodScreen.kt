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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.data.local.entity.DonationEntity
import com.example.ui.FoodShareViewModel
import com.example.ui.components.DonationCard
import com.example.ui.components.FoodRequestCard
import com.example.ui.theme.FoodShareAccentOrange
import com.example.ui.theme.FoodShareGreenContainer
import com.example.ui.theme.FoodShareGreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FindFoodScreen(
  viewModel: FoodShareViewModel,
  onNavigateToAuth: () -> Unit,
  modifier: Modifier = Modifier
) {
  val currentUser by viewModel.currentUser.collectAsState()
  val availableDonations by viewModel.availableDonations.collectAsState()
  val myRequests by viewModel.myRequests.collectAsState()

  var selectedTab by remember { mutableIntStateOf(0) } // 0: Available Food, 1: My Requests
  var searchQuery by remember { mutableStateOf("") }
  var selectedCategory by remember { mutableStateOf("All") }

  // Request Dialog State
  var selectedDonationForRequest by remember { mutableStateOf<DonationEntity?>(null) }
  var reqOrgName by remember { mutableStateOf("") }
  var reqQuantity by remember { mutableStateOf("") }
  var reqDeliveryAddress by remember { mutableStateOf("") }
  var reqUrgency by remember { mutableStateOf("MEDIUM") }
  var reqUrgencyExpanded by remember { mutableStateOf(false) }
  var reqNotes by remember { mutableStateOf("") }
  var dialogError by remember { mutableStateOf<String?>(null) }

  val categories = listOf("All", "Cooked Meals", "Baked Goods", "Produce & Fresh Fruits", "Packaged & Canned Goods", "Dairy & Beverages")

  val filteredDonations = remember(availableDonations, searchQuery, selectedCategory) {
    availableDonations.filter { d ->
      val matchesCategory = selectedCategory == "All" || d.foodType.equals(selectedCategory, ignoreCase = true)
      val matchesQuery = searchQuery.isBlank() ||
          d.foodTitle.contains(searchQuery, ignoreCase = true) ||
          d.description.contains(searchQuery, ignoreCase = true) ||
          d.pickupAddress.contains(searchQuery, ignoreCase = true) ||
          d.donorName.contains(searchQuery, ignoreCase = true)
      matchesCategory && matchesQuery
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFFF7FBF7))
  ) {
    TabRow(
      selectedTabIndex = selectedTab,
      containerColor = Color.White,
      contentColor = FoodShareGreenPrimary
    ) {
      Tab(
        selected = selectedTab == 0,
        onClick = { selectedTab = 0 },
        text = { Text("Available Food (${filteredDonations.size})", fontWeight = FontWeight.Bold) },
        modifier = Modifier.testTag("find_tab_available")
      )
      if (currentUser != null) {
        Tab(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          text = { Text("My Requests (${myRequests.size})", fontWeight = FontWeight.Bold) },
          modifier = Modifier.testTag("find_tab_my_requests")
        )
      }
    }

    if (selectedTab == 0) {
      // Search bar & filters
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color.White)
          .padding(horizontal = 16.dp, vertical = 10.dp)
      ) {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("Search food, meals, bakery...") },
          leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = FoodShareGreenPrimary) },
          trailingIcon = {
            if (searchQuery.isNotEmpty()) {
              IconButton(onClick = { searchQuery = "" }) {
                Icon(Icons.Default.Close, contentDescription = "Clear")
              }
            }
          },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("find_food_search_input"),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          items(categories) { cat ->
            FilterChip(
              selected = selectedCategory == cat,
              onClick = { selectedCategory = cat },
              label = { Text(cat, fontSize = 12.sp) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = FoodShareGreenContainer,
                selectedLabelColor = FoodShareGreenPrimary
              ),
              modifier = Modifier.testTag("filter_chip_$cat")
            )
          }
        }
      }

      // Available food cards
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        if (filteredDonations.isEmpty()) {
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
                  text = "No surplus food listings match your search.",
                  fontWeight = FontWeight.Bold,
                  color = FoodShareGreenPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = "Try clearing your filters or search keywords.",
                  fontSize = 12.sp,
                  color = Color(0xFF757575)
                )
              }
            }
          }
        } else {
          items(filteredDonations) { donation ->
            DonationCard(
              donation = donation,
              onRequestClick = {
                if (currentUser == null) {
                  onNavigateToAuth()
                } else {
                  selectedDonationForRequest = donation
                  reqQuantity = donation.quantity
                }
              }
            )
          }
        }
      }
    } else {
      // My Requests List
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        if (myRequests.isEmpty()) {
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
                  text = "No food requests submitted yet.",
                  fontWeight = FontWeight.Bold,
                  color = FoodShareGreenPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = "Switch to 'Available Food' and request any surplus items for your NGO or shelter.",
                  fontSize = 12.sp,
                  color = Color(0xFF757575)
                )
              }
            }
          }
        } else {
          items(myRequests) { req ->
            FoodRequestCard(request = req)
          }
        }
      }
    }
  }

  // Request Submission Dialog
  selectedDonationForRequest?.let { donation ->
    AlertDialog(
      onDismissRequest = { selectedDonationForRequest = null; dialogError = null },
      title = {
        Text("Request Food: ${donation.foodTitle}", fontWeight = FontWeight.Bold, color = FoodShareGreenPrimary)
      },
      text = {
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Text(
            text = "Total Available: ${donation.quantity} • Deadline: ${donation.deadlineTime}",
            fontSize = 12.sp,
            color = Color(0xFF555555)
          )

          OutlinedTextField(
            value = reqOrgName,
            onValueChange = { reqOrgName = it },
            label = { Text("Organization / Shelter Name") },
            placeholder = { Text("e.g. Hope Community Shelter") },
            modifier = Modifier.fillMaxWidth().testTag("request_org_input"),
            singleLine = true
          )

          OutlinedTextField(
            value = reqQuantity,
            onValueChange = { reqQuantity = it; dialogError = null },
            label = { Text("Quantity Needed *") },
            placeholder = { Text("e.g. 15 meals or All") },
            modifier = Modifier.fillMaxWidth().testTag("request_qty_input"),
            singleLine = true
          )

          OutlinedTextField(
            value = reqDeliveryAddress,
            onValueChange = { reqDeliveryAddress = it; dialogError = null },
            label = { Text("Delivery Address *") },
            placeholder = { Text("e.g. 405 Hope Ave, Shelter Kitchen") },
            modifier = Modifier.fillMaxWidth().testTag("request_delivery_address_input"),
            singleLine = true
          )

          // Urgency Dropdown
          ExposedDropdownMenuBox(
            expanded = reqUrgencyExpanded,
            onExpandedChange = { reqUrgencyExpanded = !reqUrgencyExpanded }
          ) {
            OutlinedTextField(
              value = "$reqUrgency Priority",
              onValueChange = {},
              readOnly = true,
              label = { Text("Urgency Level") },
              trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = reqUrgencyExpanded) },
              modifier = Modifier.fillMaxWidth().menuAnchor().testTag("request_urgency_select")
            )
            ExposedDropdownMenu(
              expanded = reqUrgencyExpanded,
              onDismissRequest = { reqUrgencyExpanded = false }
            ) {
              listOf("HIGH", "MEDIUM", "LOW").forEach { u ->
                DropdownMenuItem(
                  text = { Text("$u Priority") },
                  onClick = { reqUrgency = u; reqUrgencyExpanded = false }
                )
              }
            }
          }

          OutlinedTextField(
            value = reqNotes,
            onValueChange = { reqNotes = it },
            label = { Text("Special Dietary or Delivery Notes") },
            placeholder = { Text("e.g. vegetarian, dock access in rear") },
            modifier = Modifier.fillMaxWidth().testTag("request_notes_input"),
            minLines = 2
          )

          if (dialogError != null) {
            Text(
              text = dialogError ?: "",
              color = MaterialTheme.colorScheme.error,
              fontSize = 12.sp
            )
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.requestFood(
              donation = donation,
              quantityRequested = reqQuantity,
              organizationName = reqOrgName,
              deliveryAddress = reqDeliveryAddress,
              urgency = reqUrgency,
              notes = reqNotes,
              onSuccess = {
                selectedDonationForRequest = null
                selectedTab = 1 // Switch to My Requests
              },
              onError = { dialogError = it }
            )
          },
          colors = ButtonDefaults.buttonColors(containerColor = FoodShareGreenPrimary),
          modifier = Modifier.testTag("submit_food_request_button")
        ) {
          Text("Submit Request")
        }
      },
      dismissButton = {
        Button(
          onClick = { selectedDonationForRequest = null; dialogError = null },
          colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color(0xFF555555))
        ) {
          Text("Cancel")
        }
      }
    )
  }
}
