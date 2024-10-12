package com.android.brewr.ui.overview

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.brewr.model.journey.BrewingMethod
import com.android.brewr.model.journey.CoffeeOrigin
import com.android.brewr.model.journey.CoffeeRate
import com.android.brewr.model.journey.CoffeeTaste
import com.android.brewr.model.journey.Journey
import com.android.brewr.model.journey.ListJourneysViewModel
import com.android.brewr.ui.navigation.NavigationActions
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.firestore
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JourneyRecordScreen(
    listJourneysViewModel: ListJourneysViewModel =
        viewModel(factory = ListJourneysViewModel.Factory),
    navigationActions: NavigationActions
) {
  var coffeeShopName by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }
  var origin by remember { mutableStateOf<CoffeeOrigin?>(null) } // No origin by default
  var brewingMethod by remember {
    mutableStateOf<BrewingMethod?>(null)
  } // No brewing method by default
  var taste by remember {
    mutableStateOf<CoffeeTaste?>(null)
  } // No coffee taste description by default
  var coffeeRate by remember { mutableStateOf<CoffeeRate?>(null) } // No coffee rate by default
  var dateAdded by remember {
    mutableStateOf(Calendar.getInstance().time)
  } // Current time by default
  var likes by remember { mutableStateOf(0) } // 0 likes by default
  var liked by remember { mutableStateOf(false) } // when the user clicks on like

  var showError by remember { mutableStateOf(false) }

  Scaffold(
      topBar = {
        TopAppBar(
            title = { Text("Add New Journey") },
            navigationIcon = {
              IconButton(onClick = { navigationActions.goBack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back")
              }
            })
      },
      content = { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp).padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
              // Coffee Shop Name part
              OutlinedTextField(
                  value = coffeeShopName,
                  onValueChange = { coffeeShopName = it },
                  label = { Text("Coffee Shop Name") },
                  modifier = Modifier.fillMaxWidth())

              // Photo placeholder
              Box(
                  modifier =
                      Modifier.fillMaxWidth().height(200.dp).clickable {
                        // TODO : Handle photo click even such as opening gallery
                      },
                  contentAlignment = Alignment.Center) {
                    Text("Photo Added by User")
                  }

              // Description
              OutlinedTextField(
                  value = description,
                  onValueChange = { description = it },
                  label = { Text("Description") },
                  modifier = Modifier.fillMaxWidth().height(100.dp))

              // Coffee Origin
              DropdownMenuField(
                  label = "Origin",
                  options = CoffeeOrigin.values(),
                  selectedOption = origin,
                  onOptionSelected = { origin = it })

              // Brewing Method
              DropdownMenuField(
                  label = "Brewing Method",
                  options = BrewingMethod.values(),
                  selectedOption = brewingMethod,
                  onOptionSelected = { brewingMethod = it })

              // Taste
              DropdownMenuField(
                  label = "Taste",
                  options = CoffeeTaste.values(),
                  selectedOption = taste,
                  onOptionSelected = { taste = it })

              // Coffee Rating
              DropdownMenuField(
                  label = "Rating",
                  options = CoffeeRate.values(),
                  selectedOption = coffeeRate,
                  onOptionSelected = { coffeeRate = it })

              // Date
              Text("Date: ${dateAdded.toString()}")

              Spacer(modifier = Modifier.height(16.dp))

              // Like / Unlike button
              Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      IconButton(
                          onClick = {
                            liked = !liked
                            likes += if (liked) 1 else -1
                          }) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = if (liked) "Unlike" else "Like",
                                tint = if (liked) Color.Red else Color.Gray)
                          }
                      Text("$likes")
                    }

                    // Edit button
                    IconButton(
                        onClick = {
                          // TODO Handle edit action
                        }) {
                          Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                  }

              Spacer(modifier = Modifier.height(16.dp))

              // Save Button
              Button(
                  onClick = {
                    if (origin != null &&
                        brewingMethod != null &&
                        taste != null &&
                        coffeeRate != null) {
                      val newJourney =
                          Journey(
                              uid = listJourneysViewModel.getNewUid(),
                              imageUrl = "", // TODO update with real img url later
                              description = description,
                              coffeeShopName = coffeeShopName,
                              coffeeOrigin = origin!!,
                              brewingMethod = brewingMethod!!,
                              coffeeTaste = taste!!,
                              coffeeRate = coffeeRate!!,
                              date =
                                  Timestamp(
                                      dateAdded), // TODO maybe the user wants to put another date
                                                  // (like a diary)
                              location = "", // TODO put location when we create Location.kt
                              // likes = likes -> Maybe we need to add this in Journey.kt
                          )

                      // Upload the Journey object to Firestore
                      val db = Firebase.firestore
                      db.collection("journeys")
                          .add(
                              newJourney) // Firestore should serialize the data class automatically
                          .addOnSuccessListener { documentReference ->
                            // Navigate back or show a success message
                            navigationActions.goBack()
                          }
                          .addOnFailureListener { e ->
                            // Handle the error
                            showError = true
                          }
                    } else {
                      showError = true // Show error if fields are incomplete
                    }
                  },
                  modifier = Modifier.fillMaxWidth(),
                  enabled =
                      coffeeShopName.isNotBlank() &&
                          description.isNotBlank() &&
                          origin != null &&
                          brewingMethod != null &&
                          taste != null &&
                          coffeeRate != null // So that we demand do the user to make a selection
                  ) {
                    Text("Save")
                  }

              // Display an error message if form in incomplete
              if (showError) {
                Text("Please complete all fields before submitting", color = Color.Red)
              }
            }
      })
}

@Composable
fun <T : Enum<T>> DropdownMenuField(
    label: String,
    options: Array<T>,
    selectedOption: T?,
    onOptionSelected: (T) -> Unit
) {
  var expanded by remember { mutableStateOf(false) }

  // Display placeholder if no option is selected
  val displayText = selectedOption?.toString() ?: "Select $label"

  Box {
    OutlinedTextField(
        value = displayText,
        onValueChange = {},
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth().clickable { expanded = true })
    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
      options.forEach { option ->
        DropdownMenuItem(
            onClick = {
              onOptionSelected(option)
              expanded = false
            },
            text = { Text(option.toString()) })
      }
    }
  }
}
