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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.FoodPartnerEntity
import com.example.data.local.entity.UserEntity
import com.example.ui.FoodShareViewModel
import com.example.ui.components.DonationCard
import com.example.ui.components.StatusBadge
import com.example.ui.components.VolunteerTaskCard
import com.example.ui.theme.FoodShareAccentOrange
import com.example.ui.theme.FoodShareGreenContainer
import com.example.ui.theme.FoodShareGreenPrimary

@Composable
fun AdminDashboardScreen(
  viewModel: FoodShareViewModel,
  onNavigateToAuth: () -> Unit,
  modifier: Modifier = Modifier
) {
  val currentUser by viewModel.currentUser.collectAsState()
  val allUsers by viewModel.allUsers.collectAsState()
  val pendingPartners by viewModel.pendingPartners.collectAsState()
  val allPartners by viewModel.allPartners.collectAsState()
  val allDonations by viewModel.allDonations.collectAsState()
  val allTasks by viewModel.allTasks.collectAsState()

  var selectedTab by remember { mutableIntStateOf(0) } // 0: Partners, 1: Users, 2: Donations, 3: Tasks

  // Server/Repository Authorization check
  if (currentUser?.role != "ADMIN") {
    Box(
      modifier = modifier
        .fillMaxSize()
        .background(Color(0xFFF7FBF7))
        .padding(24.dp),
      contentAlignment = Alignment.Center
    ) {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(
          modifier = Modifier.padding(24.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(48.dp)
          )
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = "Access Restricted to Admin",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.error
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "This area requires authorized Admin credentials.",
            fontSize = 13.sp,
            color = Color(0xFF666666)
          )
          Spacer(modifier = Modifier.height(16.dp))
          Button(
            onClick = onNavigateToAuth,
            colors = ButtonDefaults.buttonColors(containerColor = FoodShareGreenPrimary),
            modifier = Modifier.testTag("admin_login_prompt_button")
          ) {
            Text("Sign In with Admin Account")
          }
        }
      }
    }
    return
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFFF7FBF7))
  ) {
    // Admin Control Header
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .background(Color.White)
        .padding(16.dp)
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(40.dp)
            .background(FoodShareGreenContainer, RoundedCornerShape(10.dp)),
          contentAlignment = Alignment.Center
        ) {
          Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = FoodShareGreenPrimary)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
          Text(
            text = "FoodShare Admin Control",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = FoodShareGreenPrimary
          )
          Text(
            text = "Platform oversight, approval queues & audit controls",
            fontSize = 12.sp,
            color = Color(0xFF666666)
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Quick metric chips
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        MetricChip(label = "Pending Partners", count = "${pendingPartners.size}", color = FoodShareAccentOrange, modifier = Modifier.weight(1f))
        MetricChip(label = "Users", count = "${allUsers.size}", color = FoodShareGreenPrimary, modifier = Modifier.weight(1f))
        MetricChip(label = "Donations", count = "${allDonations.size}", color = Color(0xFF1565C0), modifier = Modifier.weight(1f))
        MetricChip(label = "Tasks", count = "${allTasks.size}", color = Color(0xFF6A1B9A), modifier = Modifier.weight(1f))
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
        text = { Text("Partners (${pendingPartners.size})", fontWeight = FontWeight.Bold) },
        modifier = Modifier.testTag("admin_tab_partners")
      )
      Tab(
        selected = selectedTab == 1,
        onClick = { selectedTab = 1 },
        text = { Text("Users", fontWeight = FontWeight.Bold) },
        modifier = Modifier.testTag("admin_tab_users")
      )
      Tab(
        selected = selectedTab == 2,
        onClick = { selectedTab = 2 },
        text = { Text("Donations", fontWeight = FontWeight.Bold) },
        modifier = Modifier.testTag("admin_tab_donations")
      )
      Tab(
        selected = selectedTab == 3,
        onClick = { selectedTab = 3 },
        text = { Text("Tasks", fontWeight = FontWeight.Bold) },
        modifier = Modifier.testTag("admin_tab_tasks")
      )
    }

    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      when (selectedTab) {
        0 -> {
          // PARTNERS (Pending approvals & active partners)
          if (allPartners.isEmpty()) {
            item {
              EmptyAdminState("No food partner registrations in system.")
            }
          } else {
            items(allPartners) { partner ->
              AdminPartnerCard(
                partner = partner,
                onApprove = { viewModel.adminApprovePartner(partner.id) },
                onReject = { viewModel.adminRejectPartner(partner.id) }
              )
            }
          }
        }

        1 -> {
          // USERS MANAGEMENT
          items(allUsers) { user ->
            AdminUserCard(
              user = user,
              onToggleSuspend = { viewModel.adminToggleUserSuspension(user.id, !user.isSuspended) },
              onMakeAdmin = { viewModel.adminUpdateUserRole(user.id, "ADMIN") }
            )
          }
        }

        2 -> {
          // DONATIONS MANAGEMENT
          if (allDonations.isEmpty()) {
            item {
              EmptyAdminState("No food donations posted yet.")
            }
          } else {
            items(allDonations) { donation ->
              DonationCard(
                donation = donation,
                onDeleteClick = { viewModel.deleteDonation(donation.id) }
              )
            }
          }
        }

        3 -> {
          // TASKS TRACKING
          if (allTasks.isEmpty()) {
            item {
              EmptyAdminState("No volunteer tasks scheduled.")
            }
          } else {
            items(allTasks) { task ->
              VolunteerTaskCard(
                task = task,
                currentVolunteerId = null // Admin views all
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun MetricChip(
  label: String,
  count: String,
  color: Color,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .background(Color(0xFFF1F5F1), RoundedCornerShape(8.dp))
      .padding(vertical = 8.dp, horizontal = 6.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(count, fontWeight = FontWeight.Bold, color = color, fontSize = 15.sp)
      Text(label, fontSize = 10.sp, color = Color(0xFF555555), maxLines = 1)
    }
  }
}

@Composable
fun AdminPartnerCard(
  partner: FoodPartnerEntity,
  onApprove: () -> Unit,
  onReject: () -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(partner.businessName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = FoodShareGreenPrimary)
          Text("${partner.businessType} • Contact: ${partner.contactPerson}", fontSize = 12.sp, color = Color(0xFF666666))
        }
        StatusBadge(status = partner.approvalStatus)
      }

      Spacer(modifier = Modifier.height(8.dp))
      Text("Email: ${partner.email} • Phone: ${partner.phone}", fontSize = 12.sp, color = Color(0xFF424242))
      Text("Address: ${partner.address}", fontSize = 12.sp, color = Color(0xFF555555))
      if (partner.donationDetails.isNotBlank()) {
        Text("Surplus: ${partner.donationDetails}", fontSize = 12.sp, color = FoodShareAccentOrange)
      }

      if (partner.approvalStatus == "PENDING") {
        Spacer(modifier = Modifier.height(12.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedButton(
            onClick = onReject,
            modifier = Modifier.weight(1f).testTag("admin_reject_partner_${partner.id}")
          ) {
            Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Reject", color = MaterialTheme.colorScheme.error)
          }

          Button(
            onClick = onApprove,
            modifier = Modifier.weight(1f).testTag("admin_approve_partner_${partner.id}"),
            colors = ButtonDefaults.buttonColors(containerColor = FoodShareGreenPrimary)
          ) {
            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Approve Partner")
          }
        }
      }
    }
  }
}

@Composable
fun AdminUserCard(
  user: UserEntity,
  onToggleSuspend: () -> Unit,
  onMakeAdmin: () -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(user.fullName, fontWeight = FontWeight.Bold, color = FoodShareGreenPrimary)
          Text("${user.email} • ${user.phone}", fontSize = 12.sp, color = Color(0xFF666666))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          StatusBadge(status = user.role)
          if (user.isSuspended) {
            StatusBadge(status = "SUSPENDED")
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
      ) {
        if (user.role != "ADMIN") {
          OutlinedButton(
            onClick = onMakeAdmin,
            modifier = Modifier.testTag("admin_grant_admin_${user.id}")
          ) {
            Text("Grant Admin", fontSize = 11.sp)
          }
          Spacer(modifier = Modifier.width(8.dp))
        }

        Button(
          onClick = onToggleSuspend,
          colors = ButtonDefaults.buttonColors(
            containerColor = if (user.isSuspended) FoodShareGreenPrimary else Color(0xFFBA1A1A)
          ),
          modifier = Modifier.testTag("admin_toggle_suspend_${user.id}")
        ) {
          Text(if (user.isSuspended) "Reactivate" else "Suspend Account", fontSize = 11.sp)
        }
      }
    }
  }
}

@Composable
fun EmptyAdminState(message: String) {
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
      Text(message, color = Color(0xFF757575), fontWeight = FontWeight.Medium)
    }
  }
}
