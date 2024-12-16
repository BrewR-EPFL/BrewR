package com.android.brewr.ui.overview

import android.icu.util.GregorianCalendar
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.android.brewr.R
import com.android.brewr.model.journey.BrewingMethod
import com.android.brewr.model.journey.CoffeeOrigin
import com.android.brewr.model.journey.CoffeeRate
import com.android.brewr.model.journey.CoffeeTaste
import com.android.brewr.model.journey.Journey
import com.android.brewr.model.map.Location
import com.android.brewr.model.map.LocationViewModel
import com.android.brewr.ui.theme.CoffeeBrown
import com.android.brewr.ui.theme.Gold
import com.android.brewr.ui.theme.LightBrown
import com.android.brewr.utils.isConnectedToInternet
import com.android.brewr.utils.uploadPicture
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale
import android.content.Context
import com.android.brewr.model.journey.ListJourneysViewModel
import com.android.brewr.ui.navigation.NavigationActions

@Composable
fun JourneyImageBox(imageUri: Uri?, imageUrl: String?, onImageClick: () -> Unit, testTag: String) {
  Box(
      modifier =
          Modifier.size(150.dp).border(2.dp, Color.Black).testTag(testTag).clickable {
            onImageClick()
          }) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.Center)) {
              Text("Add Photo", color = Color.Black)
              Image(
                  painter = rememberAsyncImagePainter(imageUri ?: imageUrl),
                  contentDescription = "Selected Image",
                  modifier = Modifier.size(120.dp).testTag("selectedImagePreview"))
            }
      }
}

@Composable
fun JourneyDescriptionField(description: String, onDescriptionChange: (String) -> Unit) {
  OutlinedTextField(
      value = description,
      onValueChange = onDescriptionChange,
      label = { Text("Description") },
      placeholder = { Text("Capture your coffee experience") },
      modifier = Modifier.fillMaxWidth().height(150.dp).testTag("inputJourneyDescription"))
}

@Composable
fun CoffeeShopCheckRow(
    isYesSelected: Boolean,
    onCheckChange: () -> Unit,
    coffeeshopExpanded: Boolean,
    onSelectedLocationChange: (Location) -> Unit
) {
  val locationViewModel: LocationViewModel = viewModel(factory = LocationViewModel.Factory)
  val locationSuggestions by
      locationViewModel.locationSuggestions.collectAsState(initial = emptyList<Location?>())
  val locationQuery by locationViewModel.query.collectAsState()

  CoffeeShopCheckboxRow(isYesSelected, onCheckChange)

  if (coffeeshopExpanded) {
    LocationDropdown(
        locationSuggestions, locationQuery, onSelectedLocationChange, locationViewModel)
  } else if (!isYesSelected) {
    onSelectedLocationChange(Location())
  }
}

