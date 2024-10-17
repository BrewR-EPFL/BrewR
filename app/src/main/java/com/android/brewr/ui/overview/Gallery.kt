package com.android.brewr.ui.overview

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.android.brewr.model.journey.Journey
import com.android.brewr.model.journey.ListJourneysViewModel
import com.android.brewr.ui.navigation.NavigationActions
import com.android.brewr.ui.navigation.Screen

@Composable
fun GalleryScreen(
    listJourneysViewModel: ListJourneysViewModel =
        viewModel(factory = ListJourneysViewModel.Factory),
    padding: PaddingValues,
    navigationActions: NavigationActions

) {
  val journeys = listJourneysViewModel.journeys.collectAsState().value
  val context = LocalContext.current

  if (journeys.isNotEmpty()) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize().padding(padding),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      items(journeys) { journey ->
        JourneyItem(journey = journey) {
            listJourneysViewModel.selectJourney(journey)
            navigationActions.navigateTo(Screen.JOURNEY_RECORD)
        }
      }
    }
  } else {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Text(modifier = Modifier.testTag("emptyJourneyPrompt"), text = "You have no Journey yet.")
    }
  }
}

@Composable
fun JourneyItem(journey: Journey, onClick: () -> Unit) {
  Card(
      modifier =
          Modifier.testTag("journeyListItem")
              .fillMaxWidth()
              .padding(vertical = 4.dp)
              .clickable(onClick = onClick),
  ) {
    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
      Image(
          painter =
              rememberAsyncImagePainter(
                  ImageRequest.Builder(LocalContext.current)
                      .data(journey.imageUrl)
                      .apply(
                          block =
                              fun ImageRequest.Builder.() {
                                crossfade(true)
                              })
                      .build()),
          contentDescription = "Selected Image",
          modifier = Modifier.testTag("journeyImage").size(120.dp))
    }
  }
}
