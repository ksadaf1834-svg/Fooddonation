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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
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
import com.example.data.local.entity.FoodRequestEntity
import com.example.ui.FoodShareViewModel
import com.example.ui.components.DonationCard
import com.example.ui.components.FoodRequestCard
import com.example.ui.theme.FoodShareAccentOrange
import com.example.ui.theme.FoodShareGreenContainer
import com.example.ui.theme.FoodShareGreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonateFoodScreen(
  viewModel: FoodShareViewModel,
  modifier: Modifier = Modifier
) {
  val currentUser by viewModel.currentUser.collectAsState()
  val myDonations by viewModel.myDonations.collectAsState()
  val allRequests by viewModel.allRequests.collectAsState()
  val myPartner by viewModel.myFoodPartner.collectAsState()

  var selectedTab by remember { mutableIntStateOf(0) } // 0: My Donations, 1: Post New Donation, 2: Incoming Requests

  // Post donation form state
  var foodTitle by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }
  var quantity by remember { mutableStateOf("") }
  var foodType by remember { mutableStateOf("Cooked Meals") }
  var foodTypeExpanded by remember { mutableStateOf(false) }
  var perishable by remember { mutableStateOf(true) }
  var pickupAddress by remember { mutableStateOf("") }
  var deadlineTime by remember { mutableStateOf("") }
  var formError by remember { mutableStateOf<String?>(null) }

  // Requests relevant to my donations
  val myDonationIds = remember(myDonations) { myDonations.map { it.id }.toSet() }
  val incomingRequests = remember(allRequests, myDonationIds) {
    allRequests.filter { it.donationId in myDonationIds }
  }

  // Pre-fill address if food partner
  if (pickupAddress.isEmpty() && myPartner != null) {
    pickupAddress = myPartner?.address ?: ""
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFFF7FBF7))
  ) {
    // If pending partner notice
    if (currentUser?.role == "FOOD_PARTNER" && myPartner?.approvalStatus == "PENDING") {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
        shape = RoundedCornerShape(12.dp)
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.Info, contentDescription = null, tint = FoodShareAccentOrange)
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "Your Food Partner account is currently Pending Admin Approval. You can draft listings; they will be visible to receivers once approved.",
            fontSize = 12.sp,
            color = Color(0xFF5D4037)
          )
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
        text = { Text("My Donations (${myDonations.size})", fontWeight = FontWeight.Bold) },
        modifier = Modifier.testTag("donate_tab_my_donations")
      )
      Tab(
        selected = selectedTab == 1,
        onClick = { selectedTab = 1 },
        text = { Text("Post Food", fontWeight = FontWeight.Bold) },
        modifier = Modifier.testTag("donate_tab_post")
      )
      Tab(
        selected = selectedTab == 2,
        onClick = { selectedTab = 2 },
        text = { Text("Requests (${incomingRequests.count { it.status == "PENDING" }})", fontWeight = FontWeight.Bold) },
        modifier = Modifier.testTag("donate_tab_requests")
      )
    }

    when (selectedTab) {
      0 -> {
        // My Donations List
        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          if (myDonations.isEmpty()) {
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
                    text = "No donations posted yet.",
                    fontWeight = FontWeight.Bold,
                    color = FoodShareGreenPrimary
                  )
                  Spacer(modifier = Modifier.height(6.dp))
                  Text(
                    text = "Tap 'Post Food' above to share your extra food with people in need.",
                    fontSize = 13.sp,
                    color = Color(0xFF666666)
                  )
                  Spacer(modifier = Modifier.height(16.dp))
                  Button(
                    onClick = { selectedTab = 1 },
                    colors = ButtonDefaults.buttonColors(containerColor = FoodShareGreenPrimary)
                  ) {
                    Text("Post Food Now")
                  }
                }
              }
            }
          } else {
            items(myDonations) { donation ->
              DonationCard(
                donation = donation,
                onDeleteClick = { viewModel.deleteDonation(donation.id) }
              )
            }
          }
        }
      }

      1 -> {
        // Post New Food Form
        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(containerColor = Color.White),
              elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
              Column(modifier = Modifier.padding(18.dp)) {
                Text(
                  text = "Post Surplus Food Donation",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold,
                  color = FoodShareGreenPrimary
                )
                Text(
                  text = "Specify food items, pickup location, and consumption deadline.",
                  fontSize = 12.sp,
                  color = Color(0xFF666666)
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                  value = foodTitle,
                  onValueChange = { foodTitle = it; formError = null },
                  label = { Text("Food Title / Dish Name *") },
                  placeholder = { Text("e.g. 20 Freshly Baked Sourdough Loaves") },
                  modifier = Modifier.fillMaxWidth().testTag("donation_title_input"),
                  singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Food Type Dropdown
                ExposedDropdownMenuBox(
                  expanded = foodTypeExpanded,
                  onExpandedChange = { foodTypeExpanded = !foodTypeExpanded }
                ) {
                  OutlinedTextField(
                    value = foodType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Food Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = foodTypeExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor().testTag("donation_category_select")
                  )
                  ExposedDropdownMenu(
                    expanded = foodTypeExpanded,
                    onDismissRequest = { foodTypeExpanded = false }
                  ) {
                    listOf("Cooked Meals", "Baked Goods", "Produce & Fresh Fruits", "Packaged & Canned Goods", "Dairy & Beverages").forEach { type ->
                      DropdownMenuItem(
                        text = { Text(type) },
                        onClick = { foodType = type; foodTypeExpanded = false }
                      )
                    }
                  }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                  value = quantity,
                  onValueChange = { quantity = it; formError = null },
                  label = { Text("Quantity & Servings *") },
                  placeholder = { Text("e.g. 25 meal boxes, 10 kg, 30 sandwiches") },
                  modifier = Modifier.fillMaxWidth().testTag("donation_quantity_input"),
                  singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                  value = pickupAddress,
                  onValueChange = { pickupAddress = it; formError = null },
                  label = { Text("Pickup Address *") },
                  placeholder = { Text("e.g. 142 Market St, Back Door Kitchen") },
                  modifier = Modifier.fillMaxWidth().testTag("donation_address_input"),
                  singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                  value = deadlineTime,
                  onValueChange = { deadlineTime = it },
                  label = { Text("Pickup Deadline / Window") },
                  placeholder = { Text("e.g. Today by 8:30 PM") },
                  modifier = Modifier.fillMaxWidth().testTag("donation_deadline_input"),
                  singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                  value = description,
                  onValueChange = { description = it },
                  label = { Text("Description & Handling Details") },
                  placeholder = { Text("e.g. Keep refrigerated, vegetarian, contains nuts, packed in sanitary containers") },
                  modifier = Modifier.fillMaxWidth().testTag("donation_description_input"),
                  minLines = 2
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column {
                    Text("Perishable Item", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Text("Requires timely pickup & consumption", fontSize = 11.sp, color = Color(0xFF666666))
                  }
                  Switch(
                    checked = perishable,
                    onCheckedChange = { perishable = it },
                    modifier = Modifier.testTag("donation_perishable_switch")
                  )
                }

                if (formError != null) {
                  Spacer(modifier = Modifier.height(8.dp))
                  Text(
                    text = formError ?: "",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp
                  )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                  onClick = {
                    viewModel.createDonation(
                      title = foodTitle,
                      description = description,
                      quantity = quantity,
                      foodType = foodType,
                      perishable = perishable,
                      pickupAddress = pickupAddress,
                      deadline = deadlineTime,
                      onSuccess = {
                        foodTitle = ""
                        description = ""
                        quantity = ""
                        deadlineTime = ""
                        selectedTab = 0
                      },
                      onError = { formError = it }
                    )
                  },
                  modifier = Modifier.fillMaxWidth().testTag("donation_submit_button"),
                  colors = ButtonDefaults.buttonColors(containerColor = FoodShareGreenPrimary)
                ) {
                  Text("Publish Food Donation")
                }
              }
            }
          }
        }
      }

      2 -> {
        // Incoming Requests
        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          if (incomingRequests.isEmpty()) {
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
                    text = "No food requests received yet.",
                    fontWeight = FontWeight.Bold,
                    color = FoodShareGreenPrimary
                  )
                  Spacer(modifier = Modifier.height(6.dp))
                  Text(
                    text = "When an NGO or receiver requests your food, it will appear here for your approval.",
                    fontSize = 12.sp,
                    color = Color(0xFF757575)
                  )
                }
              }
            }
          } else {
            items(incomingRequests) { req ->
              val matchedDonation = myDonations.find { it.id == req.donationId }
              FoodRequestCard(
                request = req,
                onAcceptClick = if (matchedDonation != null) {
                  { viewModel.acceptFoodRequest(req, matchedDonation) }
                } else null,
                onRejectClick = { viewModel.rejectFoodRequest(req) }
              )
            }
          }
        }
      }
    }
  }
}
