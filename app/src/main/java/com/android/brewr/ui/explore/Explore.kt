package com.android.brewr.ui.explore

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.brewr.model.coffee.Coffee
import com.android.brewr.model.coffee.CoffeesViewModel
import com.android.brewr.model.journey.ListJourneysViewModel
import com.android.brewr.ui.theme.CoffeeBrown
import kotlinx.coroutines.launch

/**
 * A composable function that displays the Explore screen.
 *
 * @param coffeesViewModel The ViewModel that provides the list of coffees.
 * @param curatedCoffees A list of curated Coffee objects to be displayed.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    coffeesViewModel: CoffeesViewModel,
    listJourneysViewModel: ListJourneysViewModel,
    curatedCoffees: List<Coffee>
) {
  val sheetState = rememberModalBottomSheetState()
  val coroutineScope = rememberCoroutineScope()
  var showBottomSheet by remember { mutableStateOf(false) }
  var showCuratedList by remember { mutableStateOf(false) }

  // Collect coffees list
  val coffees = coffeesViewModel.coffees.collectAsState().value

  // State for controlling the current view in the bottom sheet
  var showCoffeeInfos by remember { mutableStateOf(false) }

  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    MapScreen(coffees, listJourneysViewModel)

    if (showBottomSheet) {
      ModalBottomSheet(
          onDismissRequest = { showBottomSheet = false },
          sheetState = sheetState,
          modifier = Modifier.testTag("exploreBottomSheet")) {
            if (!showCoffeeInfos) {
              Column(modifier = Modifier.fillMaxWidth()) {
                ListToggleRow(showCuratedList) { showCuratedList = !showCuratedList }
                CoffeeList(if (showCuratedList) curatedCoffees else coffees) {
                  coffeesViewModel.selectCoffee(it)
                  showCoffeeInfos = true
                }
              }
            } else {
              CoffeeInformationScreen(
                  coffeesViewModel = coffeesViewModel, onBack = { showCoffeeInfos = false })
            }
          }
    }

    ShowMenuButton { showBottomSheet = true }

    LaunchedEffect(Unit) { coroutineScope.launch { sheetState.show() } }
  }
}

/**
 * A composable function that displays a row with a toggle button to switch between showing a
 * curated list and nearby coffee shops.
 *
 * @param showCuratedList A boolean indicating whether the curated list is currently shown.
 * @param onToggle A lambda function to be executed when the toggle button is clicked.
 */
@Composable
fun ListToggleRow(showCuratedList: Boolean, onToggle: () -> Unit) {
  Row(
      modifier = Modifier.fillMaxWidth().padding(16.dp).testTag("exploreListToggleRow"),
      horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            text = if (showCuratedList) "Curated List" else "Nearby Coffee Shops",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.CenterVertically).testTag("listTitle"))

        Button(
            onClick = onToggle,
            colors = ButtonDefaults.buttonColors(containerColor = CoffeeBrown),
            shape = RoundedCornerShape(15.dp),
            modifier =
                Modifier.padding(start = 8.dp)
                    .height(40.dp)
                    .wrapContentWidth()
                    .testTag("toggleListButton")) {
              Text(
                  text = if (showCuratedList) "Show Nearby" else "Show Curated",
                  style = MaterialTheme.typography.bodySmall,
                  color = Color.White)
            }
      }
}

/**
 * A composable function that displays a list of coffee items.
 *
 * @param coffees A list of Coffee objects to be displayed.
 * @param onCoffeeClick A lambda function to be executed when a coffee item is clicked.
 */
@Composable
fun CoffeeList(coffees: List<Coffee>, onCoffeeClick: (Coffee) -> Unit) {
  LazyColumn(modifier = Modifier.fillMaxHeight(0.9f).testTag("bottomSheet")) {
    items(coffees) { coffee ->
      CoffeeInformationCardScreen(coffee = coffee, onClick = { onCoffeeClick(coffee) })
    }
  }
}

/**
 * A composable function that displays a menu button.
 *
 * @param onClick A lambda function to be executed when the button is clicked.
 */
@Composable
fun ShowMenuButton(onClick: () -> Unit) {
  Box(Modifier.fillMaxSize()) {
    IconButton(
        onClick = onClick,
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
  }
}
