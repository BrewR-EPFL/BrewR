package com.android.brewr.ui.overview

import android.icu.util.GregorianCalendar
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.android.brewr.model.journey.BrewingMethod
import com.android.brewr.model.journey.CoffeeOrigin
import com.android.brewr.model.journey.CoffeeRate
import com.android.brewr.model.journey.CoffeeTaste
import com.android.brewr.model.journey.Journey
import com.android.brewr.model.journey.ListJourneysViewModel
import com.android.brewr.ui.navigation.NavigationActions
import com.android.brewr.ui.theme.Purple80
import com.google.firebase.Timestamp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddJourneyScreen(
    listJourneysViewModel: ListJourneysViewModel =
        viewModel(factory = ListJourneysViewModel.Factory),
    navigationActions: NavigationActions
) {
  val uid = listJourneysViewModel.getNewUid()
  val imageUrl by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }
  var coffeeShopName by remember { mutableStateOf("") }
  var coffeeOrigin by remember { mutableStateOf(CoffeeOrigin.DEFAULT) }
  var brewingMethod by remember { mutableStateOf(BrewingMethod.DEFAULT) }
  var coffeeTaste by remember { mutableStateOf(CoffeeTaste.DEFAULT) }
  var coffeeRate by remember { mutableStateOf(CoffeeRate.DEFAULT) }
  val date by remember { mutableStateOf("") } // Using Firebase Timestamp for now
  var location by remember { mutableStateOf("") } // Will change to Location once it's implemented
  val context = LocalContext.current
  var expanded by remember { mutableStateOf(false) } // State for the dropdown menu
  var isYesSelected by remember { mutableStateOf(false) }

  Scaffold(
      topBar = {
        TopAppBar(
            navigationIcon = {
              IconButton(
                  onClick = { navigationActions.goBack() },
                  modifier = Modifier.testTag("backButton")) {
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
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()), // Add padding to the whole Column
            verticalArrangement = Arrangement.spacedBy(16.dp)) {
              Text(
                  text = "Your Journey",
                  style = MaterialTheme.typography.titleLarge,
                  color = Color.Black,
                  modifier = Modifier.fillMaxWidth().testTag("YourJourneyTitle"),
              )

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
                                .testTag("addPhotoBox") // Add a test tag for testing
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
                  }

              // CoffeeShop Dropdown Menu below the row

              Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier =
                      Modifier.testTag("atCoffeeShopRow") // Add a test tag for testing
                          .clickable {
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

              // Coffee Origin Dropdown Menu
              val focusRequester = remember { FocusRequester() }
              var coffeeOriginExpand by remember { mutableStateOf(false) }

              // Wrap with ExposedDropdownMenuBox for the dropdown functionality
              Text(
                  text = "Origin",
                  fontSize = 16.sp, // Adjust the font size for the title
                  fontWeight = FontWeight.Bold, // Make the title bold
              )
              ExposedDropdownMenuBox(
                  expanded = coffeeOriginExpand,
                  onExpandedChange = { coffeeOriginExpand = !coffeeOriginExpand }) {
                    // TextField displaying the selected coffee origin
                    TextField(
                        value = coffeeOrigin.name.replace("DEFAULT", "Select the origin"),
                        onValueChange = {},
                        readOnly = true, // Prevent typing in the TextField
                        trailingIcon = {
                          ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier =
                            Modifier.menuAnchor()
                                .fillMaxWidth() // Set the width of the text field to fill the
                                // parent width
                                .testTag("inputCoffeeOrigin") // Add a test tag for testing
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
                                .testTag("dropdownMenuCoffeeOrigin") // Add a test tag for testing
                        ) {
                          CoffeeOrigin.values().drop(1).forEach { origin ->
                            DropdownMenuItem(
                                text = { Text(origin.name) },
                                onClick = {
                                  coffeeOrigin = origin // Set the selected coffee origin
                                  expanded = false // Close the dropdown
                                },
                                modifier = Modifier.padding(8.dp).testTag("YourJourneyTitle"))
                          }
                        }
                  }

              Column(
                  modifier = Modifier.fillMaxWidth(),
                  verticalArrangement =
                      Arrangement.spacedBy(2.dp) // Space between title and buttons
                  ) {
                    Text(
                        text = "Brewing Method",
                        fontSize = 16.sp, // Adjust the font size for the title
                        fontWeight = FontWeight.Bold, // Make the title bold
                    )

                    FlowRow(modifier = Modifier.padding(16.dp)) {
                      BrewingMethod.values().drop(1).forEach { method ->
                        // Determine if this method is the currently selected one
                        val isSelected = brewingMethod == method

                        // Use Button or OutlinedButton based on selection
                        if (isSelected) {
                          Button(
                              onClick = { brewingMethod = method },
                              shape = RoundedCornerShape(16.dp),
                              modifier = Modifier.padding(4.dp),
                              contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                              colors =
                                  ButtonDefaults.buttonColors(
                                      containerColor = Purple80, contentColor = Color.White)) {
                                Text(
                                    method.name.replace("_", " "),
                                    modifier =
                                        Modifier.padding(4.dp).testTag("Button:${method.name}"))
                              }
                        } else {
                          OutlinedButton(
                              onClick = { brewingMethod = method },
                              shape = RoundedCornerShape(16.dp),
                              modifier = Modifier.padding(4.dp),
                              contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                              colors =
                                  ButtonDefaults.outlinedButtonColors(
                                      contentColor = Color(0xFF000000),
                                  )) {
                                Text(
                                    method.name.replace("_", " "),
                                    modifier =
                                        Modifier.padding(4.dp).testTag("Button:${method.name}"))
                              }
                        }
                      }
                    }
                  }

              // Taste
              Column(
                  modifier = Modifier.fillMaxWidth(),
                  verticalArrangement =
                      Arrangement.spacedBy(2.dp) // Space between title and buttons
                  ) {
                    Text(
                        text = "Taste",
                        fontSize = 16.sp, // Adjust the font size for the title
                        fontWeight = FontWeight.Bold, // Make the title bold
                    )

                    FlowRow(modifier = Modifier.padding(16.dp)) {
                      CoffeeTaste.values().drop(1).forEach { taste ->
                        // Determine if this method is the currently selected one
                        val isSelected = coffeeTaste == taste

                        // Use Button or OutlinedButton based on selection
                        if (isSelected) {
                          Button(
                              onClick = { coffeeTaste = taste },
                              shape = RoundedCornerShape(16.dp),
                              modifier = Modifier.padding(4.dp),
                              contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                              colors =
                                  ButtonDefaults.buttonColors(
                                      containerColor = Purple80, contentColor = Color.White)) {
                                Text(
                                    taste.name.replace("_", " "),
                                    modifier =
                                        Modifier.padding(4.dp).testTag("Button:${taste.name}"))
                              }
                        } else {
                          OutlinedButton(
                              onClick = { coffeeTaste = taste },
                              shape = RoundedCornerShape(16.dp),
                              modifier = Modifier.padding(4.dp),
                              contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                              colors =
                                  ButtonDefaults.outlinedButtonColors(
                                      contentColor = Color(0xFF000000),
                                  )) {
                                Text(
                                    taste.name.replace("_", " "),
                                    modifier =
                                        Modifier.padding(4.dp).testTag("Button:${taste.name}"))
                              }
                        }
                      }
                    }
                  }

              // Rate
              Column(
                  modifier = Modifier.fillMaxWidth(),
                  verticalArrangement =
                      Arrangement.spacedBy(2.dp) // Space between title and buttons
                  ) {
                    Text(
                        text = "Rate",
                        fontSize = 16.sp, // Adjust the font size for the title
                        fontWeight = FontWeight.Bold, // Make the title bold
                    )

                    // Map CoffeeRate to the number of stars
                    val starCount =
                        coffeeRate
                            .ordinal // ordinal gives you 0-based index, so we don't add 1 due to
                                     // the default parameter
                    Row(
                        modifier =
                            Modifier.fillMaxWidth()
                                .testTag("rateRow"), // Add a test tag for testing
                        horizontalArrangement = Arrangement.Center // Center the star icons
                        ) {
                          for (i in 1..5) {
                            if (i <= starCount) {
                              Icon(
                                  imageVector = Icons.Filled.Star,
                                  contentDescription = "Filled Star $i",
                                  tint = Color(0xFFFFD700), // Gold color for filled star
                                  modifier =
                                      Modifier.size(40.dp).clickable {
                                        // Update the coffeeRate when the star is clicked
                                        coffeeRate = CoffeeRate.values()[i - 1]
                                      })
                            } else {
                              Icon(
                                  imageVector = Icons.Outlined.Star,
                                  contentDescription = "Outlined Star $i",
                                  tint = Color(0xFF312F2F), // Same gold color for consistency
                                  modifier =
                                      Modifier.size(40.dp).clickable {
                                        // Update the coffeeRate when the star is clicked
                                        coffeeRate = CoffeeRate.values()[i - 1]
                                      })
                            }
                          }
                        }
                  }

              // Date

              var selectedDate by remember { mutableStateOf(date) }
              Column() {
                // Label Text
                Text(
                    text = "Date",
                    modifier = Modifier.padding(bottom = 20.dp),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold)

                // Date Text
                TextField(
                    value = selectedDate,
                    onValueChange = { selectedDate = it },
                    label = { Text("DD/MM/YYYY") },
                    placeholder = { Text(selectedDate) },
                    modifier = Modifier.fillMaxWidth().testTag("inputDate"))
              }
              // Location
              Column() {
                // Label Text
                Text(
                    text = "Location",
                    modifier = Modifier.padding(bottom = 20.dp),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold)

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    placeholder = { Text("Enter the location") },
                    modifier = Modifier.fillMaxWidth().testTag("location"))
              }

              Button(
                  onClick = {
                    val calendar = GregorianCalendar()
                    val parts = selectedDate.split("/")
                    if (parts.size == 3) {
                      try {
                        calendar.set(
                            parts[2].toInt(),
                            parts[1].toInt() - 1, // Months are 0-based
                            parts[0].toInt(),
                            0,
                            0,
                            0)

                        listJourneysViewModel.addJourney(
                            Journey(
                                uid = uid,
                                imageUrl = imageUrl,
                                description = description,
                                coffeeShopName = coffeeShopName,
                                coffeeOrigin = coffeeOrigin,
                                brewingMethod = brewingMethod,
                                coffeeTaste = coffeeTaste,
                                coffeeRate = coffeeRate,
                                date = Timestamp(calendar.time),
                                location = location))
                        navigationActions.goBack()

                        return@Button
                      } catch (_: NumberFormatException) {}
                    }

                    Toast.makeText(
                            context, "Invalid format, date must be DD/MM/YYYY.", Toast.LENGTH_SHORT)
                        .show()
                  },
                  modifier = Modifier.fillMaxWidth().testTag("journeySave")) {
                    Text("Save")
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
