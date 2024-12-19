package com.android.brewr.ui.overview

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
import com.android.brewr.model.journey.Journey
import com.android.brewr.model.journey.ListJourneysViewModel
import com.android.brewr.ui.navigation.NavigationActions
import com.android.brewr.ui.navigation.Screen
import com.android.brewr.ui.theme.CoffeeBrown
import com.android.brewr.ui.theme.Gold
import com.android.brewr.ui.theme.LightBrown
import com.android.brewr.utils.deletePicture
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * A composable function that displays the journey record screen.
 *
 * @param listJourneysViewModel The ViewModel that provides the journey data.
 * @param navigationActions The navigation actions to handle navigation events.
 */
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
      topBar = { JourneyTopBar({ navigationActions.goBack() }, { showDeleteDialog = true }) },
      floatingActionButton = {
        JourneyFloatingActionButton { navigationActions.navigateTo(Screen.EDIT_JOURNEY) }
      },
      content = { paddingValues ->
        Column(
            modifier =
                Modifier.fillMaxWidth()
                    .padding(paddingValues)
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()), // Add padding to the whole Column
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
              JourneyDetails(journey!!)

              JourneyImage(journey.imageUrl)

              Spacer(modifier = Modifier.height(16.dp))

              JourneyBrewMethodAndTaste(journey)

              JourneyRating(journey)

              JourneyOrigin(journey)

              JourneyDate(journey)

              JourneyDescription(journey)

              if (showDeleteDialog) {
                DeleteConfirmationDialog(
                    onDismiss = { showDeleteDialog = false },
                    onConfirm = {
                      deletePicture(journey.imageUrl) {
                        listJourneysViewModel.deleteJourneyById(journey.uid)
                        navigationActions.goBack()
                      }
                      showDeleteDialog = false
                    })
              }
            }
      })
}

/**
 * A composable function that displays the top app bar for the journey record screen.
 *
 * @param onBack A lambda function to be called when the back action is triggered.
 * @param onDelete A lambda function to be called when the delete action is triggered.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JourneyTopBar(onBack: () -> Unit, onDelete: () -> Unit) {
  TopAppBar(
      title = { Text("Journey Record", fontWeight = FontWeight.Bold) },
      navigationIcon = {
        IconButton(onClick = onBack, modifier = Modifier.testTag("backButton")) {
          Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
        }
      },
      actions = {
        // Delete Button at the top-right corner with text and icon
        TextButton(onClick = onDelete, modifier = Modifier.testTag("deleteButton")) {
          Text(text = "Delete", color = Color.Red)
          Spacer(Modifier.width(4.dp))
          Icon(
              imageVector = Icons.Filled.Delete,
              contentDescription = "Delete Journey",
              tint = Color.Red)
        }
      },
  )
}

/**
 * A composable function that displays a floating action button for editing a journey.
 *
 * @param onEdit A lambda function to be called when the edit action is triggered.
 */
@Composable
fun JourneyFloatingActionButton(onEdit: () -> Unit) {
  FloatingActionButton(
      onClick = onEdit,
      shape = RoundedCornerShape(50),
      containerColor = CoffeeBrown,
      modifier = Modifier.testTag("editButton")) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically) {
              Text(text = "Edit", color = Color.White, style = MaterialTheme.typography.bodyMedium)
              Spacer(modifier = Modifier.width(8.dp))
              Icon(
                  imageVector = Icons.Filled.Edit,
                  contentDescription = "Edit Journey",
                  tint = Color.White)
            }
      }
}

/**
 * A composable function that displays the details of a journey.
 *
 * @param journey The journey object containing the details.
 */
@Composable
fun JourneyDetails(journey: Journey) {
  // Coffee Shop Name
  journey.let {
    val nameAndAddress =
        if (journey.coffeeShop == null) {
          listOf("At Home", "")
        } else {
          listOf(journey.coffeeShop.coffeeShopName, journey.coffeeShop.location.name)
        }

    val coffeeShopName = nameAndAddress[0]
    val coffeeShopAddress = nameAndAddress[1].trim()

    if (coffeeShopName.isNotEmpty()) {
      Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(2.dp) // Space between title and address
          ) {
            Text(
                text = coffeeShopName,
                fontSize = 16.sp, // Adjust the font size for the name
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth().testTag(tag = "coffeeShopName"))
            if (coffeeShopAddress.isNotEmpty()) {
              Text(
                  text = coffeeShopAddress,
                  fontSize = 14.sp, // Adjust the font size for the address
                  modifier = Modifier.fillMaxWidth().testTag(tag = "coffeeShopAddress"))
            }
          }
    }
  }
}

