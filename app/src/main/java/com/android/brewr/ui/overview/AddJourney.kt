package com.android.brewr.ui.overview

import android.app.DatePickerDialog
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Add
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
import androidx.compose.ui.text.style.TextAlign
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
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddJourneyScreen(
    listJourneysViewModel: ListJourneysViewModel =
        viewModel(factory = ListJourneysViewModel.Factory),
    navigationActions: NavigationActions
) {
  //val currentUser = FirebaseAuth.getInstance().currentUser
  val uid = listJourneysViewModel.getNewUid()
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
  // Convert the Timestamp to Date and format it
  val formattedDate =
      remember(date) {
        val dateObject = date.toDate() // Convert Timestamp to Date
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) // Date format
        formatter.format(dateObject) // Return formatted date
      }

  var expanded by remember { mutableStateOf(false) } // State for the dropdown menu
  var isYesSelected by remember { mutableStateOf(false) }

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
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()), // Add padding to the whole Column
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
                        value = coffeeOrigin.name,
                        onValueChange = {},
                        readOnly = true, // Prevent typing in the TextField
                        // label = { Text("Coffee Origin") },
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
                          CoffeeOrigin.values().forEach { origin ->
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
                      BrewingMethod.values().forEach { method ->
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
                                    modifier = Modifier.padding(4.dp))
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
                                    modifier = Modifier.padding(4.dp))
                              }
                        }
                      }
                    }
                  }

            //Taste
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
                      CoffeeTaste.values().forEach { taste ->
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
                                    taste.name.replace("_", " "), modifier = Modifier.padding(4.dp))
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
                                    taste.name.replace("_", " "), modifier = Modifier.padding(4.dp))
                              }
                        }
                      }
                    }
                  }

            //Rate
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
                val starCount = coffeeRate.ordinal + 1 // ordinal gives you 0-based index, so add 1
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center // Center the star icons
                )  {
                    for (i in 1..5) {
                        if (i <= starCount) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = "Filled Star $i",
                                tint = Color(0xFFFFD700), // Gold color for filled star
                                modifier = Modifier.size(40.dp)
                                    .clickable {
                                    // Update the coffeeRate when the star is clicked
                                    coffeeRate = CoffeeRate.values()[i - 1]
                                }
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.Star,
                                contentDescription = "Outlined Star $i",
                                tint = Color(0xFF312F2F), // Same gold color for consistency
                                modifier = Modifier.size(40.dp)
                                    .clickable {
                                    // Update the coffeeRate when the star is clicked
                                    coffeeRate = CoffeeRate.values()[i - 1]
                                }
                            )
                        }
                    }
                }
            }

              // DatePickerDialog initialization logic
              val datePickerDialog = remember {
                DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                      // Update the date state when a new date is selected
                      val calendar = Calendar.getInstance()
                      calendar.set(year, month, dayOfMonth)
                      date = Timestamp(calendar.time) // Convert Date to Timestamp
                    },
                    // Initialize the dialog with the current date values
                    Calendar.getInstance()
                        .apply {
                          time = date.toDate() // Use the current timestamp's Date
                        }
                        .get(Calendar.YEAR),
                    Calendar.getInstance().apply { time = date.toDate() }.get(Calendar.MONTH),
                    Calendar.getInstance()
                        .apply { time = date.toDate() }
                        .get(Calendar.DAY_OF_MONTH))
              }

            //Date
              Column(modifier = Modifier.padding(16.dp)) {
                // Label Text
                Text(
                    text = "Date",
                    modifier = Modifier.padding(bottom = 20.dp),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold)

                // OutlinedTextField displaying the formatted date
                OutlinedTextField(
                    value = formattedDate, // Display the formatted date
                    onValueChange = {}, // Read-only field, no direct input
                    readOnly = true, // To prevent manual edits
                    textStyle =
                        androidx.compose.ui.text.TextStyle(
                            textAlign = TextAlign.Center), // Center align the text
                    modifier =
                        Modifier.padding(8.dp).clickable {
                          // Show DatePickerDialog when clicked
                          datePickerDialog.show()
                        })
              }




            Button(
                onClick = {
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
                                    date = date,
                                    location = location)
                            )

                            navigationActions.goBack()
                            Toast.makeText(
                                context, "Saved!", Toast.LENGTH_SHORT)
                                .show()
                            return@Button
                },
                modifier = Modifier.fillMaxWidth().testTag("AddRecordSave")) {
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
