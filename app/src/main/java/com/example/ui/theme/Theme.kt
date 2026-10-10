package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
  primary = FoodShareGreenPrimary,
  onPrimary = Color.White,
  primaryContainer = FoodShareGreenContainer,
  onPrimaryContainer = FoodShareOnGreenContainer,
  secondary = FoodShareSecondary,
  onSecondary = Color.White,
  secondaryContainer = FoodShareSecondaryContainer,
  onSecondaryContainer = FoodShareGreenPrimary,
  tertiary = FoodShareAccentOrange,
  onTertiary = Color.White,
  tertiaryContainer = FoodShareAccentOrangeContainer,
  onTertiaryContainer = Color(0xFF4E1600),
  background = FoodShareBackground,
  onBackground = FoodShareTextPrimary,
  surface = FoodShareSurface,
  onSurface = FoodShareTextPrimary,
  surfaceVariant = FoodShareSurfaceVariant,
  onSurfaceVariant = FoodShareTextSecondary,
  outline = FoodShareOutline,
  error = FoodShareError,
  onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
  primary = Color(0xFF81C784),
  onPrimary = Color(0xFF00390B),
  primaryContainer = Color(0xFF1B5E20),
  onPrimaryContainer = Color(0xFFA5D6A7),
  secondary = Color(0xFFA5D6A7),
  onSecondary = Color(0xFF0D3812),
  background = Color(0xFF111711),
  onBackground = Color(0xFFE1E6E1),
  surface = Color(0xFF192019),
  onSurface = Color(0xFFE1E6E1),
  surfaceVariant = Color(0xFF232C23),
  onSurfaceVariant = Color(0xFFBAC5BA),
  outline = Color(0xFF4C5B4C)
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Keep consistent FoodShare green branding
  content: @Composable () -> Unit
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
