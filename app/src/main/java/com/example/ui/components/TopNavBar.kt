package com.example.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.entity.UserEntity
import com.example.ui.theme.FoodShareGreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavBar(
  currentUser: UserEntity?,
  onLogoutClick: () -> Unit,
  onProfileClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  TopAppBar(
    modifier = modifier,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.Eco,
          contentDescription = null,
          tint = Color.White
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "FoodShare",
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
      }
    },
    actions = {
      if (currentUser != null) {
        IconButton(
          onClick = onProfileClick,
          modifier = Modifier.testTag("nav_profile_button")
        ) {
          Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "User Profile",
            tint = Color.White
          )
        }
        IconButton(
          onClick = onLogoutClick,
          modifier = Modifier.testTag("nav_logout_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
            contentDescription = "Logout",
            tint = Color.White
          )
        }
      }
    },
    colors = TopAppBarDefaults.topAppBarColors(
      containerColor = FoodShareGreenPrimary
    )
  )
}
