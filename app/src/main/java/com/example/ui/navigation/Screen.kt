package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
  object Home : Screen("home", "Home", Icons.Filled.Home)
  object DonateFood : Screen("donate_food", "Donate Food", Icons.Filled.VolunteerActivism)
  object FindFood : Screen("find_food", "Find Food", Icons.Outlined.Search)
  object Volunteers : Screen("volunteers", "Volunteers", Icons.Filled.Favorite)
  object NearbyPartners : Screen("nearby_partners", "Nearby Partners", Icons.Filled.LocationOn)
  object Dashboard : Screen("dashboard", "Dashboard", Icons.Filled.Dashboard)
  object AdminDashboard : Screen("admin_dashboard", "Admin", Icons.Filled.AdminPanelSettings)
  object Auth : Screen("auth", "Sign In", Icons.Filled.Home)
}
