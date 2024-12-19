package com.android.brewr.ui.overview

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
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

/**
 * Displays a gallery of journey items in a staggered grid layout.
 *
 * This composable fetches a list of journeys from the [ListJourneysViewModel] and displays them in
 * a grid. If no journeys are available, a placeholder text is shown.
 *
 * @param listJourneysViewModel The ViewModel containing journey data.
 * @param padding Padding values for the screen layout.
 * @param navigationActions Navigation actions for screen transitions.
 */
@Composable
fun GalleryScreen(
    listJourneysViewModel: ListJourneysViewModel =
        viewModel(factory = ListJourneysViewModel.Factory),
    padding: PaddingValues,
    navigationActions: NavigationActions
) {
  val journeys = listJourneysViewModel.journeys.collectAsState().value

  if (journeys.isNotEmpty()) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        modifier = Modifier.fillMaxSize().padding(padding),
        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          items(journeys) { journey ->
            Log.e("GalleryScreen", "journey: $journey")
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

/**
 * Represents a journey item in the gallery.
 *
 * This composable displays a card with an image representing the journey. Clicking on the card
 * triggers the [onClick] callback.
 *
 * @param journey The [Journey] object containing journey details.
 * @param onClick The callback function to execute when the card is clicked.
 */
@Composable
fun JourneyItem(journey: Journey, onClick: () -> Unit) {
  Card(
      modifier =
          Modifier.testTag("journeyListItem")
              .fillMaxWidth()
              .padding(vertical = 4.dp)
              .clickable(onClick = onClick),
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
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
          contentScale = ContentScale.Crop,
          modifier =
              Modifier.testTag("journeyImage").fillMaxWidth().heightIn(min = 180.dp, max = 300.dp))
    }
  }
}
