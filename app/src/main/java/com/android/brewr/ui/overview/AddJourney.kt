package com.android.brewr.ui.overview

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
import androidx.compose.material3.ButtonColors
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
import com.android.brewr.R
import com.android.brewr.model.journey.BrewingMethod
import com.android.brewr.model.journey.CoffeeOrigin
import com.android.brewr.model.journey.CoffeeRate
import com.android.brewr.model.journey.CoffeeTaste
import com.android.brewr.model.journey.Journey
import com.android.brewr.model.journey.ListJourneysViewModel
import com.android.brewr.model.map.Location
import com.android.brewr.ui.navigation.NavigationActions
import com.android.brewr.ui.theme.CoffeeBrown
import com.android.brewr.utils.isConnectedToInternet
import com.android.brewr.utils.uploadPicture
import com.google.firebase.Timestamp

/**
 * A composable screen that allows the user to add a new journey to the app.
 *
 * This screen collects various journey details, including:
 * - Image upload.
 * - Description.
 * - Location selection (e.g., coffee shop).
 * - Coffee attributes (origin, brewing method, taste, and rating).
 * - Date selection.
 *
 * The journey data is saved locally or uploaded to Firebase Firestore, with image handling managed
 * through Firebase Storage.
 *
 * @param listJourneysViewModel The ViewModel used for managing journey-related data.
 * @param navigationActions Navigation actions to handle screen transitions.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddJourneyScreen(
    listJourneysViewModel: ListJourneysViewModel =
        viewModel(factory = ListJourneysViewModel.Factory),
    navigationActions: NavigationActions,
) {
  val uid = listJourneysViewModel.getNewUid()
  var imageUri by remember { mutableStateOf<Uri?>(null) }
  var description by remember { mutableStateOf("") }
  var selectedLocation by remember { mutableStateOf(Location()) }
  var coffeeOrigin by remember { mutableStateOf(CoffeeOrigin.DEFAULT) }
  var brewingMethod by remember { mutableStateOf(BrewingMethod.DEFAULT) }
  var coffeeTaste by remember { mutableStateOf(CoffeeTaste.DEFAULT) }
  var coffeeRate by remember { mutableStateOf(CoffeeRate.DEFAULT) }
  val date by remember { mutableStateOf(Timestamp.now()) } // Using Firebase Timestamp for now
  val context = LocalContext.current
  var expanded by remember { mutableStateOf(true) } // State for the dropdown menu
  var isYesSelected by remember { mutableStateOf(true) }

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
              selectedLocation.let {
                CoffeeShopCheckRow(
                    isYesSelected = isYesSelected,
                    onCheckChange = {
                      isYesSelected = !isYesSelected
                      expanded = isYesSelected
                    },
                    coffeeshopExpanded = expanded,
                    onSelectedLocationChange = { selectedLocation = it })
              }

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
                  colors = ButtonColors(CoffeeBrown, Color.White, CoffeeBrown, Color.White),
                  onClick = {
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
                                  date = selectedDate)
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
                                date = selectedDate)
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
                                  date = selectedDate)
                          listJourneysViewModel.updateJourney(journeyWithRealImage)
                          return@uploadPicture
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
