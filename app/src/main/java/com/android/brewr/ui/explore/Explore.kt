package com.android.brewr.ui.explore

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import com.android.brewr.model.coffee.CoffeesViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(coffeesViewModel: CoffeesViewModel) {
  val sheetState = rememberModalBottomSheetState()
  val coroutineScope = rememberCoroutineScope()
  var showBottomSheet by remember { mutableStateOf(false) }

  // Collect coffees list
  val coffees = coffeesViewModel.coffees.collectAsState().value

  // State for controlling the current view in the bottom sheet
  var showCoffeeInfos by remember { mutableStateOf(false) }

  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    // Map Screen
    MapScreen(coffees)

    // Modal Bottom Sheet
    if (showBottomSheet) {
      ModalBottomSheet(onDismissRequest = { showBottomSheet = false }, sheetState = sheetState) {
        if (!showCoffeeInfos) {
          LazyColumn(
              modifier = Modifier.fillMaxWidth().fillMaxHeight(0.9f).testTag("bottomSheet")) {
                items(coffees) { coffee ->
                  CoffeeInformationCardScreen(
                      coffee = coffee,
                      onClick = {
                        coffeesViewModel.selectCoffee(coffee)
                        showCoffeeInfos = true
                      })
                }
              }
        } else {
          // Coffee Information Screen
          CoffeeInformationScreen(
              coffeesViewModel = coffeesViewModel, onBack = { showCoffeeInfos = false })
        }
      }
    }

    // Floating Menu Button
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

    // Automatically show the bottom sheet when the screen loads
    LaunchedEffect(Unit) { coroutineScope.launch { sheetState.show() } }
  }
}
