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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.FoodShareViewModel
import com.example.ui.components.StatusBadge
import com.example.ui.navigation.Screen
import com.example.ui.theme.FoodShareAccentOrange
import com.example.ui.theme.FoodShareGreenContainer
import com.example.ui.theme.FoodShareGreenPrimary

@Composable
fun DashboardScreen(
  viewModel: FoodShareViewModel,
  onNavigate: (Screen) -> Unit,
  modifier: Modifier = Modifier
) {
  val currentUser by viewModel.currentUser.collectAsState()
  val myDonations by viewModel.myDonations.collectAsState()
  val myRequests by viewModel.myRequests.collectAsState()
  val myAcceptedTasks by viewModel.myAcceptedTasks.collectAsState()
  val myVolunteerProfile by viewModel.myVolunteerProfile.collectAsState()
  val myPartner by viewModel.myFoodPartner.collectAsState()

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFFF7FBF7))
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    if (currentUser == null) {
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
          Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text("Please Sign In", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = FoodShareGreenPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Log in to view your personalized role dashboard, donations, or assigned tasks.", color = Color(0xFF666666))
            Spacer(modifier = Modifier.height(16.dp))
            Button(
              onClick = { onNavigate(Screen.Auth) },
              colors = ButtonDefaults.buttonColors(containerColor = FoodShareGreenPrimary),
              modifier = Modifier.testTag("dashboard_signin_button")
            ) {
              Text("Go to Sign In")
            }
          }
        }
      }
    } else {
      val user = currentUser!!

      // User Profile Header
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
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
                .size(54.dp)
                .clip(CircleShape)
                .background(FoodShareGreenContainer),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = when (user.role) {
                  "ADMIN" -> Icons.Default.AdminPanelSettings
                  "VOLUNTEER" -> Icons.Default.Favorite
                  "FOOD_PARTNER" -> Icons.Default.Business
                  else -> Icons.Default.AccountCircle
                },
                contentDescription = null,
                tint = FoodShareGreenPrimary,
                modifier = Modifier.size(32.dp)
              )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = user.fullName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = FoodShareGreenPrimary
              )
              Text(
                text = user.email,
                fontSize = 12.sp,
                color = Color(0xFF666666)
              )
              Spacer(modifier = Modifier.height(4.dp))
              Row(verticalAlignment = Alignment.CenterVertically) {
                StatusBadge(status = user.role)
                if (user.role == "FOOD_PARTNER" && myPartner != null) {
                  Spacer(modifier = Modifier.width(6.dp))
                  StatusBadge(status = myPartner?.approvalStatus ?: "PENDING")
                }
              }
            }
          }
        }
      }

      // Role-Specific Overview
      when (user.role) {
        "FOOD_PARTNER" -> {
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Text(
                  text = "Food Partner Overview",
                  fontWeight = FontWeight.Bold,
                  style = MaterialTheme.typography.titleMedium,
                  color = FoodShareGreenPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))
                myPartner?.let { partner ->
                  Text("Business: ${partner.businessName} (${partner.businessType})", fontSize = 13.sp)
                  Text("Address: ${partner.address}", fontSize = 12.sp, color = Color(0xFF555555))
                  Text("Surplus: ${partner.donationDetails}", fontSize = 12.sp, color = FoodShareAccentOrange)
                  Spacer(modifier = Modifier.height(8.dp))
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text("Accepting Surplus Donations", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Switch(
                      checked = partner.isAvailableForDonations,
                      onCheckedChange = { viewModel.updatePartnerAvailability(partner.id, it) },
                      modifier = Modifier.testTag("partner_availability_switch")
                    )
                  }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                  Button(
                    onClick = { onNavigate(Screen.DonateFood) },
                    modifier = Modifier.weight(1f).testTag("partner_post_surplus_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = FoodShareGreenPrimary)
                  ) {
                    Text("Post Surplus Food")
                  }
                }
              }
            }
          }
        }

        "VOLUNTEER" -> {
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Text(
                  text = "Volunteer Hero Stats",
                  fontWeight = FontWeight.Bold,
                  style = MaterialTheme.typography.titleMedium,
                  color = FoodShareGreenPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceAround
                ) {
                  Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${myAcceptedTasks.size}", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = FoodShareGreenPrimary)
                    Text("Current Tasks", fontSize = 12.sp, color = Color(0xFF666666))
                  }
                  Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${myVolunteerProfile?.completedTaskCount ?: 0}", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color(0xFF2E7D32))
                    Text("Completed Deliveries", fontSize = 12.sp, color = Color(0xFF666666))
                  }
                }
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                  onClick = { onNavigate(Screen.Volunteers) },
                  modifier = Modifier.fillMaxWidth().testTag("volunteer_browse_tasks_button"),
                  colors = ButtonDefaults.buttonColors(containerColor = FoodShareGreenPrimary)
                ) {
                  Text("Find Available Pickup & Delivery Tasks")
                }
              }
            }
          }
        }

        "DONOR" -> {
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Text(
                  text = "Donor Impact",
                  fontWeight = FontWeight.Bold,
                  style = MaterialTheme.typography.titleMedium,
                  color = FoodShareGreenPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceAround
                ) {
                  Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${myDonations.size}", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = FoodShareGreenPrimary)
                    Text("Total Donations", fontSize = 12.sp, color = Color(0xFF666666))
                  }
                  Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${myDonations.count { it.status == "AVAILABLE" }}", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = FoodShareAccentOrange)
                    Text("Active Listings", fontSize = 12.sp, color = Color(0xFF666666))
                  }
                }
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                  onClick = { onNavigate(Screen.DonateFood) },
                  modifier = Modifier.fillMaxWidth().testTag("donor_manage_listings_button"),
                  colors = ButtonDefaults.buttonColors(containerColor = FoodShareGreenPrimary)
                ) {
                  Text("Manage Food Donations & Requests")
                }
              }
            }
          }
        }

        "RECEIVER" -> {
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Text(
                  text = "Receiver / NGO Summary",
                  fontWeight = FontWeight.Bold,
                  style = MaterialTheme.typography.titleMedium,
                  color = FoodShareGreenPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceAround
                ) {
                  Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${myRequests.size}", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = FoodShareGreenPrimary)
                    Text("Food Requests", fontSize = 12.sp, color = Color(0xFF666666))
                  }
                  Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${myRequests.count { it.status == "ACCEPTED" }}", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = FoodShareGreenPrimary)
                    Text("Accepted", fontSize = 12.sp, color = Color(0xFF666666))
                  }
                }
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                  onClick = { onNavigate(Screen.FindFood) },
                  modifier = Modifier.fillMaxWidth().testTag("receiver_find_food_button"),
                  colors = ButtonDefaults.buttonColors(containerColor = FoodShareGreenPrimary)
                ) {
                  Text("Browse Available Food Surplus")
                }
              }
            }
          }
        }

        "ADMIN" -> {
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Text(
                  text = "Administrator Access",
                  fontWeight = FontWeight.Bold,
                  style = MaterialTheme.typography.titleMedium,
                  color = FoodShareGreenPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = "You have full administrator privileges to approve partners, moderate listings, and monitor tasks.",
                  fontSize = 12.sp,
                  color = Color(0xFF666666)
                )
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                  onClick = { onNavigate(Screen.AdminDashboard) },
                  modifier = Modifier.fillMaxWidth().testTag("goto_admin_dashboard_button"),
                  colors = ButtonDefaults.buttonColors(containerColor = FoodShareGreenPrimary)
                ) {
                  Text("Open Admin Dashboard")
                }
              }
            }
          }
        }
      }

      // Logout button
      item {
        OutlinedButton(
          onClick = { viewModel.logout() },
          modifier = Modifier.fillMaxWidth().testTag("dashboard_logout_button")
        ) {
          Text("Log Out of FoodShare")
        }
      }
    }
  }
}
