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
import com.android.brewr.ui.theme.CoffeeBrown
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(coffeesViewModel: CoffeesViewModel, curatedCoffees: List<Coffee>) {
  val sheetState = rememberModalBottomSheetState()
  val coroutineScope = rememberCoroutineScope()
  var showBottomSheet by remember { mutableStateOf(false) }
  var showCuratedList by remember { mutableStateOf(false) }

  // Collect coffees list
  val coffees = coffeesViewModel.coffees.collectAsState().value

  // State for controlling the current view in the bottom sheet
  var showCoffeeInfos by remember { mutableStateOf(false) }

  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    MapScreen(coffees)

    if (showBottomSheet) {
      ModalBottomSheet(
          onDismissRequest = { showBottomSheet = false },
          sheetState = sheetState,
          modifier = Modifier.testTag("exploreBottomSheet")) {
            if (!showCoffeeInfos) {
              Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier =
                        Modifier.fillMaxWidth().padding(16.dp).testTag("exploreListToggleRow"),
                    horizontalArrangement = Arrangement.SpaceBetween) {
                      Text(
                          text = if (showCuratedList) "Curated List" else "Nearby Coffee Shops",
                          style = MaterialTheme.typography.titleMedium,
                          modifier =
                              Modifier.align(Alignment.CenterVertically).testTag("listTitle"))

                      Button(
                          onClick = { showCuratedList = !showCuratedList },
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
              LazyColumn(modifier = Modifier.fillMaxHeight(0.9f).testTag("bottomSheet")) {
                items(if (showCuratedList) curatedCoffees else coffees) { coffee ->
                  CoffeeInformationCardScreen(
                      coffee = coffee,
                      onClick = {
                        coffeesViewModel.selectCoffee(coffee)

                        showCoffeeInfos = true
                      })
                }
              }
            } else {
              CoffeeInformationScreen(
                  coffeesViewModel = coffeesViewModel, onBack = { showCoffeeInfos = false })
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
