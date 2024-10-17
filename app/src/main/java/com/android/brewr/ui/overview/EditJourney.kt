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
import androidx.compose.runtime.collectAsState
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
import com.android.brewr.model.journey.Journey
import com.android.brewr.model.journey.ListJourneysViewModel
import com.android.brewr.ui.navigation.NavigationActions
import com.android.brewr.utils.updatePicture
import com.google.firebase.Timestamp
import java.util.Calendar

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
  var coffeeShopName by remember { mutableStateOf(task.coffeeShopName) }
  var coffeeOrigin by remember { mutableStateOf(task.coffeeOrigin) }
  var brewingMethod by remember { mutableStateOf(task.brewingMethod) }
  var coffeeTaste by remember { mutableStateOf(task.coffeeTaste) }
  var coffeeRate by remember { mutableStateOf(task.coffeeRate) }
  val date by remember {
    mutableStateOf(
        task.date.let {
          val calendar = java.util.GregorianCalendar()
          calendar.time = task.date.toDate()
          return@let "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}/${
                    calendar.get(
                        Calendar.YEAR
                    )
                }"
        })
  }

  val context = LocalContext.current
  var expanded by remember {
    mutableStateOf(coffeeShopName.isNotEmpty())
  } // State for the dropdown menu
  var isYesSelected by remember { mutableStateOf(false) }

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
                  expanded = expanded,
                  coffeeShopName = coffeeShopName,
                  onCoffeeShopNameChange = { coffeeShopName = it })

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
              DateField(selectedDate, onDateChange = { selectedDate = it })

              // Save button

              Button(
                  onClick = {
                    val calendar = GregorianCalendar()
                    val parts = selectedDate.split("/")
                    if (imageUri != null) {
                      updatePicture(imageUri!!, imageUrl) { newImageUrl ->
                        if (parts.size == 3) {
                          try {
                            calendar.set(
                                parts[2].toInt(),
                                parts[1].toInt() - 1, // Months are 0-based
                                parts[0].toInt(),
                                0,
                                0,
                                0)

                            // Create a new journey with the updated image URL
                            val updatedJourney =
                                Journey(
                                    uid = uid,
                                    imageUrl = newImageUrl, // Use the downloaded URL from Firebase
                                    description = description,
                                    coffeeShopName = coffeeShopName,
                                    coffeeOrigin = coffeeOrigin,
                                    brewingMethod = brewingMethod,
                                    coffeeTaste = coffeeTaste,
                                    coffeeRate = coffeeRate,
                                    date = Timestamp(calendar.time))
                            listJourneysViewModel.updateJourney(updatedJourney)
                            navigationActions.goBack()
                          } catch (_: NumberFormatException) {
                            Toast.makeText(
                                    context,
                                    "Invalid format, date must be DD/MM/YYYY.",
                                    Toast.LENGTH_SHORT)
                                .show()
                          }
                        }
                      }
                    } else if (parts.size == 3) {
                      try {
                        calendar.set(
                            parts[2].toInt(),
                            parts[1].toInt() - 1, // Months are 0-based
                            parts[0].toInt(),
                            0,
                            0,
                            0)

                        // Create a new journey without image update
                        val updatedJourney =
                            Journey(
                                uid = uid,
                                imageUrl = imageUrl, // Keep the old URL
                                description = description,
                                coffeeShopName = coffeeShopName,
                                coffeeOrigin = coffeeOrigin,
                                brewingMethod = brewingMethod,
                                coffeeTaste = coffeeTaste,
                                coffeeRate = coffeeRate,
                                date = Timestamp(calendar.time))
                        listJourneysViewModel.updateJourney(updatedJourney)
                        navigationActions.goBack()
                      } catch (_: NumberFormatException) {
                        Toast.makeText(
                                context,
                                "Invalid format, date must be DD/MM/YYYY.",
                                Toast.LENGTH_SHORT)
                            .show()
                      }
                    }
                  },
                  modifier = Modifier.fillMaxWidth().testTag("journeySave")) {
                    Text("Save")
                  }
            }
      })
}
