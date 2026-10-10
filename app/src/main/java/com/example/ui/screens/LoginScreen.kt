package com.example.ui.screens

import android.app.Activity
import android.content.Context
import android.util.Log
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.example.R
import com.example.ui.FoodShareViewModel
import com.example.ui.theme.FoodShareGreenContainer
import com.example.ui.theme.FoodShareGreenPrimary
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
  viewModel: FoodShareViewModel,
  onAuthSuccess: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  val credentialManager = remember { CredentialManager.create(context) }

  var selectedTab by remember { mutableIntStateOf(0) } // 0: Sign In, 1: Register
  var selectedRole by remember { mutableStateOf("DONOR") } // DONOR, VOLUNTEER, RECEIVER
  var isGoogleLoading by remember { mutableStateOf(false) }

  // Email / Password state
  var email by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var passwordVisible by remember { mutableStateOf(false) }
  var fullName by remember { mutableStateOf("") }
  var phone by remember { mutableStateOf("") }
  var errorMessage by remember { mutableStateOf<String?>(null) }
  var roleExpanded by remember { mutableStateOf(false) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFFF7FBF7))
      .verticalScroll(rememberScrollState())
      .padding(20.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Spacer(modifier = Modifier.height(16.dp))

    Text(
      text = "FoodShare Secure Auth",
      style = MaterialTheme.typography.headlineMedium,
      fontWeight = FontWeight.Bold,
      color = FoodShareGreenPrimary
    )
    Text(
      text = "Sign in to donate food, deliver as a volunteer, or receive for your NGO",
      fontSize = 13.sp,
      color = Color(0xFF666666),
      modifier = Modifier.padding(top = 4.dp)
    )

    Spacer(modifier = Modifier.height(20.dp))

    // Select Role Chips
    Text(
      text = "Select your community role:",
      fontSize = 13.sp,
      fontWeight = FontWeight.SemiBold,
      color = Color(0xFF444444),
      modifier = Modifier.align(Alignment.Start)
    )
    Spacer(modifier = Modifier.height(8.dp))
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      listOf("DONOR" to "Food Donor", "VOLUNTEER" to "Volunteer", "RECEIVER" to "NGO / Receiver").forEach { (roleKey, label) ->
        FilterChip(
          selected = selectedRole == roleKey,
          onClick = { selectedRole = roleKey },
          label = { Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium) },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = FoodShareGreenContainer,
            selectedLabelColor = FoodShareGreenPrimary
          ),
          modifier = Modifier.testTag("auth_role_chip_$roleKey")
        )
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Google Sign-In Card
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
      Column(
        modifier = Modifier.padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = "Fast & Secure Sign-In",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = FoodShareGreenPrimary
        )
        Text(
          text = "Sign in with your Google account via Credential Manager",
          fontSize = 12.sp,
          color = Color(0xFF666666)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
          onClick = {
            isGoogleLoading = true
            errorMessage = null
            executeGoogleSignIn(
              context = context,
              credentialManager = credentialManager,
              scope = coroutineScope,
              onSuccess = { email, name ->
                isGoogleLoading = false
                viewModel.handleFirebaseGoogleSignIn(
                  email = email,
                  displayName = name,
                  selectedRole = selectedRole,
                  onSuccess = onAuthSuccess,
                  onError = { err -> errorMessage = err }
                )
              },
              onError = { err ->
                isGoogleLoading = false
                errorMessage = err
              },
              onCancel = {
                isGoogleLoading = false
              }
            )
          },
          enabled = !isGoogleLoading,
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("google_signin_button"),
          colors = ButtonDefaults.buttonColors(containerColor = FoodShareGreenPrimary)
        ) {
          if (isGoogleLoading) {
            CircularProgressIndicator(
              modifier = Modifier.size(22.dp),
              color = Color.White,
              strokeWidth = 2.dp
            )
          } else {
            Text("Sign in with Google", fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE0E0E0))
      Text(
        text = "  OR USE EMAIL & PASSWORD  ",
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF888888)
      )
      HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE0E0E0))
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Email/Password Form Card
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
      Column(modifier = Modifier.padding(20.dp)) {
        TabRow(
          selectedTabIndex = selectedTab,
          containerColor = Color.Transparent,
          contentColor = FoodShareGreenPrimary
        ) {
          Tab(
            selected = selectedTab == 0,
            onClick = { selectedTab = 0; errorMessage = null },
            text = { Text("Sign In", fontWeight = FontWeight.Bold) },
            modifier = Modifier.testTag("tab_login_mode")
          )
          Tab(
            selected = selectedTab == 1,
            onClick = { selectedTab = 1; errorMessage = null },
            text = { Text("Register", fontWeight = FontWeight.Bold) },
            modifier = Modifier.testTag("tab_register_mode")
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedTab == 1) {
          OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it; errorMessage = null },
            label = { Text("Full Name") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            modifier = Modifier.fillMaxWidth().testTag("auth_name_field"),
            singleLine = true
          )
          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = phone,
            onValueChange = { phone = it; errorMessage = null },
            label = { Text("Phone Number") },
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
            modifier = Modifier.fillMaxWidth().testTag("auth_phone_field"),
            singleLine = true
          )
          Spacer(modifier = Modifier.height(10.dp))
        }

        OutlinedTextField(
          value = email,
          onValueChange = { email = it; errorMessage = null },
          label = { Text("Email Address") },
          leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
          modifier = Modifier.fillMaxWidth().testTag("auth_email_field"),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = password,
          onValueChange = { password = it; errorMessage = null },
          label = { Text("Password") },
          leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
          trailingIcon = {
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
              Icon(
                imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                contentDescription = null
              )
            }
          },
          visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
          modifier = Modifier.fillMaxWidth().testTag("auth_password_field"),
          singleLine = true
        )

        if (errorMessage != null) {
          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = errorMessage ?: "",
            color = MaterialTheme.colorScheme.error,
            fontSize = 12.sp
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
          onClick = {
            if (selectedTab == 0) {
              viewModel.login(
                email = email,
                pass = password,
                onSuccess = onAuthSuccess,
                onError = { errorMessage = it }
              )
            } else {
              viewModel.register(
                email = email,
                pass = password,
                fullName = fullName,
                phone = phone,
                role = selectedRole,
                onSuccess = onAuthSuccess,
                onError = { errorMessage = it }
              )
            }
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("auth_submit_button"),
          colors = ButtonDefaults.buttonColors(containerColor = FoodShareGreenPrimary)
        ) {
          Text(if (selectedTab == 0) "Sign In" else "Create Account", fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

/**
 * Executes Google Sign-In with Jetpack CredentialManager using GetSignInWithGoogleOption
 */
fun executeGoogleSignIn(
  context: Context,
  credentialManager: CredentialManager,
  scope: CoroutineScope,
  onSuccess: (email: String, displayName: String) -> Unit,
  onError: (String) -> Unit,
  onCancel: () -> Unit
) {
  val clientId = try {
    context.getString(R.string.default_web_client_id)
  } catch (e: Exception) {
    onError("Google Sign-In configuration missing: default_web_client_id not found")
    return
  }

  val signInOption = GetSignInWithGoogleOption.Builder(serverClientId = clientId).build()
  val request = GetCredentialRequest.Builder().addCredentialOption(signInOption).build()

  scope.launch {
    try {
      val result = credentialManager.getCredential(context as Activity, request)
      val credential = result.credential
      if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
        val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
        val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
        val authResult = FirebaseAuth.getInstance().signInWithCredential(authCredential).await()
        val user = authResult.user
        val email = user?.email ?: "user@google.com"
        val name = user?.displayName ?: "Google User"
        onSuccess(email, name)
      } else {
        onError("Unexpected credential type returned")
      }
    } catch (e: GetCredentialCancellationException) {
      Log.w("Auth", "Google Sign-In flow cancelled: ${e.message}", e)
      onCancel()
    } catch (e: Exception) {
      Log.e("Auth", "Google Sign-In error", e)
      onError(e.localizedMessage ?: "Sign in failed")
    }
  }
}
