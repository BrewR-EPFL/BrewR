package com.android.brewr.ui.overview

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.android.brewr.model.journey.ListJourneysViewModel
import com.android.brewr.ui.navigation.NavigationActions
import com.android.brewr.ui.navigation.Screen
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JourneyRecordScreen(
    listJourneysViewModel: ListJourneysViewModel = viewModel(factory = ListJourneysViewModel.Factory),
    navigationActions: NavigationActions
) {
    val journey = listJourneysViewModel.selectedJourney.collectAsState().value
    var showDeleteDialog by remember { mutableStateOf(false) } // State to control dialog visibility

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Journey Record", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navigationActions.goBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    // Delete Button at the top-right corner with text and icon
                    TextButton(onClick = { showDeleteDialog = true }) {
                        Text(text = "Delete", color = Color.Red)
                        Spacer(Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Delete Journey",
                            tint = Color.Red
                        )
                    }
                },
                modifier = Modifier.testTag("journeyRecordScreen")
            )
        },
        floatingActionButton = {
            // Floating action button for edit with text and icon
            FloatingActionButton(
                onClick = { navigationActions.navigateTo(Screen.EDIT_JOURNEY) },
                shape = RoundedCornerShape(50),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Edit",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit Journey",
                        tint = Color.White
                    )
                }
            }
        }
        ,
        content = { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    // Coffee Shop Name
                    Text(
                        text = "Coffee Shop: ${journey?.coffeeShopName}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.testTag("coffeeShopName")
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))

                    // Image placeholder or uploaded image
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .border(1.dp, Color.Gray)
                    ) {
                        if (journey?.imageUrl?.isNotEmpty() == true) {
                            Image(
                                painter = rememberAsyncImagePainter(
                                    ImageRequest.Builder(LocalContext.current)
                                        .data(journey.imageUrl) // Load the image from the URL
                                        .apply {
                                            crossfade(true)
                                        }
                                        .build()
                                ),
                                contentDescription = "Uploaded Image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .align(Alignment.Center)
                            )
                        } else {
                            Text("No photo added.", modifier = Modifier.align(Alignment.Center), color = Color.Gray)
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Description
                    Text(
                        text = "Description: \n${journey?.description}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.testTag("description")
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))

                    // Coffee Origin
                    Text(
                        text = "Origin: \n${journey?.coffeeOrigin?.name}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.testTag("origin")
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))

                    // Brewing Method
                    Text(
                        text = "Brewing Method: \n${journey?.brewingMethod?.name}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.testTag("brewingMethod")
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))

                    // Taste
                    Text(
                        text = "Taste: \n${journey?.coffeeTaste?.name}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.testTag("taste")
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))

                    // Coffee Rating
                    Text(
                        text = "Rating: \n${journey?.coffeeRate?.name}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.testTag("rating")
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))

                    // Date
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val formattedDate = journey?.date?.toDate()?.let { dateFormat.format(it) } ?: "Unknown Date"
                    Text(
                        text = "Date: \n$formattedDate",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.testTag("date")
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))

                    // Location
                    if (journey?.location?.isNotEmpty() == true) {
                        Text(
                            text = "Location: \n${journey.location}",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.testTag("location")
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
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
                            onClick = {
                                deleteJourney(journey?.uid, journey?.imageUrl, navigationActions, listJourneysViewModel)
                                showDeleteDialog = false // Close the dialog
                            }
                        ) {
                            Text("Yes, Delete")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { showDeleteDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    )
}



// Function to delete the journey and its image
fun deleteJourney(
    journeyId: String?,
    imageUrl: String?,
    navigationActions: NavigationActions,
    listJourneysViewModel: ListJourneysViewModel // Pass the view model as a parameter
) {
    if (journeyId != null && imageUrl != null) {
        // Delete the image from Firebase Storage
        deletePicture(imageUrl) {
            // Once the image is deleted, delete the journey record from Firestore
            deleteJourneyFromDatabase(journeyId) {
                // Refresh the journeys and navigate back when both deletions are complete
                listJourneysViewModel.getJourneys()  // Call getJourneys to refresh the list
                navigationActions.goBack()
            }
        }
    }
}




// Function to delete the image from Firebase Storage
fun deletePicture(imageUrl: String, onSuccess: () -> Unit) {
    val storagePath = "images/${imageUrl.substringAfter("%2F").substringBefore("?alt")}"
    val storageRef = FirebaseStorage.getInstance().getReference()
    val imgRefToDelete = storageRef.child(storagePath)

    imgRefToDelete.delete().addOnSuccessListener {
        // Call onSuccess once the image is deleted
        onSuccess()
    }.addOnFailureListener {
        // Log error if the deletion fails
        it.printStackTrace()
    }
}


// Function to delete the journey record from Firestore
fun deleteJourneyFromDatabase(journeyId: String, onSuccess: () -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("journeys").document(journeyId)
        .delete()
        .addOnSuccessListener {
            // Call onSuccess once the journey is deleted
            onSuccess()
        }
        .addOnFailureListener { e ->
            // Log error if the journey deletion fails
            e.printStackTrace()
        }
}