@Composable
fun CoffeeShopCheckboxRow(isYesSelected: Boolean, onCheckChange: () -> Unit) {
  Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.testTag("coffeeShopCheckRow").clickable { onCheckChange() }) {
        Icon(
            imageVector = if (isYesSelected) Icons.Outlined.Check else Icons.Outlined.Home,
            contentDescription = if (isYesSelected) "Checked" else "Unchecked",
            tint = Color.Black,
            modifier = Modifier.testTag("coffeeShopCheckboxIcon"))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = if (isYesSelected) "At a coffee shop" else "At home",
            color = Color.Black,
            modifier = Modifier.testTag("coffeeShopCheckText"))
      }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationDropdown(
    locationSuggestions: List<Location?>,
    locationQuery: String,
    onSelectedLocationChange: (Location) -> Unit,
    locationViewModel: LocationViewModel
) {
  var showDropdown by remember { mutableStateOf(false) }
  ExposedDropdownMenuBox(
      expanded = showDropdown && locationSuggestions.isNotEmpty(),
      onExpandedChange = { showDropdown = it }, // Toggle dropdown visibility
      modifier = Modifier.testTag("exposedDropdownMenuBox")) {
        OutlinedTextField(
            value = locationQuery,
            onValueChange = {
              locationViewModel.setQuery(it)
              showDropdown = true // Show dropdown when user starts typing
            },
            label = { Text("Coffeeshop") },
            placeholder = { Text("Enter the Coffeeshop") },
            modifier =
                Modifier.menuAnchor() // Anchor the dropdown to this text field
                    .fillMaxWidth()
                    .testTag("inputCoffeeshopLocation")
                    .onKeyEvent { keyEvent ->
                      if (keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.Enter) {
                        // Handle Enter key
                        onSelectedLocationChange(Location(0.0, 0.0, locationQuery))
                        true // Consume the event
                      } else {
                        false // Pass the event further
                      }
                    },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions =
                KeyboardActions(
                    onDone = {
                      // Handle IME "Done" action
                      onSelectedLocationChange(Location(0.0, 0.0, locationQuery))
                    }))

        // Dropdown menu for location suggestions
        ExposedDropdownMenu(
            expanded = showDropdown && locationSuggestions.isNotEmpty(),
            onDismissRequest = { showDropdown = false },
            modifier = Modifier.testTag("locationSuggestionsDropdown")) {
              locationSuggestions.filterNotNull().take(3).forEach { location ->
                DropdownMenuItem(
                    text = {
                      Text(
                          text =
                              location.name.take(30) +
                                  if (location.name.length > 30) "..." else "", // Limit name length
                          maxLines = 1 // Ensure name doesn't overflow
                          )
                    },
                    onClick = {
                      locationViewModel.setQuery(location.name)
                      onSelectedLocationChange(location)
                      showDropdown = false // Close dropdown on selection
                    },
                    modifier = Modifier.padding(8.dp))
              }

              if (locationSuggestions.size > 3) {
                DropdownMenuItem(
                    text = { Text("More...") },
                    onClick = { /* Optionally show more results */},
                    modifier = Modifier.padding(8.dp))
              }
            }
      }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoffeeOriginDropdownMenu(
    coffeeOrigin: CoffeeOrigin,
    onCoffeeOriginChange: (CoffeeOrigin) -> Unit
) {
  val focusRequester = remember { FocusRequester() }
  var coffeeOriginExpand by remember { mutableStateOf(false) }
  var expanded by remember { mutableStateOf(false) }

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
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
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
            keyboardActions = KeyboardActions(onDone = { focusRequester.requestFocus() }))

        // DropdownMenu with fixed height and scrolling capability
        DropdownMenu(
            expanded = coffeeOriginExpand,
            onDismissRequest = { coffeeOriginExpand = false },
            modifier =
                Modifier.height(200.dp) // Limit height of the dropdown (set a fixed value)
                    .focusRequester(focusRequester) // Attach the FocusRequester
                    .testTag("dropdownMenuCoffeeOrigin") // Add a test tag for testing
            ) {
              CoffeeOrigin.entries.drop(1).forEach { origin ->
                DropdownMenuItem(
                    text = { Text(origin.name) },
                    onClick = {
                      onCoffeeOriginChange(origin) // Set the selected coffee origin
                      expanded = false // Close the dropdown
                    },
                    modifier = Modifier.padding(8.dp).testTag("YourJourneyTitle"))
              }
            }
      }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