/**
 * A composable function that displays an image from a given URL.
 *
 * @param imageUrl The URL of the image to be displayed.
 */
@Composable
fun JourneyImage(imageUrl: String) {
  Image(
      painter =
          rememberAsyncImagePainter(
              ImageRequest.Builder(LocalContext.current)
                  .data(imageUrl) // Load the image from the URL
                  .apply { crossfade(true) }
                  .build()),
      contentDescription = "Uploaded Image",
      modifier = Modifier.fillMaxWidth().heightIn(min = 150.dp, max = 400.dp),
      contentScale = ContentScale.FillWidth)
}

/**
 * A composable function that displays the brewing method and taste of a journey.
 *
 * @param journey The journey object containing the brewing method and taste.
 */
@Composable
fun JourneyBrewMethodAndTaste(journey: Journey) {
  Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
    JourneyBrewingMethod(journey) // Brewing Method
    JourneyTaste(journey) // Taste
  }
}

/**
 * A composable function that displays the brewing method of a journey.
 *
 * @param journey The journey object containing the brewing method.
 */
@Composable
fun JourneyBrewingMethod(journey: Journey) {
  if (journey.brewingMethod != BrewingMethod.DEFAULT) {
    Button(
        onClick = {},
        enabled = false,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.testTag("brewingMethod"),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
        colors =
            ButtonDefaults.buttonColors(
                disabledContainerColor = LightBrown, disabledContentColor = CoffeeBrown)) {
          Text(journey.brewingMethod.name.replace("_", " "), modifier = Modifier.padding(4.dp))
        }
  }
}

/**
 * A composable function that displays the taste of the coffee in a journey.
 *
 * @param journey The journey object containing the coffee taste.
 */
@Composable
fun JourneyTaste(journey: Journey) {
  if (journey.coffeeTaste != CoffeeTaste.DEFAULT) {
    Button(
        onClick = {},
        enabled = false,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.testTag("coffeeTaste"),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
        colors =
            ButtonDefaults.buttonColors(
                disabledContainerColor = LightBrown, disabledContentColor = CoffeeBrown)) {
          Text(journey.coffeeTaste.name, modifier = Modifier.padding(4.dp))
        }
  }
}

/**
 * A composable function that displays the rating of a journey using stars.
 *
 * @param journey The journey object containing the coffee rating.
 */
@Composable
fun JourneyRating(journey: Journey) {
  if (journey.coffeeRate != CoffeeRate.DEFAULT) {
    Column(modifier = Modifier.fillMaxWidth()) {
      // Map CoffeeRate to the number of stars
      val starCount =
          journey.coffeeRate.ordinal // ordinal gives you 0-based index, so we don't add 1 due to
      // the default parameter
      Row(
          modifier = Modifier.fillMaxWidth().testTag("rateRow") // Add a test tag for testing
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
}

/**
 * A composable function that displays the origin of the coffee in a journey.
 *
 * @param journey The journey object containing the coffee origin.
 */
@Composable
fun JourneyOrigin(journey: Journey) {
  if (journey.coffeeOrigin != CoffeeOrigin.DEFAULT) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
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
}

/**
 * A composable function that displays the date of a journey.
 *
 * @param journey The journey object containing the date.
 */
@Composable
fun JourneyDate(journey: Journey) {
  Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
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
}

/**
 * A composable function that displays the description of a journey.
 *
 * @param journey The journey object containing the description.
 */
@Composable
fun JourneyDescription(journey: Journey) {
  if (journey.description.isNotEmpty()) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(2.dp) // Space between title and buttons
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
}

/**
 * A composable function that displays a confirmation dialog for deleting a journey.
 *
 * @param onDismiss A lambda function to be called when the dialog is dismissed.
 * @param onConfirm A lambda function to be called when the delete action is confirmed.
 */
@Composable
fun DeleteConfirmationDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
  AlertDialog(
      onDismissRequest = onDismiss,
      title = { Text("Delete Journey") },
      text = { Text("Are you sure you want to delete this journey and its image?") },
      confirmButton = {
        Button(modifier = Modifier.testTag("button Yes"), onClick = onConfirm) {
          Text("Yes, Delete")
        }
      },
      modifier = Modifier.testTag("Alter dialog"),
      dismissButton = {
        Button(modifier = Modifier.testTag("button No"), onClick = onDismiss) { Text("Cancel") }
      })
}
