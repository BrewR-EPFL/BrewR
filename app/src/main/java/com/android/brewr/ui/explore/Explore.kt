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
import com.android.brewr.model.coffee.filterOpenCoffeeShops
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
fun ExploreScreen(coffeesViewModel: CoffeesViewModel, curatedCoffees: List<Coffee>) {
  val sheetState = rememberModalBottomSheetState()
  val coroutineScope = rememberCoroutineScope()
  var showBottomSheet by remember { mutableStateOf(false) }
  var selectedOption by remember { mutableStateOf("Nearby") } // Default to "Nearby"

  // Collect coffees list
  val coffees = coffeesViewModel.coffees.collectAsState().value
  val filteredOpenedCoffees = filterOpenCoffeeShops(coffees) // Apply filtering for "Opened"

  // State for controlling the current view in the bottom sheet
  var showCoffeeInfos by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        MapScreen(coffees)

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                modifier = Modifier.testTag("exploreBottomSheet")
            ) {
                if (!showCoffeeInfos) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        ListToggleRow(selectedOption = selectedOption) { option ->
                            selectedOption = option
                        }

                        val listToShow = when (selectedOption) {
                            "Curated" -> curatedCoffees
                            "Opened" -> filteredOpenedCoffees
                            else -> coffees
                        }

                        CoffeeList(listToShow) {
                            coffeesViewModel.selectCoffee(it)
                            showCoffeeInfos = true
                        }
                    }
                } else {
                    CoffeeInformationScreen(
                        coffeesViewModel = coffeesViewModel,
                        onBack = { showCoffeeInfos = false }
                    )
                }
            }
        }

        ShowMenuButton { showBottomSheet = true }

        LaunchedEffect(Unit) { coroutineScope.launch { sheetState.show() } }
    }
}

/**
 * A composable function that displays a row with a dropdown menu to allow users to toggle between
 * different list views such as "Nearby", "Curated", and "Opened".
 *
 * @param selectedOption A [String] representing the currently selected option (e.g., "Nearby", "Curated", "Opened").
 * @param onOptionSelected A callback function that is invoked when the user selects an option from the dropdown menu.
 *        The selected option is passed as a parameter to this function.
 */
@Composable
fun ListToggleRow(selectedOption: String, onOptionSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .testTag("exploreListToggleRow"),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = selectedOption,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .testTag("listTitle")
        )

        Box {
            Button(
                onClick = { expanded = true },
                colors = ButtonDefaults.buttonColors(containerColor = CoffeeBrown),
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier.testTag("toggleDropdownMenu")
            ) {
                Text(
                    text = "Choose View",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.testTag("dropdownMenu")
            ) {
                listOf("Nearby", "Curated", "Opened").forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        }
                    )
                }
            }
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
    if (coffees.isEmpty()) {
        // Display a message when there are no coffee shops in the list
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Everywhere is closed! I guess Coffee will disturb your sleep at this time ",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Gray,
                modifier = Modifier.testTag("noOpenCoffeeShopsMessage")
            )
        }
    } else {
        // Display the list of coffee shops
        LazyColumn(modifier = Modifier.fillMaxHeight(0.9f).testTag("bottomSheet")) {
            items(coffees) { coffee ->
                CoffeeInformationCardScreen(coffee = coffee, onClick = { onCoffeeClick(coffee) })
            }
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
