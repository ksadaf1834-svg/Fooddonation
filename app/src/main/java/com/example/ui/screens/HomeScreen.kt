package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.FoodShareViewModel
import com.example.ui.components.DonationCard
import com.example.ui.navigation.Screen
import com.example.ui.theme.FoodShareAccentOrange
import com.example.ui.theme.FoodShareGreenContainer
import com.example.ui.theme.FoodShareGreenPrimary

@Composable
fun HomeScreen(
  viewModel: FoodShareViewModel,
  onNavigate: (Screen) -> Unit,
  modifier: Modifier = Modifier
) {
  val currentUser by viewModel.currentUser.collectAsState()
  val availableDonations by viewModel.availableDonations.collectAsState()
  val allVolunteers by viewModel.allVolunteers.collectAsState()
  val allPartners by viewModel.allPartners.collectAsState()

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(8.dp))
      // Hero Banner Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column {
          Image(
            painter = painterResource(id = R.drawable.ic_foodshare_hero),
            contentDescription = "FoodShare Community Banner",
            modifier = Modifier
              .fillMaxWidth()
              .height(180.dp),
            contentScale = ContentScale.Crop
          )
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "Share Surplus Food, Feed Communities",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold,
              color = FoodShareGreenPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Bridging local restaurants, bakeries, grocery stores, and passionate volunteers with shelters and families in need.",
              style = MaterialTheme.typography.bodyMedium,
              color = Color(0xFF555555)
            )
          }
        }
      }
    }

    // Community Impact Stats
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        ImpactStatCard(
          number = "${availableDonations.size}",
          label = "Available Donations",
          color = FoodShareGreenPrimary,
          modifier = Modifier.weight(1f)
        )
        ImpactStatCard(
          number = "${allPartners.size}",
          label = "Food Partners",
          color = FoodShareAccentOrange,
          modifier = Modifier.weight(1f)
        )
        ImpactStatCard(
          number = "${allVolunteers.size}",
          label = "Active Volunteers",
          color = Color(0xFF1565C0),
          modifier = Modifier.weight(1f)
        )
      }
    }

    // Quick Action Grid
    item {
      Text(
        text = "Quick Actions",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = FoodShareGreenPrimary
      )
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        QuickActionCard(
          title = "Donate Food",
          subtitle = "Share surplus meals",
          icon = Icons.Default.VolunteerActivism,
          tag = "home_action_donate",
          onClick = {
            if (currentUser == null) onNavigate(Screen.Auth) else onNavigate(Screen.DonateFood)
          },
          modifier = Modifier.weight(1f)
        )
        QuickActionCard(
          title = "Find Food",
          subtitle = "NGO & shelter requests",
          icon = Icons.Default.Restaurant,
          tag = "home_action_find",
          onClick = { onNavigate(Screen.FindFood) },
          modifier = Modifier.weight(1f)
        )
      }
      Spacer(modifier = Modifier.height(10.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        QuickActionCard(
          title = "Volunteer Tasks",
          subtitle = "Pickups & deliveries",
          icon = Icons.Default.Favorite,
          tag = "home_action_volunteer",
          onClick = { onNavigate(Screen.Volunteers) },
          modifier = Modifier.weight(1f)
        )
        QuickActionCard(
          title = "Nearby Partners",
          subtitle = "Bakeries & stores map",
          icon = Icons.Default.LocationOn,
          tag = "home_action_nearby",
          onClick = { onNavigate(Screen.NearbyPartners) },
          modifier = Modifier.weight(1f)
        )
      }
    }

    // Available Surplus Food Listings Section
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Available Food Donations",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = FoodShareGreenPrimary
        )
        Text(
          text = "See All",
          color = FoodShareGreenPrimary,
          fontWeight = FontWeight.SemiBold,
          fontSize = 14.sp,
          modifier = Modifier
            .clickable { onNavigate(Screen.FindFood) }
            .testTag("home_see_all_donations")
        )
      }
    }

    if (availableDonations.isEmpty()) {
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = "No active surplus listings right now.",
              color = Color(0xFF757575),
              fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "Restaurants & partners post new surplus regularly.",
              fontSize = 12.sp,
              color = Color(0xFF9E9E9E)
            )
          }
        }
      }
    } else {
      items(availableDonations.take(4)) { donation ->
        DonationCard(
          donation = donation,
          onRequestClick = {
            if (currentUser == null) onNavigate(Screen.Auth) else onNavigate(Screen.FindFood)
          }
        )
      }
    }

    item {
      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}

@Composable
fun ImpactStatCard(
  number: String,
  label: String,
  color: Color,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Column(
      modifier = Modifier.padding(12.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = number,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.ExtraBold,
        color = color
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = label,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        color = Color(0xFF555555),
        lineHeight = 14.sp
      )
    }
  }
}

@Composable
fun QuickActionCard(
  title: String,
  subtitle: String,
  icon: ImageVector,
  tag: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .clickable { onClick() }
      .testTag(tag),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Row(
      modifier = Modifier.padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(42.dp)
          .clip(CircleShape)
          .background(FoodShareGreenContainer),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = FoodShareGreenPrimary,
          modifier = Modifier.size(22.dp)
        )
      }
      Spacer(modifier = Modifier.width(10.dp))
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = title,
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp,
          color = FoodShareGreenPrimary
        )
        Text(
          text = subtitle,
          fontSize = 11.sp,
          color = Color(0xFF666666)
        )
      }
      Icon(
        imageVector = Icons.Default.ArrowForward,
        contentDescription = null,
        tint = Color(0xFFBDBDBD),
        modifier = Modifier.size(16.dp)
      )
    }
  }
}
