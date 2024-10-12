package com.android.brewr.ui.overview

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.android.brewr.model.journey.BrewingMethod
import com.android.brewr.model.journey.CoffeeOrigin
import com.android.brewr.model.journey.CoffeeRate
import com.android.brewr.model.journey.CoffeeTaste
import com.android.brewr.model.journey.ListJourneysViewModel
import com.android.brewr.ui.navigation.NavigationActions
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddJourneyScreen(
    listJourneysViewModel: ListJourneysViewModel =
        viewModel(factory = ListJourneysViewModel.Factory),
    navigationActions: NavigationActions
) {
  val currentUser = FirebaseAuth.getInstance().currentUser
  val uid = currentUser?.uid ?: ""
  var imageUrl by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }
  var coffeeShopName by remember { mutableStateOf("") }
  var coffeeOrigin by remember { mutableStateOf(CoffeeOrigin.BRAZIL) }
  var brewingMethod by remember { mutableStateOf(BrewingMethod.ESPRESSO_MACHINE) }
  var coffeeTaste by remember { mutableStateOf(CoffeeTaste.NUTTY) }
  var coffeeRate by remember { mutableStateOf(CoffeeRate.ONE) }
  var date by remember { mutableStateOf(Timestamp.now()) } // Using Firebase Timestamp for now
  var location by remember { mutableStateOf("") } // Will change to Location once it's implemented

  val context = LocalContext.current

  var expanded by remember { mutableStateOf(false) } // State for the dropdown menu
  var isYesSelected by remember { mutableStateOf(false) }
  val coffeeShopOptions = listOf("Home", "Starbucks", "Search")

  Scaffold(
      topBar = {
        TopAppBar(
            navigationIcon = {
              IconButton(onClick = { navigationActions.goBack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black)
              }
            },
            title = { /* No title, just a back button */})
      },
      content = { paddingValues ->
        Column(
            modifier =
                Modifier.fillMaxWidth()
                    .padding(paddingValues)
                    .padding(16.dp), // Add padding to the whole Column
            verticalArrangement = Arrangement.spacedBy(16.dp)) {
              Text(
                  text = "Your Journey",
                  style = MaterialTheme.typography.titleLarge,
                  color = Color.Black)

              // Row for the Add Photo and description box
              Row(
                  modifier = Modifier.fillMaxWidth().padding(16.dp), // Add padding to the whole Row
                  horizontalArrangement = Arrangement.spacedBy(16.dp) // Space between the elements
                  ) {
                    // Box on the left for "Add Photo"
                    Box(
                        modifier =
                            Modifier.size(150.dp) // Size for the box, can adjust as needed
                                .border(2.dp, Color.Black) // Black border around the box
                        ) {
                          Column(
                              horizontalAlignment = Alignment.CenterHorizontally,
                              modifier = Modifier.align(Alignment.Center)) {
                                Text(text = "Add Photo", color = Color.Black)
                                IconButton(
                                    onClick = {}, modifier = Modifier.testTag("addPhotoButton")) {
                                      Icon(
                                          imageVector = Icons.Rounded.Add,
                                          contentDescription = "Add Photo")
                                    }
                              }
                          /*
                           Button(
                               onClick = { /* Add Photo action */},
                               modifier =
                                   Modifier.align(Alignment.Center) // Center the button in the box
                               ) {
                                 Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                   Icon(
                                       imageVector = Icons.Default.Add,
                                       contentDescription = "Add Photo",
                                       tint = Color(0xFF181515) // Custom orange color
                                       )
                                   Text(text = "Add Photo", color = Color.Black)
                                 }
                               }

                          */
                        }

                    // Description section on the right

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        placeholder = { Text("Capture your coffee experience") },
                        modifier =
                            Modifier.fillMaxWidth()
                                .height(150.dp)
                                .testTag("inputJourneyDescription"))

                    /*
                     Column(
                         modifier =
                             Modifier.weight(1f) // Take the remaining space
                                 .border(
                                     1.dp, Color(0xFF854704)) // Orange border around the TextField
                                 .padding(8.dp) // Padding inside the TextField
                         ) {
                           Text(
                               text = "Description",
                               color = Color(0xFF854704),
                               style = MaterialTheme.typography.bodyMedium,
                               textAlign = TextAlign.Center)

                           Spacer(modifier = Modifier.height(8.dp))

                           TextField(
                               value = description,
                               onValueChange = { description = it },
                               placeholder = { Text("Capture your coffee experience") },
                               modifier = Modifier.fillMaxWidth())
                         }

                    */
                  }

              /*
               Text(
                   text = "Coffeeshop",
                   style = MaterialTheme.typography.titleLarge,
                   color = Color.Black)

              */

              // CoffeeShop Dropdown Menu below the row

              Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier =
                      Modifier.clickable {
                        isYesSelected = !isYesSelected
                        expanded = isYesSelected // Show text field when ticked
                      }) {
                    Icon(
                        imageVector =
                            if (isYesSelected) Icons.Outlined.Check else Icons.Outlined.Close,
                        contentDescription = if (isYesSelected) "Checked" else "Unchecked",
                        tint = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "At a coffee shop", color = Color.Black)
                  }

              if (expanded) {
                OutlinedTextField(
                    value = coffeeShopName,
                    onValueChange = { coffeeShopName = it },
                    label = { Text("Coffee Shop Name") },
                    placeholder = { Text("Enter the name") },
                    modifier = Modifier.fillMaxWidth().testTag("inputCoffeeShopNameDescription"))
              }

              /*
               ExposedDropdownMenuBox(
                   expanded = expanded,
                   onExpandedChange = { expanded = !expanded } // Open/close the dropdown menu
                   ) {
                     TextField(
                         value = coffeeShopName,
                         onValueChange = {
                           coffeeShopName = it
                         }, // Optional for search implementation
                         readOnly =
                             true, // Prevent typing in the TextField, Will be changed for search
                         label = { Text("Enter the CoffeeShop Name") },
                         modifier =
                             Modifier.menuAnchor().fillMaxWidth().clickable {
                               expanded = true
                             }, // Open dropdown when clicked
                         trailingIcon = {
                           ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                         },
                         colors = ExposedDropdownMenuDefaults.textFieldColors())
                     ExposedDropdownMenu(
                         expanded = expanded, onDismissRequest = { expanded = false }) {
                           coffeeShopOptions.forEach { option ->
                             DropdownMenuItem(
                                 text = { Text(option) },
                                 onClick = {
                                   coffeeShopName = option // Set the selected coffee shop name
                                   expanded = false // Close the dropdown menu
                                 })
                           }
                         }
                   }
              */

              // Coffee Origin Dropdown Menu
              val focusRequester = remember { FocusRequester() }
              var coffeeOriginExpand by remember { mutableStateOf(false) }

              // Wrap with ExposedDropdownMenuBox for the dropdown functionality
              ExposedDropdownMenuBox(
                  expanded = coffeeOriginExpand,
                  onExpandedChange = { coffeeOriginExpand = !coffeeOriginExpand }) {
                    // TextField displaying the selected coffee origin
                    TextField(
                        value = coffeeOrigin.name,
                        onValueChange = {},
                        readOnly = true, // Prevent typing in the TextField
                        label = { Text("Coffee Origin") },
                        trailingIcon = {
                          ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier =
                            Modifier.menuAnchor()
                                .fillMaxWidth() // Set the width of the text field to fill the
                                                // parent width
                                .focusRequester(focusRequester) // Attach the FocusRequester
                                .clickable {
                                  expanded = true // Trigger dropdown when clicked
                                },
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                        keyboardActions =
                            KeyboardActions(onDone = { focusRequester.requestFocus() }))

                    // DropdownMenu with fixed height and scrolling capability
                    DropdownMenu(
                        expanded = coffeeOriginExpand,
                        onDismissRequest = { coffeeOriginExpand = false },
                        modifier =
                            Modifier.height(
                                    200.dp) // Limit height of the dropdown (set a fixed value)
                                .focusRequester(focusRequester) // Attach the FocusRequester
                        ) {
                          CoffeeOrigin.values().take(3).forEach { origin ->
                            DropdownMenuItem(
                                text = { Text(origin.name) },
                                onClick = {
                                  coffeeOrigin = origin // Set the selected coffee origin
                                  expanded = false // Close the dropdown
                                },
                                modifier = Modifier.padding(8.dp))
                          }
                        }
                  }
            }
      })
}

@Preview(showBackground = true)
@Composable
fun AddJourneyScreenPreview() {
  val navController = rememberNavController()
  val navigationActions = NavigationActions(navController)
  AddJourneyScreen(
      listJourneysViewModel = viewModel(factory = ListJourneysViewModel.Factory),
      navigationActions = navigationActions)
}
