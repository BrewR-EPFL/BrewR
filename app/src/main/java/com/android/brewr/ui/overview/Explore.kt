package com.android.brewr.ui.overview

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.brewr.model.coffee.Coffee
import com.android.brewr.model.coffee.Review
import com.android.brewr.model.location.Location
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(coffees: List<Coffee>) {
  val sheetState = rememberModalBottomSheetState()
  val coroutineScope = rememberCoroutineScope()
  var showBottomSheet by remember { mutableStateOf(false) }

  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    MapScreen(coffees.map { it.location })

    if (showBottomSheet) {
      ModalBottomSheet(onDismissRequest = { showBottomSheet = false }, sheetState = sheetState) {
        Column(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.9f)) {
          CoffeeInformationScreen(coffees[0])
        }
      }
    }

    IconButton(
        onClick = { showBottomSheet = true },
        modifier = Modifier.align(Alignment.BottomStart).padding(20.dp).testTag("menuButton")) {
          Box(
              modifier =
                  Modifier.size(56.dp)
                      .clip(CircleShape)
                      .border(1.dp, Color.Black, CircleShape)
                      .padding(8.dp)) {
                Icon(
                    imageVector = Icons.Rounded.Menu,
                    contentDescription = "Menu",
                    tint = Color.Black,
                    modifier = Modifier.size(36.dp))
              }
        }

    LaunchedEffect(Unit) { coroutineScope.launch { sheetState.show() } }
  }
}

@Preview(showBackground = true)
@Composable
fun ExploreScreenPreview() {
  val coffees =
      listOf(
          Coffee(
              "",
              coffeeShopName = "Café tranquille 1",
              Location(
                  latitude = 48.87847905807652,
                  longitude = 2.3562626423266946,
                  address = "147 Rue du Faubourg Saint-Denis, 75010 Paris, France"),
              rating = 4.9,
              hours = com.android.brewr.model.coffee.Hours(open = "8:00 AM", close = "5:00 PM"),
              reviews =
                  listOf(Review("Pablo", "Best coffee in the 10th arrondissement of Paris", 5.0)),
              imagesUrls =
                  listOf(
                      "https://firebasestorage.googleapis.com/v0/b/brewr-epfl.appspot.com/o/images%2F2023-09-29.jpg?alt=media&token=eaaa9dbf-f402-4d12-b5ac-7c5589231a35")),
          Coffee(
              "",
              coffeeShopName = "Café tranquille 2",
              Location(
                  latitude = 48.87847905807652,
                  longitude = 2.3562626423266946,
                  address = "147 Rue du Faubourg Saint-Denis, 75010 Paris, France"),
              rating = 4.9,
              hours = com.android.brewr.model.coffee.Hours(open = "8:00 AM", close = "5:00 PM"),
              reviews =
                  listOf(Review("Pablo", "Best coffee in the 10th arrondissement of Paris", 5.0)),
              imagesUrls =
                  listOf(
                      "https://firebasestorage.googleapis.com/v0/b/brewr-epfl.appspot.com/o/images%2F2023-09-29.jpg?alt=media&token=eaaa9dbf-f402-4d12-b5ac-7c5589231a35")))
  ExploreScreen(coffees)
}
