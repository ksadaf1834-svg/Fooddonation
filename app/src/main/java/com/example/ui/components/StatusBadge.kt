package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.FoodShareAccentOrange
import com.example.ui.theme.FoodShareGreenPrimary

@Composable
fun StatusBadge(
  status: String,
  modifier: Modifier = Modifier
) {
  val (bgColor, textColor) = when (status.uppercase()) {
    "AVAILABLE" -> Pair(Color(0xFFE8F5E9), FoodShareGreenPrimary)
    "ACCEPTED" -> Pair(Color(0xFFE3F2FD), Color(0xFF1565C0))
    "IN PROGRESS" -> Pair(Color(0xFFFFF3E0), FoodShareAccentOrange)
    "COMPLETED" -> Pair(Color(0xFFE8F5E9), Color(0xFF2E7D32))
    "PENDING" -> Pair(Color(0xFFFFF8E1), Color(0xFFF57F17))
    "REJECTED", "CANCELLED", "EXPIRED" -> Pair(Color(0xFFFFEBEE), Color(0xFFC62828))
    "APPROVED" -> Pair(Color(0xFFE8F5E9), FoodShareGreenPrimary)
    "HIGH" -> Pair(Color(0xFFFFEBEE), Color(0xFFC62828))
    "MEDIUM" -> Pair(Color(0xFFFFF3E0), FoodShareAccentOrange)
    "LOW" -> Pair(Color(0xFFE8F5E9), FoodShareGreenPrimary)
    else -> Pair(Color(0xFFECEFF1), Color(0xFF37474F))
  }

  Box(
    modifier = modifier
      .background(bgColor, RoundedCornerShape(8.dp))
      .padding(horizontal = 8.dp, vertical = 4.dp)
  ) {
    Text(
      text = status,
      color = textColor,
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      style = MaterialTheme.typography.labelSmall
    )
  }
}