fun BrewingMethodField(
    brewingMethod: BrewingMethod,
    onBrewingMethodChange: (BrewingMethod) -> Unit
) {
  Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(2.dp) // Space between title and buttons
      ) {
        Text(
            text = "Brewing Method",
            fontSize = 16.sp, // Adjust the font size for the title
            fontWeight = FontWeight.Bold, // Make the title bold
        )

        FlowRow(modifier = Modifier.padding(16.dp)) {
          BrewingMethod.entries.drop(1).forEach { method ->
            // Determine if this method is the currently selected one
            val isSelected = brewingMethod == method

            // Use Button or OutlinedButton based on selection
            if (isSelected) {
              Button(
                  onClick = { onBrewingMethodChange(method) },
                  shape = RoundedCornerShape(16.dp),
                  modifier = Modifier.padding(4.dp).testTag("Button:${method.name}"),
                  contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                  colors =
                      ButtonDefaults.buttonColors(
                          containerColor = LightBrown, contentColor = CoffeeBrown)) {
                    Text(method.name.replace("_", " "), modifier = Modifier.padding(4.dp))
                  }
            } else {
              OutlinedButton(
                  onClick = { onBrewingMethodChange(method) },
                  shape = RoundedCornerShape(16.dp),
                  modifier = Modifier.padding(4.dp).testTag("Button:${method.name}"),
                  contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                  colors =
                      ButtonDefaults.outlinedButtonColors(
                          contentColor = Color(0xFF000000),
                      )) {
                    Text(method.name.replace("_", " "), modifier = Modifier.padding(4.dp))
                  }
            }
          }
        }
      }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CoffeeTasteField(coffeeTaste: CoffeeTaste, onCoffeeTasteChange: (CoffeeTaste) -> Unit) {
  Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(2.dp) // Space between title and buttons
      ) {
        Text(
            text = "Taste",
            fontSize = 16.sp, // Adjust the font size for the title
            fontWeight = FontWeight.Bold, // Make the title bold
        )

        FlowRow(modifier = Modifier.padding(16.dp)) {
          CoffeeTaste.entries.drop(1).forEach { taste ->
            // Determine if this method is the currently selected one
            val isSelected = coffeeTaste == taste

            // Use Button or OutlinedButton based on selection
            if (isSelected) {
              Button(
                  onClick = { onCoffeeTasteChange(taste) },
                  shape = RoundedCornerShape(16.dp),
                  modifier = Modifier.padding(4.dp).testTag("Button:${taste.name}"),
                  contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                  colors =
                      ButtonDefaults.buttonColors(
                          containerColor = LightBrown, contentColor = CoffeeBrown)) {
                    Text(taste.name.replace("_", " "), modifier = Modifier.padding(4.dp))
                  }
            } else {
              OutlinedButton(
                  onClick = { onCoffeeTasteChange(taste) },
                  shape = RoundedCornerShape(16.dp),
                  modifier = Modifier.padding(4.dp).testTag("Button:${taste.name}"),
                  contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                  colors =
                      ButtonDefaults.outlinedButtonColors(
                          contentColor = Color(0xFF000000),
                      )) {
                    Text(
                        taste.name.replace("_", " "),
                        modifier = Modifier.padding(4.dp).testTag("Button:${taste.name}"))
                  }
            }
          }
        }
      }
}

@Composable
fun CoffeeRateField(coffeeRate: CoffeeRate, onCoffeeRateChange: (CoffeeRate) -> Unit) {
  Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(2.dp) // Space between title and buttons
      ) {
        Text(
            text = "Rate",
            fontSize = 16.sp, // Adjust the font size for the title
            fontWeight = FontWeight.Bold, // Make the title bold
        )

        // Map CoffeeRate to the number of stars
        val starCount =
            coffeeRate.ordinal // ordinal gives you 0-based index, so we don't add 1 due to
        // the default parameter
        Row(
            modifier = Modifier.fillMaxWidth().testTag("rateRow"), // Add a test tag for testing
            horizontalArrangement = Arrangement.Center // Center the star icons
            ) {
              for (i in 1..5) {
                if (i <= starCount) {
                  Icon(
                      imageVector = Icons.Filled.Star,
                      contentDescription = "Filled Star $i",
                      tint = Gold, // Gold color for filled star
                      modifier =
                          Modifier.size(40.dp).testTag("FilledStar$i").clickable {
                            // Update the coffeeRate when the star is clicked
                            onCoffeeRateChange(CoffeeRate.entries[i])
                          })
                } else {
                  Icon(
                      imageVector = Icons.Outlined.Star,
                      contentDescription = "Outlined Star $i",
                      tint = Color(0xFF312F2F), // Same gold color for consistency
                      modifier =
                          Modifier.size(40.dp).testTag("OutlinedStar$i").clickable {
                            // Update the coffeeRate when the star is clicked
                            onCoffeeRateChange(CoffeeRate.entries[i])
                          })
                }
              }
            }
      }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateField(date: Timestamp, onDateChange: (Timestamp) -> Unit) {
  var showDatePicker by remember { mutableStateOf(false) }
  var selectedDate by remember { mutableStateOf(date) }
  val datePickerState =
      rememberDatePickerState(
          initialSelectedDateMillis = date.toDate().time, initialDisplayMode = DisplayMode.Picker)
  val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

  // Trigger the DatePickerDialog
  if (showDatePicker) {
    DatePickerDialog(
        onDismissRequest = { showDatePicker = false },
        confirmButton = {
          TextButton(
              onClick = {
                val selectedMillis = datePickerState.selectedDateMillis
                if (selectedMillis != null) {
                  val calendar = GregorianCalendar()
                  calendar.timeInMillis = selectedMillis
                  val timestamp = Timestamp(calendar.time)
                  selectedDate = timestamp
                  onDateChange(timestamp)
                }
                showDatePicker = false
              }) {
                Text("OK", fontWeight = FontWeight.Bold)
              }
        },
        dismissButton = {
          TextButton(onClick = { showDatePicker = false }) {
            Text("Cancel", fontWeight = FontWeight.Bold)
          }
        },
        modifier = Modifier.testTag("datePickerDialog")) {
          DatePicker(state = datePickerState)
        }
  }

  Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
    Text(
        text = "Date",
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.testTag("dateTitle"))
    // UI element to open the DatePickerDialog
    TextButton(onClick = { showDatePicker = true }, modifier = Modifier.testTag("dateButton")) {
      Text(text = selectedDate.let { dateFormat.format(it.toDate()) }, fontSize = 14.sp)
    }
  }
}


