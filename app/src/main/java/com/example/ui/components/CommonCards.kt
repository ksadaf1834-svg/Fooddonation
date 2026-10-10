package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.DonationEntity
import com.example.data.local.entity.FoodRequestEntity
import com.example.data.local.entity.VolunteerTaskEntity
import com.example.ui.theme.FoodShareAccentOrange
import com.example.ui.theme.FoodShareGreenPrimary

@Composable
fun DonationCard(
  donation: DonationEntity,
  onRequestClick: (() -> Unit)? = null,
  onDeleteClick: (() -> Unit)? = null,
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
        Text(
          text = donation.foodTitle,
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = FoodShareGreenPrimary
        )
        StatusBadge(status = donation.status)
      }

      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = donation.description,
        style = MaterialTheme.typography.bodyMedium,
        color = Color(0xFF424242)
      )

      Spacer(modifier = Modifier.height(10.dp))
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.Fastfood,
          contentDescription = null,
          tint = FoodShareAccentOrange,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "${donation.quantity} • ${donation.foodType}",
          fontSize = 13.sp,
          fontWeight = FontWeight.Medium,
          color = Color(0xFF333333)
        )
      }

      Spacer(modifier = Modifier.height(6.dp))
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.LocationOn,
          contentDescription = null,
          tint = FoodShareGreenPrimary,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = donation.pickupAddress,
          fontSize = 13.sp,
          color = Color(0xFF555555)
        )
      }

      Spacer(modifier = Modifier.height(6.dp))
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.AccessTime,
          contentDescription = null,
          tint = Color(0xFF757575),
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Deadline: ${donation.deadlineTime}",
          fontSize = 12.sp,
          color = Color(0xFF666666)
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
          text = "By ${donation.donorName}",
          fontSize = 12.sp,
          fontWeight = FontWeight.Medium,
          color = FoodShareGreenPrimary
        )
      }

      if (onRequestClick != null && donation.status == "AVAILABLE") {
        Spacer(modifier = Modifier.height(12.dp))
        Button(
          onClick = onRequestClick,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("request_food_button_${donation.id}"),
          colors = ButtonDefaults.buttonColors(containerColor = FoodShareGreenPrimary)
        ) {
          Text("Request This Food Donation")
        }
      }

      if (onDeleteClick != null) {
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(
          onClick = onDeleteClick,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("delete_donation_button_${donation.id}")
        ) {
          Text("Remove Donation Listing", color = MaterialTheme.colorScheme.error)
        }
      }
    }
  }
}

@Composable
fun FoodRequestCard(
  request: FoodRequestEntity,
  onAcceptClick: (() -> Unit)? = null,
  onRejectClick: (() -> Unit)? = null,
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
        Text(
          text = "Request for ${request.foodTitle}",
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Bold,
          color = FoodShareGreenPrimary
        )
        StatusBadge(status = request.status)
      }

      Spacer(modifier = Modifier.height(8.dp))
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.Business,
          contentDescription = null,
          tint = FoodShareGreenPrimary,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "${request.organizationName.ifBlank { request.receiverName }} (${request.quantityRequested})",
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold
        )
      }

      Spacer(modifier = Modifier.height(4.dp))
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.LocationOn,
          contentDescription = null,
          tint = Color(0xFF666666),
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Deliver to: ${request.deliveryAddress}",
          fontSize = 12.sp,
          color = Color(0xFF555555)
        )
      }

      if (request.notes.isNotBlank()) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Notes: ${request.notes}",
          fontSize = 12.sp,
          color = Color(0xFF757575)
        )
      }

      if (onAcceptClick != null && onRejectClick != null && request.status == "PENDING") {
        Spacer(modifier = Modifier.height(12.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedButton(
            onClick = onRejectClick,
            modifier = Modifier
              .weight(1f)
              .testTag("reject_request_button_${request.id}")
          ) {
            Text("Reject")
          }
          Button(
            onClick = onAcceptClick,
            modifier = Modifier
              .weight(1f)
              .testTag("accept_request_button_${request.id}"),
            colors = ButtonDefaults.buttonColors(containerColor = FoodShareGreenPrimary)
          ) {
            Text("Accept & Schedule")
          }
        }
      }
    }
  }
}

@Composable
fun VolunteerTaskCard(
  task: VolunteerTaskEntity,
  currentVolunteerId: String?,
  onAcceptTask: (() -> Unit)? = null,
  onStartDelivery: (() -> Unit)? = null,
  onCompleteTask: (() -> Unit)? = null,
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
        Column {
          Text(
            text = task.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = FoodShareGreenPrimary
          )
          Text(
            text = "Type: ${task.taskType}",
            fontSize = 12.sp,
            color = FoodShareAccentOrange,
            fontWeight = FontWeight.SemiBold
          )
        }
        StatusBadge(status = task.status)
      }

      Spacer(modifier = Modifier.height(10.dp))
      Text(
        text = "📦 Food: ${task.foodDetails}",
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium
      )

      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = "📍 Pickup: ${task.pickupLocation}",
        fontSize = 12.sp,
        color = Color(0xFF424242)
      )

      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "🎯 Delivery: ${task.deliveryLocation}",
        fontSize = 12.sp,
        color = Color(0xFF424242)
      )

      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "⏰ Required Time: ${task.requiredTime}",
        fontSize = 12.sp,
        color = Color(0xFF666666)
      )

      if (!task.assignedVolunteerName.isNullOrBlank()) {
        Spacer(modifier = Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            tint = FoodShareGreenPrimary,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Assigned Volunteer: ${task.assignedVolunteerName}",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = FoodShareGreenPrimary
          )
        }
      }

      // Actions based on state
      val isAssignedToMe = task.assignedVolunteerId == currentVolunteerId

      if (task.status == "Available" && onAcceptTask != null) {
        Spacer(modifier = Modifier.height(12.dp))
        Button(
          onClick = onAcceptTask,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("accept_task_button_${task.id}"),
          colors = ButtonDefaults.buttonColors(containerColor = FoodShareGreenPrimary)
        ) {
          Text("Accept This Task")
        }
      } else if (isAssignedToMe && task.status == "Accepted" && onStartDelivery != null) {
        Spacer(modifier = Modifier.height(12.dp))
        Button(
          onClick = onStartDelivery,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("start_delivery_button_${task.id}"),
          colors = ButtonDefaults.buttonColors(containerColor = FoodShareAccentOrange)
        ) {
          Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Start Delivery (In Progress)")
        }
      } else if (isAssignedToMe && task.status == "In Progress" && onCompleteTask != null) {
        Spacer(modifier = Modifier.height(12.dp))
        Button(
          onClick = onCompleteTask,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("complete_task_button_${task.id}"),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
        ) {
          Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Mark Food Delivery Completed")
        }
      }
    }
  }
}
