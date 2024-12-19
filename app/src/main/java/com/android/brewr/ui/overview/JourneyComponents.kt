package com.android.brewr.ui.overview

import android.content.Context
import android.icu.util.GregorianCalendar
import android.net.Uri
import android.util.Log
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.android.brewr.model.coffee.CoffeeShop
import com.android.brewr.model.journey.BrewingMethod
import com.android.brewr.model.journey.CoffeeOrigin
import com.android.brewr.model.journey.CoffeeRate
import com.android.brewr.model.journey.CoffeeTaste
import com.android.brewr.model.journey.Location
import com.android.brewr.ui.theme.CoffeeBrown
import com.android.brewr.ui.theme.Gold
import com.android.brewr.ui.theme.LightBrown
import com.android.brewr.utils.fetchCoffeeShopsByLocationQuery
import com.android.brewr.utils.getCurrentLocation
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Displays an image box for selecting and previewing images.
 *
 * @param imageUri The URI of the selected image (if any).
 * @param imageUrl The URL of the selected image (used for previews).
 * @param onImageClick Callback when the image box is clicked.
 * @param testTag A test tag for UI testing purposes.
 */
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

/**
 * Displays a text field for entering a journey description.
 *
 * @param description The current description text.
 * @param onDescriptionChange Callback when the description is updated.
 */
@Composable
fun JourneyDescriptionField(description: String, onDescriptionChange: (String) -> Unit) {
  OutlinedTextField(
      value = description,
      onValueChange = onDescriptionChange,
      label = { Text("Description") },
      placeholder = { Text("Capture your coffee experience") },
      modifier = Modifier.fillMaxWidth().height(150.dp).testTag("inputJourneyDescription"))
}

/**
 * Displays a row with a checkbox for selecting "At home" or "At a coffee shop".
 *
 * @param isYesSelected Whether the coffee shop option is selected.
 * @param onCheckChange Callback when the checkbox state changes.
 * @param coffeeShopExpanded Whether the coffee shop dropdown is expanded.
 * @param onSelectedCoffeeShopChange Callback to update the selected [CoffeeShop].
 * @param scope The coroutine scope for launching asynchronous tasks.
 * @param context The context for accessing resources and services.
 */
@Composable
fun CoffeeShopCheckRow(
    isYesSelected: Boolean,
    onCheckChange: () -> Unit,
    coffeeShopExpanded: Boolean,
    onSelectedCoffeeShopChange: (CoffeeShop) -> Unit,
    scope: CoroutineScope,
    context: Context
) {
  CoffeeShopCheckboxRow(isYesSelected, onCheckChange)

  if (coffeeShopExpanded) {
    LocationDropdown(onSelectedCoffeeShopChange, scope, context)
  } else if (!isYesSelected) {
    onSelectedCoffeeShopChange(
        CoffeeShop(
            id = "Unknown",
            coffeeShopName = "Unknown Location",
            location = Location(latitude = 0.0, longitude = 0.0, name = "Unknown Location"),
            rating = 0.0,
            hours = emptyList(),
            reviews = emptyList(),
            imagesUrls = emptyList()))
  }
}

/**
 * Displays a row with a checkbox for selecting "At home" or "At a coffee shop".
 *
 * @param isYesSelected Whether the coffee shop option is selected.
 * @param onCheckChange Callback when the checkbox state changes.
 */
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