fun journeySaveButtonClick(context: Context, uid: String, imageUri: Uri?, description: String, selectedLocation: Location, coffeeOrigin: CoffeeOrigin, brewingMethod: BrewingMethod, coffeeTaste: CoffeeTaste, coffeeRate: CoffeeRate, selectedDate: Timestamp, listJourneysViewModel: ListJourneysViewModel, navigationActions: NavigationActions) {
    if (imageUri != null) {
        if (isConnectedToInternet(context)) {
            uploadPicture(imageUri!!) { imageUrl ->
                val newJourney =
                    Journey(
                        uid = uid,
                        imageUrl = imageUrl, // Use the downloaded URL from Firebase
                        description = description,
                        location = selectedLocation,
                        coffeeOrigin = coffeeOrigin,
                        brewingMethod = brewingMethod,
                        coffeeTaste = coffeeTaste,
                        coffeeRate = coffeeRate,
                        date = selectedDate
                    )
                listJourneysViewModel.addJourney(newJourney)
                navigationActions.goBack()
                return@uploadPicture
            }
        } else {
            // Use a predefined image URL when offline
            val predefinedImageUrl =
                "android.resource://${context.packageName}/${R.drawable.offlinemode}"

            val newJourney =
                Journey(
                    uid = uid,
                    imageUrl = predefinedImageUrl, // Use the predefined URL
                    description = description,
                    location = selectedLocation,
                    coffeeOrigin = coffeeOrigin,
                    brewingMethod = brewingMethod,
                    coffeeTaste = coffeeTaste,
                    coffeeRate = coffeeRate,
                    date = selectedDate
                )
            listJourneysViewModel.addJourney(newJourney)
            navigationActions
                .goBack() // Update the Journey with real image when connected to the
            // internet
            uploadPicture(imageUri!!) { imageUrl ->
                val journeyWithRealImage =
                    Journey(
                        uid = uid,
                        imageUrl = imageUrl, // Use the downloaded URL from Firebase
                        description = description,
                        location = selectedLocation,
                        coffeeOrigin = coffeeOrigin,
                        brewingMethod = brewingMethod,
                        coffeeTaste = coffeeTaste,
                        coffeeRate = coffeeRate,
                        date = selectedDate
                    )
                listJourneysViewModel.updateJourney(journeyWithRealImage)
                return@uploadPicture
            }
        }
    } else {
        Toast.makeText(context, "Please select an image", Toast.LENGTH_SHORT).show()
    }
}