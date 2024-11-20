package com.android.brewr.ui.overview

import android.net.Uri
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.brewr.model.journey.Journey
import com.android.brewr.model.journey.ListJourneysViewModel
import com.android.brewr.ui.navigation.NavigationActions
import com.android.brewr.ui.theme.CoffeeBrown
import com.android.brewr.utils.updatePicture

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditJourneyScreen(
    listJourneysViewModel: ListJourneysViewModel =
        viewModel(factory = ListJourneysViewModel.Factory),
    navigationActions: NavigationActions
) {
  val task =
      listJourneysViewModel.selectedJourney.collectAsState().value
          ?: return Text(text = "No Journey selected. Should not happen", color = Color.Red)

  val uid = task.uid
  val imageUrl by remember { mutableStateOf(task.imageUrl) }
  var imageUri by remember { mutableStateOf<Uri?>(null) }
  var description by remember { mutableStateOf(task.description) }
  var selectedLocation by remember { mutableStateOf(task.location) }
  var coffeeOrigin by remember { mutableStateOf(task.coffeeOrigin) }
  var brewingMethod by remember { mutableStateOf(task.brewingMethod) }
  var coffeeTaste by remember { mutableStateOf(task.coffeeTaste) }
  var coffeeRate by remember { mutableStateOf(task.coffeeRate) }
  val date by remember { mutableStateOf(task.date) }

  var expanded by remember {
    mutableStateOf(false)
  } // State for the dropdown menu
  var isYesSelected by remember { mutableStateOf(selectedLocation.name !="At home") }
  val getImageLauncher =
      rememberLauncherForActivityResult(
          contract = ActivityResultContracts.GetContent(), onResult = { uri -> imageUri = uri })

  Scaffold(
      modifier = Modifier.testTag("editJourneyScreen"),
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
                    // Box on the left for "Edit Photo"
                    JourneyImageBox(
                        imageUri = imageUri,
                        imageUrl = imageUrl,
                        onImageClick = { getImageLauncher.launch("image/*") },
                        testTag = "editImageBox")

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
                  coffeeshopExpanded = expanded,
                  onSelectedLocationChange = { selectedLocation = it })

              // Coffee Origin Dropdown Menu
              CoffeeOriginDropdownMenu(
                  coffeeOrigin,
                  onCoffeeOriginChange = { coffeeOrigin = it },
              )

              // Brewing Method

              BrewingMethodField(brewingMethod, onBrewingMethodChange = { brewingMethod = it })

              // Taste

              CoffeeTasteField(coffeeTaste, onCoffeeTasteChange = { coffeeTaste = it })

              // Rate
              CoffeeRateField(coffeeRate, onCoffeeRateChange = { coffeeRate = it })

              // Date

              var selectedDate by remember { mutableStateOf(date) }
              DateField(date) { selectedDate = it }

              var finalImageUrl by remember { mutableStateOf(imageUrl) }
              // Save button
              Button(
                  colors = ButtonColors(CoffeeBrown, Color.White, CoffeeBrown, Color.White),
                  onClick = {
                    if (imageUri != null) {
                      updatePicture(imageUri!!, imageUrl) { finalImageUrl = it }
                    }
                    val updatedJourney =
                        Journey(
                            uid = uid,
                            imageUrl = finalImageUrl,
                            description = description,
                            location = selectedLocation,
                            coffeeOrigin = coffeeOrigin,
                            brewingMethod = brewingMethod,
                            coffeeTaste = coffeeTaste,
                            coffeeRate = coffeeRate,
                            date = selectedDate)
                    listJourneysViewModel.updateJourney(updatedJourney)
                    listJourneysViewModel.selectJourney(updatedJourney)
                    navigationActions.goBack()
                  },
                  modifier = Modifier.fillMaxWidth().testTag("journeySave")) {
                    Text("Save")
                  }
            }
      })
}
