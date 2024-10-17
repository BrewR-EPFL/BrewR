package com.android.brewr.ui.overview

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.android.brewr.model.journey.ListJourneysViewModel
import com.android.brewr.ui.navigation.NavigationActions
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JourneyRecordScreen(
    listJourneysViewModel: ListJourneysViewModel =
        viewModel(factory = ListJourneysViewModel.Factory),
    navigationActions: NavigationActions
) {

  val journey = listJourneysViewModel.selectedJourney.collectAsState().value

  // Display the selected journey details
  Scaffold(
      topBar = {
        TopAppBar(
            title = { Text("Journey Record") },
            navigationIcon = {
              IconButton(onClick = { navigationActions.goBack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back")
              }
            },
            modifier = Modifier.testTag("journeyRecordScreen"))
      },
      content = { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp).padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
              // Display the journey details

              // Coffee Shop Name
              Text(
                  text = "Coffee Shop: ${journey?.coffeeShopName}",
                  style = MaterialTheme.typography.bodyLarge,
                  modifier = Modifier.testTag("coffeeShopName"))

              // Image placeholder
              Box(modifier = Modifier.fillMaxWidth().height(200.dp).border(1.dp, Color.Gray)) {
                if (journey?.imageUrl?.isNotEmpty() == true) {
                  Image(
                      painter =
                          rememberAsyncImagePainter(
                              ImageRequest.Builder(LocalContext.current)
                                  .data(journey.imageUrl) // Load the image from the URL
                                  .apply { crossfade(true) }
                                  .build()),
                      contentDescription = "Uploaded Image",
                      modifier = Modifier.fillMaxWidth().height(200.dp).align(Alignment.Center))
                } else {
                  Text(
                      "No photo added.",
                      modifier = Modifier.align(Alignment.Center),
                      color = Color.Gray)
                }
              }

              // Description
              Text(
                  text = "Description: ${journey?.description}",
                  style = MaterialTheme.typography.bodyLarge,
                  modifier = Modifier.testTag("description"))

              // Coffee Origin
              Text(
                  text = "Origin: ${journey?.coffeeOrigin?.name}",
                  style = MaterialTheme.typography.bodyLarge,
                  modifier = Modifier.testTag("origin"))

              // Brewing Method
              Text(
                  text = "Brewing Method: ${journey?.brewingMethod?.name}",
                  style = MaterialTheme.typography.bodyLarge,
                  modifier = Modifier.testTag("brewingMethod"))

              // Taste
              Text(
                  text = "Taste: ${journey?.coffeeTaste?.name}",
                  style = MaterialTheme.typography.bodyLarge,
                  modifier = Modifier.testTag("taste"))

              // Coffee Rating
              Text(
                  text = "Rating: ${journey?.coffeeRate?.name}",
                  style = MaterialTheme.typography.bodyLarge,
                  modifier = Modifier.testTag("rating"))

              // Date
              // Format the date
              val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
              val formattedDate =
                  journey?.date?.toDate()?.let { dateFormat.format(it) } ?: "Unknown Date"

              Text(
                  text = "Date: $formattedDate",
                  style = MaterialTheme.typography.bodyLarge,
                  modifier = Modifier.testTag("date"))

              // Location
              if (journey?.location?.isNotEmpty() == true) {
                Text(
                    text = "Location: ${journey.location}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.testTag("location"))
              }
            }
      })
}
