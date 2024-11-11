package com.android.brewr.ui.overview

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.android.brewr.model.journey.BrewingMethod
import com.android.brewr.model.journey.CoffeeOrigin
import com.android.brewr.model.journey.CoffeeRate
import com.android.brewr.model.journey.CoffeeTaste
import com.android.brewr.model.journey.ListJourneysViewModel
import com.android.brewr.ui.navigation.NavigationActions
import com.android.brewr.ui.navigation.Screen
import com.android.brewr.ui.theme.CoffeeBrown
import com.android.brewr.ui.theme.Gold
import com.android.brewr.ui.theme.LightBrown
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JourneyRecordScreen(
    listJourneysViewModel: ListJourneysViewModel =
        viewModel(factory = ListJourneysViewModel.Factory),
    navigationActions: NavigationActions
) {
  val journey = listJourneysViewModel.selectedJourney.collectAsState().value
  var showDeleteDialog by remember { mutableStateOf(false) } // State to control dialog visibility

  Scaffold(
      modifier = Modifier.testTag("journeyRecordScreen"),
      topBar = {
        TopAppBar(
            title = { Text("Journey Record", fontWeight = FontWeight.Bold) },
            navigationIcon = {
              IconButton(
                  onClick = { navigationActions.goBack() },
                  modifier = Modifier.testTag("backButton")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back")
                  }
            },
            actions = {
              // Delete Button at the top-right corner with text and icon
              TextButton(
                  onClick = { showDeleteDialog = true },
                  modifier = Modifier.testTag("deleteButton")) {
                    Text(text = "Delete", color = Color.Red)
                    Spacer(Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete Journey",
                        tint = Color.Red)
                  }
            },
        )
      },
      floatingActionButton = {
        // Floating action button for edit with text and icon
        FloatingActionButton(
            onClick = { navigationActions.navigateTo(Screen.EDIT_JOURNEY) },
            shape = RoundedCornerShape(50),
            containerColor = CoffeeBrown,
            modifier = Modifier.testTag("editButton")) {
              Row(
                  modifier = Modifier.padding(horizontal = 16.dp),
                  verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Edit",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit Journey",
                        tint = Color.White)
                  }
            }
      },
      content = { paddingValues ->
        Column(
            modifier =
                Modifier.fillMaxWidth()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()), // Add padding to the whole Column
            verticalArrangement = Arrangement.spacedBy(8.dp)) {

              // Coffee Shop Name
              if (journey!!.coffeeShopName.isNotEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement =
                        Arrangement.spacedBy(2.dp) // Space between title and buttons
                    ) {
                      Text(
                          text = "CoffeeShop Name",
                          fontSize = 16.sp, // Adjust the font size for the title
                          fontWeight = FontWeight.Bold, // Make the title bold
                      )
                      Text(
                          text = journey.coffeeShopName,
                          fontSize = 14.sp, // Adjust the font size for the title
                          modifier = Modifier.fillMaxWidth().testTag("coffeeShopName"))
                    }
              }

              // Image placeholder or uploaded image
              Image(
                  painter =
                      rememberAsyncImagePainter(
                          ImageRequest.Builder(LocalContext.current)
                              .data(journey.imageUrl) // Load the image from the URL
                              .apply { crossfade(true) }
                              .build()),
                  contentDescription = "Uploaded Image",
                  modifier = Modifier.fillMaxWidth().heightIn(min = 150.dp, max = 400.dp),
                  contentScale = ContentScale.FillWidth)

              Spacer(modifier = Modifier.height(16.dp))

              Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Brewing Method
                    if (journey.brewingMethod != BrewingMethod.DEFAULT) {
                      Button(
                          onClick = {},
                          enabled = false,
                          shape = RoundedCornerShape(16.dp),
                          modifier = Modifier.testTag("brewingMethod"),
                          contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                          colors =
                              ButtonDefaults.buttonColors(
                                  disabledContainerColor = LightBrown,
                                  disabledContentColor = CoffeeBrown)) {
                            Text(
                                journey.brewingMethod.name.replace("_", " "),
                                modifier = Modifier.padding(4.dp))
                          }
                    }
                    // Taste
                    if (journey.coffeeTaste != CoffeeTaste.DEFAULT) {
                      Button(
                          onClick = {},
                          enabled = false,
                          shape = RoundedCornerShape(16.dp),
                          modifier = Modifier.testTag("coffeeTaste"),
                          contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                          colors =
                              ButtonDefaults.buttonColors(
                                  disabledContainerColor = LightBrown,
                                  disabledContentColor = CoffeeBrown)) {
                            Text(journey.coffeeTaste.name, modifier = Modifier.padding(4.dp))
                          }
                    }
                  }
              // Coffee Rating
              if (journey.coffeeRate != CoffeeRate.DEFAULT) {
                Column(modifier = Modifier.fillMaxWidth()) {
                  // Map CoffeeRate to the number of stars
                  val starCount =
                      journey.coffeeRate
                          .ordinal // ordinal gives you 0-based index, so we don't add 1 due to
                  // the default parameter
                  Row(
                      modifier =
                          Modifier.fillMaxWidth().testTag("rateRow") // Add a test tag for testing
                      ) {
                        for (i in 1..5) {
                          if (i <= starCount) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = "Filled Star $i",
                                tint = Gold, // Gold color for filled star
                                modifier = Modifier.size(40.dp).testTag("FilledStar$i"))
                          } else {
                            Icon(
                                imageVector = Icons.Outlined.Star,
                                contentDescription = "Outlined Star $i",
                                tint = Color(0xFF312F2F), // Same gold color for consistency
                                modifier = Modifier.size(40.dp).testTag("OutlinedStar$i"))
                          }
                        }
                      }
                }
              }

              // Coffee Origin
              if (journey.coffeeOrigin != CoffeeOrigin.DEFAULT) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                      Text(
                          text = "Coffee Origin:",
                          fontSize = 16.sp, // Adjust the font size for the title
                          fontWeight = FontWeight.Bold, // Make the title bold
                      )
                      Text(
                          text = journey.coffeeOrigin.name.replace("_", " "),
                          fontSize = 16.sp, // Adjust the font size for the title
                          fontWeight = FontWeight.Bold,
                          modifier = Modifier.fillMaxWidth().testTag("CoffeeOrigin"))
                    }
              }

              Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    // Label Text
                    Text(text = "Date:", fontSize = 16.sp, fontWeight = FontWeight.Bold)

                    // Date Text
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val formattedDate = dateFormat.format(journey.date.toDate())
                    Text(
                        text = formattedDate,
                        fontSize = 16.sp, // Adjust the font size for the title
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth().testTag("date"))
                  }
              // Description
              if (journey.description.isNotEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement =
                        Arrangement.spacedBy(2.dp) // Space between title and buttons
                    ) {
                      Text(
                          text = "Description",
                          fontSize = 16.sp, // Adjust the font size for the title
                          fontWeight = FontWeight.Bold, // Make the title bold
                      )
                      Text(
                          text = journey.description,
                          fontSize = 14.sp, // Adjust the font size for the title
                          modifier = Modifier.fillMaxWidth().testTag("journeyDescription"))
                    }
              }

              // Confirmation dialog for deleting the journey
              if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("Delete Journey") },
                    text = { Text("Are you sure you want to delete this journey and its image?") },
                    confirmButton = {
                      Button(
                          modifier = Modifier.testTag("button Yes"),
                          onClick = {
                            deletePicture(journey.imageUrl) {
                              // Once the image is deleted, delete the journey record from
                              // Firestore
                              listJourneysViewModel.deleteJourneyById(journey.uid)
                              navigationActions.goBack()
                            }
                            showDeleteDialog = false // Close the dialog
                          }) {
                            Text("Yes, Delete")
                          }
                    },
                    modifier = Modifier.testTag("Alter dialog"),
                    dismissButton = {
                      Button(
                          modifier = Modifier.testTag("button No"),
                          onClick = { showDeleteDialog = false }) {
                            Text("Cancel")
                          }
                    })
              }
            }
      })
}

// Function to delete the image from Firebase Storage
fun deletePicture(imageUrl: String, onSuccess: () -> Unit) {
  val storagePath = "images/${imageUrl.substringAfter("%2F").substringBefore("?alt")}"
  val storageRef = FirebaseStorage.getInstance().getReference()
  val imgRefToDelete = storageRef.child(storagePath)

  imgRefToDelete
      .delete()
      .addOnSuccessListener {
        // Call onSuccess once the image is deleted
        onSuccess()
      }
      .addOnFailureListener {
        // Log error if the deletion fails
        Log.e("JourneyRecord", "Error deleting image: $it")
      }
}