/**
 * Displays a dropdown menu for selecting a coffee shop location.
 *
 * Users can search for a coffee shop by typing a query. Matching location suggestions are displayed
 * in a dropdown list. Selecting an option updates the selected location.
 *
 * @param onSelectedLocationChange Callback to update the selected [CoffeeShop].
 * @param coroutineScope The coroutine scope for launching asynchronous tasks.
 * @param context The context for accessing resources and services.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationDropdown(
    onSelectedLocationChange: (CoffeeShop) -> Unit,
    coroutineScope: CoroutineScope,
    context: Context
) {
  var locationQuery by remember { mutableStateOf("") }
  var userLocation by remember { mutableStateOf<LatLng?>(null) }
  var coffeeShops by remember { mutableStateOf<List<CoffeeShop>>(emptyList()) }
  var showDropdown by remember { mutableStateOf(false) }

  // Whenever locationQuery changes and is not blank, fetch coffee shops
  LaunchedEffect(locationQuery) {
    if (locationQuery.isNotBlank()) {
      coroutineScope.launch {
        getCurrentLocation(
            context,
            onSuccess = {
              Log.e("UserLocation", "Current location: $it")
              // Fetch coffee shops once
              fetchCoffeeShopsByLocationQuery(coroutineScope, context, locationQuery, it) {
                  fetchedShops ->
                userLocation = it
                coffeeShops = fetchedShops
              }
            })
      }
    } else {
      coffeeShops = emptyList()
    }
  }

  // Helper to select a coffee shop
  fun selectCoffeeShop() {
    fetchCoffeeShopsByLocationQuery(coroutineScope, context, locationQuery, userLocation) { shops ->
      val selected =
          if (shops.isNotEmpty()) {
            shops.first()
          } else {
            // Fallback coffee shop if none found
            CoffeeShop(
                id = "Unknown",
                coffeeShopName = locationQuery.ifBlank { "Unknown Location" },
                location =
                    Location(
                        latitude = 0.0,
                        longitude = 0.0,
                        name = locationQuery.ifBlank { "Unknown Location" }),
                rating = 0.0,
                hours = emptyList(),
                reviews = emptyList(),
                imagesUrls = emptyList())
          }
      onSelectedLocationChange(selected)
    }
  }

  ExposedDropdownMenuBox(
      expanded = showDropdown && coffeeShops.isNotEmpty(),
      onExpandedChange = { showDropdown = it },
      modifier = Modifier.testTag("exposedDropdownMenuBox")) {
        OutlinedTextField(
            value = locationQuery,
            onValueChange = { newQuery ->
              locationQuery = newQuery
              showDropdown = true
            },
            label = { Text("Coffeeshop") },
            placeholder = { Text("Enter the Coffeeshop") },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions =
                KeyboardActions(
                    onDone = {
                      selectCoffeeShop()
                      showDropdown = false
                    }),
            modifier = Modifier.menuAnchor().fillMaxWidth().testTag("inputCoffeeshopLocation"))

        // Dropdown menu for coffee shop suggestions
        ExposedDropdownMenu(
            expanded = showDropdown && coffeeShops.isNotEmpty(),
            onDismissRequest = { showDropdown = false },
            modifier = Modifier.testTag("locationSuggestionsDropdown")) {
              coffeeShops.take(3).forEach { coffeeShop ->
                DropdownMenuItem(
                    text = {
                      Text(
                          text =
                              coffeeShop.coffeeShopName.take(30) +
                                  if (coffeeShop.coffeeShopName.length > 30) "..., "
                                  else
                                      ", " +
                                          coffeeShop.location.name
                                              .split(",", limit = 2)
                                              .joinToString(", "),
                          maxLines = 1)
                    },
                    onClick = {
                      onSelectedLocationChange(coffeeShop)
                      showDropdown = false
                    },
                    modifier = Modifier.padding(8.dp))
              }

              if (coffeeShops.size > 3) {
                DropdownMenuItem(
                    text = { Text("More...") },
                    onClick = {
                      // Handle showing more results or pagination if needed
                    },
                    modifier = Modifier.padding(8.dp))
              }
            }
      }
}

/**
 * Displays a dropdown menu for selecting a coffee origin.
 *
 * @param coffeeOrigin The currently selected coffee origin.
 * @param onCoffeeOriginChange Callback when the coffee origin is updated.
 */
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

/**
 * Displays a selection field for choosing a brewing method.
 *
 * This function provides users with options to choose a brewing method using buttons. Selected
 * options are displayed as filled buttons, while unselected options appear as outlined buttons.
 *
 * @param brewingMethod The currently selected brewing method as a [BrewingMethod] enum value.
 * @param onBrewingMethodChange Callback to update the selected brewing method.
 */
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

/**
 * Displays a selection field for choosing a coffee taste.
 *
 * This function allows users to choose a coffee taste using buttons. The selected option is
 * highlighted for clarity, while unselected options are displayed as outlined buttons.
 *
 * @param coffeeTaste The currently selected coffee taste as a [CoffeeTaste] enum value.
 * @param onCoffeeTasteChange Callback to update the selected coffee taste.
 */
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

/**
 * Displays a rating field for selecting a coffee rating using stars.
 *
 * @param coffeeRate The currently selected coffee rate.
 * @param onCoffeeRateChange Callback when the coffee rate is updated.
 */
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

/**
 * Displays a date picker for selecting a date.
 *
 * @param date The currently selected date as a [Timestamp].
 * @param onDateChange Callback when the date is updated.
 */
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
