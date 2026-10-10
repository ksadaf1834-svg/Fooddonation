package com.example.ui.screens

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NearbyPartner
import com.example.ui.FoodShareViewModel
import com.example.ui.components.OsmMapView
import com.example.ui.components.StatusBadge
import com.example.ui.theme.FoodShareAccentOrange
import com.example.ui.theme.FoodShareGreenContainer
import com.example.ui.theme.FoodShareGreenPrimary

@Composable
fun NearbyPartnersScreen(
  viewModel: FoodShareViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val nearbyPartners by viewModel.nearbyPartners.collectAsState()
  val isSearching by viewModel.isSearchingNearby.collectAsState()
  val userCoords by viewModel.userCoordinates.collectAsState()
  val selectedPartnerForMap by viewModel.selectedPartnerForMap.collectAsState()

  var searchQuery by remember { mutableStateOf("") }
  var selectedCategory by remember { mutableStateOf("All") }
  var viewMode by remember { mutableIntStateOf(0) } // 0: List View, 1: OpenStreetMap Leaflet View

  // Runtime location permission launcher
  val locationPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    if (isGranted) {
      viewModel.fetchDeviceLocation()
    } else {
      viewModel.showMessage("Location access denied. You can search manually by entering any city, area, or PIN.")
    }
  }

  val categoryFilters = listOf("All", "Restaurants", "Bakeries", "Grocery Stores", "NGOs")

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFFF7FBF7))
  ) {
    // Header & Search section
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .background(Color.White)
        .padding(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Nearby Food Support",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = FoodShareGreenPrimary
          )
          Text(
            text = "Find restaurants, bakeries, stores & food banks",
            fontSize = 12.sp,
            color = Color(0xFF666666)
          )
        }

        // Toggle View Mode (List / Map)
        Row {
          IconButton(
            onClick = { viewMode = 0 },
            modifier = Modifier.testTag("nearby_toggle_list")
          ) {
            Icon(
              imageVector = Icons.Default.List,
              contentDescription = "List View",
              tint = if (viewMode == 0) FoodShareGreenPrimary else Color(0xFF9E9E9E)
            )
          }
          IconButton(
            onClick = { viewMode = 1 },
            modifier = Modifier.testTag("nearby_toggle_map")
          ) {
            Icon(
              imageVector = Icons.Default.Map,
              contentDescription = "Map View",
              tint = if (viewMode == 1) FoodShareGreenPrimary else Color(0xFF9E9E9E)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Search field + My Location button
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("Search Mumbai areas, PIN (e.g. Bandra, Andheri, Dadar)...") },
          leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = FoodShareGreenPrimary) },
          trailingIcon = {
            if (searchQuery.isNotEmpty()) {
              IconButton(onClick = {
                searchQuery = ""
                viewModel.loadNearbyPartners(null, selectedCategory)
              }) {
                Icon(Icons.Default.Close, contentDescription = "Clear")
              }
            }
          },
          modifier = Modifier
            .weight(1f)
            .testTag("nearby_search_input"),
          singleLine = true
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Trigger search or GPS
        IconButton(
          onClick = {
            if (searchQuery.isNotBlank()) {
              viewModel.loadNearbyPartners(searchQuery, selectedCategory)
            } else {
              locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
          },
          modifier = Modifier
            .background(FoodShareGreenContainer, RoundedCornerShape(12.dp))
            .testTag("nearby_search_action_button")
        ) {
          Icon(
            imageVector = if (searchQuery.isNotBlank()) Icons.Default.Search else Icons.Default.MyLocation,
            contentDescription = "Use My GPS Location or Search",
            tint = FoodShareGreenPrimary
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Category filter pills
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        items(categoryFilters) { cat ->
          FilterChip(
            selected = selectedCategory == cat,
            onClick = {
              selectedCategory = cat
              viewModel.loadNearbyPartners(searchQuery.ifBlank { null }, cat)
            },
            label = { Text(cat, fontSize = 12.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = FoodShareGreenContainer,
              selectedLabelColor = FoodShareGreenPrimary
            ),
            modifier = Modifier.testTag("nearby_filter_$cat")
          )
        }
      }
    }

    if (isSearching) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(32.dp),
        contentAlignment = Alignment.Center
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          CircularProgressIndicator(color = FoodShareGreenPrimary, modifier = Modifier.size(24.dp))
          Spacer(modifier = Modifier.width(12.dp))
          Text("Locating verified nearby food partners...", color = Color(0xFF666666), fontSize = 13.sp)
        }
      }
    }

    // Main Content: List or Map
    if (viewMode == 0) {
      // LIST VIEW
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        if (nearbyPartners.isEmpty() && !isSearching) {
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
                  text = if (searchQuery.isBlank()) "Search for Nearby Food Support" else "No partners found for \"$searchQuery\"",
                  fontWeight = FontWeight.Bold,
                  color = FoodShareGreenPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = if (searchQuery.isBlank()) 
                    "Enter a city, neighborhood, or PIN code above, or tap the GPS icon to find restaurants, bakeries, and food banks near you."
                  else 
                    "Try searching another city, neighborhood, or PIN code.",
                  fontSize = 12.sp,
                  color = Color(0xFF757575)
                )
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                  onClick = {
                    searchQuery = ""
                    selectedCategory = "All"
                    viewModel.loadNearbyPartners(null, "All")
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = FoodShareGreenPrimary)
                ) {
                  Text("Reset Search")
                }
              }
            }
          }
        } else {
          items(nearbyPartners) { partner ->
            NearbyPartnerCard(
              partner = partner,
              onViewOnMap = {
                viewModel.selectPartnerForMap(partner)
                viewMode = 1 // Switch to map view focused on partner
              },
              onCallClick = {
                if (partner.phone.isNotBlank() && !partner.phone.contains("Community")) {
                  val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${partner.phone}"))
                  context.startActivity(intent)
                } else {
                  viewModel.showMessage("Phone contact not public for this organization.")
                }
              }
            )
          }
        }
      }
    } else {
      // OPENSTREETMAP LEAFLET VIEW
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(12.dp)
      ) {
        OsmMapView(
          centerLat = userCoords.first,
          centerLon = userCoords.second,
          partners = nearbyPartners,
          selectedPartner = selectedPartnerForMap,
          modifier = Modifier.fillMaxSize()
        )

        // Floating Info banner if a partner is selected
        selectedPartnerForMap?.let { partner ->
          Card(
            modifier = Modifier
              .align(Alignment.BottomCenter)
              .fillMaxWidth()
              .padding(8.dp),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
          ) {
            Row(
              modifier = Modifier.padding(12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(partner.name, fontWeight = FontWeight.Bold, color = FoodShareGreenPrimary)
                Text("${partner.category} • ${partner.distanceKm} km away", fontSize = 12.sp, color = Color(0xFF666666))
                Text(partner.donationAvailability, fontSize = 11.sp, color = FoodShareAccentOrange)
              }
              IconButton(onClick = { viewModel.selectPartnerForMap(null) }) {
                Icon(Icons.Default.Close, contentDescription = "Close")
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun NearbyPartnerCard(
  partner: NearbyPartner,
  onViewOnMap: () -> Unit,
  onCallClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier.fillMaxWidth(),
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
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
          Box(
            modifier = Modifier
              .size(38.dp)
              .clip(CircleShape)
              .background(FoodShareGreenContainer),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Store, contentDescription = null, tint = FoodShareGreenPrimary, modifier = Modifier.size(20.dp))
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = partner.name,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = FoodShareGreenPrimary
            )
            Text(
              text = partner.category,
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              color = Color(0xFF666666)
            )
          }
        }
        StatusBadge(status = "${partner.distanceKm} km")
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.LocationOn, contentDescription = null, tint = FoodShareGreenPrimary, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(partner.address, fontSize = 13.sp, color = Color(0xFF424242))
      }

      Spacer(modifier = Modifier.height(6.dp))

      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Call, contentDescription = null, tint = Color(0xFF757575), modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(partner.phone, fontSize = 12.sp, color = Color(0xFF666666))
      }

      Spacer(modifier = Modifier.height(8.dp))

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color(0xFFF1F8F1), RoundedCornerShape(8.dp))
          .padding(8.dp)
      ) {
        Text(
          text = "Surplus Support: ${partner.donationAvailability}",
          fontSize = 12.sp,
          color = FoodShareGreenPrimary,
          fontWeight = FontWeight.Medium
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedButton(
          onClick = onCallClick,
          modifier = Modifier
            .weight(1f)
            .testTag("partner_call_button_${partner.id}")
        ) {
          Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Call", fontSize = 12.sp)
        }

        Button(
          onClick = onViewOnMap,
          modifier = Modifier
            .weight(1f)
            .testTag("partner_view_map_button_${partner.id}"),
          colors = ButtonDefaults.buttonColors(containerColor = FoodShareGreenPrimary)
        ) {
          Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("View on Map", fontSize = 12.sp)
        }
      }
    }
  }
}
