package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.ui.FoodShareViewModel
import com.example.ui.components.BottomNavBar
import com.example.ui.components.TopNavBar
import com.example.ui.navigation.Screen
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DonateFoodScreen
import com.example.ui.screens.FindFoodScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.NearbyPartnersScreen
import com.example.ui.screens.VolunteerTasksScreen
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {

  private val viewModel: FoodShareViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        FoodShareApp(viewModel = viewModel)
      }
    }
  }
}

@Composable
fun FoodShareApp(viewModel: FoodShareViewModel) {
  val currentScreen by viewModel.currentScreen.collectAsState()
  val currentUser by viewModel.currentUser.collectAsState()
  val snackbarHostState = remember { SnackbarHostState() }

  // Handle hardware back press
  BackHandler(enabled = currentScreen != Screen.Home) {
    viewModel.navigateTo(Screen.Home)
  }

  // Observe feedback messages
  LaunchedEffect(Unit) {
    viewModel.snackbarMessage.collectLatest { msg ->
      snackbarHostState.showSnackbar(msg)
    }
  }

  Scaffold(
    topBar = {
      TopNavBar(
        currentUser = currentUser,
        onLogoutClick = { viewModel.logout() },
        onProfileClick = { viewModel.navigateTo(Screen.Dashboard) }
      )
    },
    bottomBar = {
      BottomNavBar(
        currentScreen = currentScreen,
        currentUser = currentUser,
        onNavigate = { screen -> viewModel.navigateTo(screen) }
      )
    },
    snackbarHost = { SnackbarHost(snackbarHostState) },
    modifier = Modifier.fillMaxSize()
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      when (currentScreen) {
        Screen.Home -> HomeScreen(
          viewModel = viewModel,
          onNavigate = { screen -> viewModel.navigateTo(screen) }
        )
        Screen.DonateFood -> DonateFoodScreen(
          viewModel = viewModel
        )
        Screen.FindFood -> FindFoodScreen(
          viewModel = viewModel,
          onNavigateToAuth = { viewModel.navigateTo(Screen.Auth) }
        )
        Screen.Volunteers -> VolunteerTasksScreen(
          viewModel = viewModel,
          onNavigateToAuth = { viewModel.navigateTo(Screen.Auth) }
        )
        Screen.NearbyPartners -> NearbyPartnersScreen(
          viewModel = viewModel
        )
        Screen.Dashboard -> DashboardScreen(
          viewModel = viewModel,
          onNavigate = { screen -> viewModel.navigateTo(screen) }
        )
        Screen.AdminDashboard -> AdminDashboardScreen(
          viewModel = viewModel,
          onNavigateToAuth = { viewModel.navigateTo(Screen.Auth) }
        )
        Screen.Auth -> LoginScreen(
          viewModel = viewModel,
          onAuthSuccess = { viewModel.navigateTo(Screen.Dashboard) }
        )
      }
    }
  }
}
