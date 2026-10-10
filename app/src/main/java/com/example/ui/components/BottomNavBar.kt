package com.example.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import com.example.data.local.entity.UserEntity
import com.example.ui.navigation.Screen
import com.example.ui.theme.FoodShareGreenContainer
import com.example.ui.theme.FoodShareGreenPrimary

@Composable
fun BottomNavBar(
  currentScreen: Screen,
  currentUser: UserEntity?,
  onNavigate: (Screen) -> Unit,
  modifier: Modifier = Modifier
) {
  val items = when {
    currentUser == null -> listOf(
      Screen.Home,
      Screen.FindFood,
      Screen.NearbyPartners,
      Screen.Auth
    )
    currentUser.role == "ADMIN" -> listOf(
      Screen.Home,
      Screen.FindFood,
      Screen.Volunteers,
      Screen.NearbyPartners,
      Screen.AdminDashboard
    )
    currentUser.role == "VOLUNTEER" -> listOf(
      Screen.Home,
      Screen.Volunteers,
      Screen.NearbyPartners,
      Screen.Dashboard
    )
    currentUser.role in listOf("DONOR", "FOOD_PARTNER") -> listOf(
      Screen.Home,
      Screen.DonateFood,
      Screen.NearbyPartners,
      Screen.Dashboard
    )
    else -> listOf( // "RECEIVER"
      Screen.Home,
      Screen.FindFood,
      Screen.NearbyPartners,
      Screen.Dashboard
    )
  }

  NavigationBar(
    modifier = modifier,
    containerColor = Color.White
  ) {
    items.forEach { screen ->
      val selected = currentScreen.route == screen.route
      NavigationBarItem(
        modifier = Modifier.testTag("nav_item_${screen.route}"),
        selected = selected,
        onClick = { onNavigate(screen) },
        icon = { Icon(screen.icon, contentDescription = screen.title) },
        label = { Text(screen.title) },
        colors = NavigationBarItemDefaults.colors(
          selectedIconColor = FoodShareGreenPrimary,
          selectedTextColor = FoodShareGreenPrimary,
          indicatorColor = FoodShareGreenContainer,
          unselectedIconColor = Color(0xFF757575),
          unselectedTextColor = Color(0xFF757575)
        )
      )
    }
  }
}
