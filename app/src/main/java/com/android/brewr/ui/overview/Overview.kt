package com.android.brewr.ui.overview

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun OverviewScreen() {
  Scaffold(
      modifier = Modifier.testTag("overviewScreen"),
      topBar = {
        TopAppBar(
            title = { Text(text = "BrewR") },
            actions = {
              Row {
                IconButton(onClick = {}) {
                  Icon(imageVector = Icons.Outlined.Add, contentDescription = "Add")
                }
                Spacer(modifier = Modifier.width(16.dp))
                IconButton(onClick = {}) {
                  Icon(imageVector = Icons.Default.AccountCircle, contentDescription = "Add")
                }
              }
            })
      },
      content = { pd ->
        pd
      })
}

// TODO: Refactor this to use a list of Journey ViewModels
@Composable
fun JourneyItem(journey: String, onClick: () -> Unit) {
  Card(
      modifier =
          Modifier.testTag("todoListItem")
              .fillMaxWidth()
              .padding(vertical = 4.dp)
              .clickable(onClick = onClick),
  ) {
    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
      // Date and Grade Row
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = "Test", style = MaterialTheme.typography.bodySmall)

        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(text = "Test", style = MaterialTheme.typography.bodySmall, color = Color.Blue)
          Icon(
              imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
        }
      }

      Spacer(modifier = Modifier.height(4.dp))

      // Drink Name
      Text(text = "Test", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)

      // Coffee Shop Name
      Text(text = "Test", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
    }
  }
}
