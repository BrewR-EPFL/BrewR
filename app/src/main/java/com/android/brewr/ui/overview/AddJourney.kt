package com.android.brewr.ui.overview

import android.icu.util.GregorianCalendar
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.brewr.model.journey.BrewingMethod
import com.android.brewr.model.journey.CoffeeOrigin
import com.android.brewr.model.journey.CoffeeRate
import com.android.brewr.model.journey.CoffeeTaste
import com.android.brewr.model.journey.Journey
import com.android.brewr.model.journey.ListJourneysViewModel
import com.android.brewr.ui.navigation.NavigationActions
import com.android.brewr.utils.uploadPicture
import com.google.firebase.Timestamp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddJourneyScreen(
    listJourneysViewModel: ListJourneysViewModel =
        viewModel(factory = ListJourneysViewModel.Factory),
    navigationActions: NavigationActions
) {
  val uid = listJourneysViewModel.getNewUid()
  var imageUri by remember { mutableStateOf<Uri?>(null) }
  var description by remember { mutableStateOf("") }
  var coffeeShopName by remember {
    mutableStateOf("")
  } // Will change to Location once it's implemented
  var coffeeOrigin by remember { mutableStateOf(CoffeeOrigin.DEFAULT) }
  var brewingMethod by remember { mutableStateOf(BrewingMethod.DEFAULT) }
  var coffeeTaste by remember { mutableStateOf(CoffeeTaste.DEFAULT) }
  var coffeeRate by remember { mutableStateOf(CoffeeRate.DEFAULT) }
  val date by remember { mutableStateOf("") } // Using Firebase Timestamp for now
  val context = LocalContext.current
  var expanded by remember { mutableStateOf(false) } // State for the dropdown menu
  var isYesSelected by remember { mutableStateOf(false) }

  val getImageLauncher =
      rememberLauncherForActivityResult(
          contract = ActivityResultContracts.GetContent(), onResult = { uri -> imageUri = uri })

  Scaffold(
      modifier = Modifier.testTag("addJourneyScreen"),
      topBar = {
        TopAppBar(
            navigationIcon = {
              IconButton(
                  onClick = { navigationActions.goBack() },
                  modifier = Modifier.testTag("backButton")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
                    JourneyImageBox(
                        imageUri = imageUri,
                        imageUrl = null,
                        onImageClick = {
                          // Open the gallery to pick an image
                          getImageLauncher.launch("image/*")
                        },
                        testTag = "addImageBox")

                    // Description section on the right
                    JourneyDescriptionField(
                        description = description, onDescriptionChange = { description = it })
                  }
              // CoffeeShop Dropdown Menu below the row
              CoffeeShopCheckRow(
                  isYesSelected = isYesSelected,
                  onCheckChange = {
                    isYesSelected = !isYesSelected
                    expanded = isYesSelected
                  },
                  expanded = expanded,
                  coffeeShopName = coffeeShopName,
                  onCoffeeShopNameChange = { coffeeShopName = it })

              // Coffee Origin Dropdown Menu
              CoffeeOriginDropdownMenu(
                  coffeeOrigin = coffeeOrigin, onCoffeeOriginChange = { coffeeOrigin = it })

              // Brewing Method

              BrewingMethodField(
                  brewingMethod = brewingMethod, onBrewingMethodChange = { brewingMethod = it })

              // Taste

              CoffeeTasteField(
                  coffeeTaste = coffeeTaste, onCoffeeTasteChange = { coffeeTaste = it })

              // Rate
              CoffeeRateField(coffeeRate = coffeeRate, onCoffeeRateChange = { coffeeRate = it })

              // Date

              var selectedDate by remember { mutableStateOf(date) }
              DateField(selectedDate) { selectedDate = it }

              Button(
                  onClick = {
                    if (imageUri != null) {
                      uploadPicture(imageUri!!) { imageUrl ->
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

                            // Create a new journey with the uploaded image URL
                            val newJourney =
                                Journey(
                                    uid = uid,
                                    imageUrl = imageUrl, // Use the downloaded URL from Firebase
                                    description = description,
                                    coffeeShopName = coffeeShopName,
                                    coffeeOrigin = coffeeOrigin,
                                    brewingMethod = brewingMethod,
                                    coffeeTaste = coffeeTaste,
                                    coffeeRate = coffeeRate,
                                    date = Timestamp(calendar.time))
                            listJourneysViewModel.addJourney(newJourney)
                            navigationActions.goBack()
                            return@uploadPicture
                          } catch (_: NumberFormatException) {}
                          Toast.makeText(
                                  context,
                                  "Invalid format, date must be DD/MM/YYYY.",
                                  Toast.LENGTH_SHORT)
                              .show()
                        }
                      }
                    } else {
                      Toast.makeText(context, "Please select an image", Toast.LENGTH_SHORT).show()
                    }
                  },
                  modifier = Modifier.fillMaxWidth().testTag("journeySave")) {
                    Text("Save")
                  }
            }
      })
}
