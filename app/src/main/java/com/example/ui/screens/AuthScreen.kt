package com.example.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.FoodShareViewModel
import com.example.ui.navigation.Screen
import com.example.ui.theme.FoodShareAccentOrange
import com.example.ui.theme.FoodShareGreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
  viewModel: FoodShareViewModel,
  onNavigate: (Screen) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableIntStateOf(0) } // 0: Login, 1: Register User, 2: Register Partner

  // Login state
  var loginEmail by remember { mutableStateOf("") }
  var loginPassword by remember { mutableStateOf("") }
  var loginPasswordVisible by remember { mutableStateOf(false) }
  var loginError by remember { mutableStateOf<String?>(null) }

  // Registration state
  var regName by remember { mutableStateOf("") }
  var regEmail by remember { mutableStateOf("") }
  var regPhone by remember { mutableStateOf("") }
  var regPassword by remember { mutableStateOf("") }
  var regPasswordVisible by remember { mutableStateOf(false) }
  var regRole by remember { mutableStateOf("DONOR") } // DONOR, RECEIVER, VOLUNTEER
  var regRoleExpanded by remember { mutableStateOf(false) }
  var regError by remember { mutableStateOf<String?>(null) }

  // Food Partner state
  var partnerBusinessName by remember { mutableStateOf("") }
  var partnerContactName by remember { mutableStateOf("") }
  var partnerEmail by remember { mutableStateOf("") }
  var partnerPhone by remember { mutableStateOf("") }
  var partnerPassword by remember { mutableStateOf("") }
  var partnerBusinessType by remember { mutableStateOf("Restaurant") }
  var partnerTypeExpanded by remember { mutableStateOf(false) }
  var partnerAddress by remember { mutableStateOf("") }
  var partnerDonationDetails by remember { mutableStateOf("") }
  var partnerError by remember { mutableStateOf<String?>(null) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFFF7FBF7))
      .verticalScroll(rememberScrollState())
      .padding(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Spacer(modifier = Modifier.height(12.dp))

    Text(
      text = "FoodShare Community",
      style = MaterialTheme.typography.headlineMedium,
      fontWeight = FontWeight.Bold,
      color = FoodShareGreenPrimary
    )
    Text(
      text = "Connect, Share Surplus, & Empower",
      fontSize = 14.sp,
      color = Color(0xFF666666)
    )

    Spacer(modifier = Modifier.height(20.dp))

    TabRow(
      selectedTabIndex = selectedTab,
      containerColor = Color.White,
      contentColor = FoodShareGreenPrimary
    ) {
      Tab(
        selected = selectedTab == 0,
        onClick = { selectedTab = 0 },
        text = { Text("Sign In", fontWeight = FontWeight.Bold) },
        modifier = Modifier.testTag("auth_tab_login")
      )
      Tab(
        selected = selectedTab == 1,
        onClick = { selectedTab = 1 },
        text = { Text("Register", fontWeight = FontWeight.Bold) },
        modifier = Modifier.testTag("auth_tab_register")
      )
      Tab(
        selected = selectedTab == 2,
        onClick = { selectedTab = 2 },
        text = { Text("Food Partner", fontWeight = FontWeight.Bold) },
        modifier = Modifier.testTag("auth_tab_partner")
      )
    }

    Spacer(modifier = Modifier.height(16.dp))

    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
      Column(modifier = Modifier.padding(20.dp)) {

        // TAB 0: LOGIN
        if (selectedTab == 0) {
          Text(
            text = "Welcome Back",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = FoodShareGreenPrimary
          )
          Text(
            text = "Log in to manage donations, requests, or volunteer tasks.",
            fontSize = 13.sp,
            color = Color(0xFF666666)
          )

          Spacer(modifier = Modifier.height(16.dp))

          OutlinedTextField(
            value = loginEmail,
            onValueChange = { loginEmail = it; loginError = null },
            label = { Text("Email Address") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("login_email_input"),
            singleLine = true
          )

          Spacer(modifier = Modifier.height(12.dp))

          OutlinedTextField(
            value = loginPassword,
            onValueChange = { loginPassword = it; loginError = null },
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            trailingIcon = {
              IconButton(onClick = { loginPasswordVisible = !loginPasswordVisible }) {
                Icon(
                  imageVector = if (loginPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                  contentDescription = null
                )
              }
            },
            visualTransformation = if (loginPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("login_password_input"),
            singleLine = true
          )

          if (loginError != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = loginError ?: "",
              color = MaterialTheme.colorScheme.error,
              fontSize = 12.sp
            )
          }

          Spacer(modifier = Modifier.height(20.dp))

          Button(
            onClick = {
              viewModel.login(
                email = loginEmail,
                pass = loginPassword,
                onSuccess = { onNavigate(Screen.Dashboard) },
                onError = { loginError = it }
              )
            },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("login_submit_button"),
            colors = ButtonDefaults.buttonColors(containerColor = FoodShareGreenPrimary)
          ) {
            Text("Sign In")
          }
        }

        // TAB 1: REGISTER USER (Donor, Receiver/NGO, Volunteer)
        else if (selectedTab == 1) {
          Text(
            text = "Create New Account",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = FoodShareGreenPrimary
          )
          Text(
            text = "Join as a Food Donor, Receiver / NGO, or Community Volunteer.",
            fontSize = 13.sp,
            color = Color(0xFF666666)
          )

          Spacer(modifier = Modifier.height(16.dp))

          OutlinedTextField(
            value = regName,
            onValueChange = { regName = it; regError = null },
            label = { Text("Full Name") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("register_name_input"),
            singleLine = true
          )

          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = regEmail,
            onValueChange = { regEmail = it; regError = null },
            label = { Text("Email Address") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("register_email_input"),
            singleLine = true
          )

          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = regPhone,
            onValueChange = { regPhone = it; regError = null },
            label = { Text("Phone Number") },
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("register_phone_input"),
            singleLine = true
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Role selection dropdown
          ExposedDropdownMenuBox(
            expanded = regRoleExpanded,
            onExpandedChange = { regRoleExpanded = !regRoleExpanded }
          ) {
            OutlinedTextField(
              value = when (regRole) {
                "DONOR" -> "Food Donor (Individual or Group)"
                "RECEIVER" -> "Receiver / NGO (Shelters, Food Bank)"
                "VOLUNTEER" -> "Volunteer (Pickup & Delivery)"
                else -> regRole
              },
              onValueChange = {},
              readOnly = true,
              label = { Text("Account Role") },
              trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = regRoleExpanded) },
              modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
                .testTag("register_role_select")
            )
            ExposedDropdownMenu(
              expanded = regRoleExpanded,
              onDismissRequest = { regRoleExpanded = false }
            ) {
              DropdownMenuItem(
                text = { Text("Food Donor") },
                onClick = { regRole = "DONOR"; regRoleExpanded = false }
              )
              DropdownMenuItem(
                text = { Text("Receiver / NGO") },
                onClick = { regRole = "RECEIVER"; regRoleExpanded = false }
              )
              DropdownMenuItem(
                text = { Text("Volunteer (Food Pickup & Delivery)") },
                onClick = { regRole = "VOLUNTEER"; regRoleExpanded = false }
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = regPassword,
            onValueChange = { regPassword = it; regError = null },
            label = { Text("Password (min 6 characters)") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            visualTransformation = if (regPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
              IconButton(onClick = { regPasswordVisible = !regPasswordVisible }) {
                Icon(
                  imageVector = if (regPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                  contentDescription = null
                )
              }
            },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("register_password_input"),
            singleLine = true
          )

          if (regError != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = regError ?: "",
              color = MaterialTheme.colorScheme.error,
              fontSize = 12.sp
            )
          }

          Spacer(modifier = Modifier.height(18.dp))

          Button(
            onClick = {
              viewModel.register(
                email = regEmail,
                pass = regPassword,
                fullName = regName,
                phone = regPhone,
                role = regRole,
                onSuccess = { onNavigate(Screen.Dashboard) },
                onError = { regError = it }
              )
            },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("register_submit_button"),
            colors = ButtonDefaults.buttonColors(containerColor = FoodShareGreenPrimary)
          ) {
            Text("Create Account")
          }
        }

        // TAB 2: FOOD PARTNER REGISTRATION (Restaurants, Bakeries, Food Stores)
        else if (selectedTab == 2) {
          Text(
            text = "Food Partner Registration",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = FoodShareGreenPrimary
          )
          Text(
            text = "For Restaurants, Bakeries, Supermarkets & Food Outlets.",
            fontSize = 13.sp,
            color = Color(0xFF666666)
          )

          Spacer(modifier = Modifier.height(14.dp))

          OutlinedTextField(
            value = partnerBusinessName,
            onValueChange = { partnerBusinessName = it; partnerError = null },
            label = { Text("Business / Organization Name") },
            leadingIcon = { Icon(Icons.Default.Store, contentDescription = null) },
            modifier = Modifier.fillMaxWidth().testTag("partner_business_name_input"),
            singleLine = true
          )

          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = partnerContactName,
            onValueChange = { partnerContactName = it; partnerError = null },
            label = { Text("Contact Person / Manager Name") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            modifier = Modifier.fillMaxWidth().testTag("partner_contact_name_input"),
            singleLine = true
          )

          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = partnerEmail,
            onValueChange = { partnerEmail = it; partnerError = null },
            label = { Text("Business Email") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            modifier = Modifier.fillMaxWidth().testTag("partner_email_input"),
            singleLine = true
          )

          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = partnerPhone,
            onValueChange = { partnerPhone = it; partnerError = null },
            label = { Text("Business Phone") },
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
            modifier = Modifier.fillMaxWidth().testTag("partner_phone_input"),
            singleLine = true
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Business Type Dropdown
          ExposedDropdownMenuBox(
            expanded = partnerTypeExpanded,
            onExpandedChange = { partnerTypeExpanded = !partnerTypeExpanded }
          ) {
            OutlinedTextField(
              value = partnerBusinessType,
              onValueChange = {},
              readOnly = true,
              label = { Text("Business Type") },
              trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = partnerTypeExpanded) },
              modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
                .testTag("partner_type_select")
            )
            ExposedDropdownMenu(
              expanded = partnerTypeExpanded,
              onDismissRequest = { partnerTypeExpanded = false }
            ) {
              listOf("Restaurant", "Bakery", "Grocery / Food Store", "Supermarket", "Community Kitchen").forEach { type ->
                DropdownMenuItem(
                  text = { Text(type) },
                  onClick = { partnerBusinessType = type; partnerTypeExpanded = false }
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = partnerAddress,
            onValueChange = { partnerAddress = it; partnerError = null },
            label = { Text("Physical Store / Pickup Address") },
            modifier = Modifier.fillMaxWidth().testTag("partner_address_input"),
            singleLine = true
          )

          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = partnerDonationDetails,
            onValueChange = { partnerDonationDetails = it; partnerError = null },
            label = { Text("Available Food Donation Details") },
            placeholder = { Text("e.g., daily excess bread, dinner buffet portions, produce") },
            modifier = Modifier.fillMaxWidth().testTag("partner_donation_details_input"),
            minLines = 2
          )

          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = partnerPassword,
            onValueChange = { partnerPassword = it; partnerError = null },
            label = { Text("Account Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth().testTag("partner_password_input"),
            singleLine = true
          )

          if (partnerError != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = partnerError ?: "",
              color = MaterialTheme.colorScheme.error,
              fontSize = 12.sp
            )
          }

          Spacer(modifier = Modifier.height(16.dp))

          Button(
            onClick = {
              viewModel.registerFoodPartner(
                businessName = partnerBusinessName,
                contactName = partnerContactName,
                email = partnerEmail,
                pass = partnerPassword,
                phone = partnerPhone,
                businessType = partnerBusinessType,
                address = partnerAddress,
                donationDetails = partnerDonationDetails,
                onSuccess = { onNavigate(Screen.Dashboard) },
                onError = { partnerError = it }
              )
            },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("partner_submit_button"),
            colors = ButtonDefaults.buttonColors(containerColor = FoodShareGreenPrimary)
          ) {
            Text("Register Partner (Pending Approval)")
          }
        }
      }
    }
  }
}
