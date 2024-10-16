package com.android.brewr.ui.overview

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.google.firebase.Timestamp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JourneyRecordScreen(
    listJourneysViewModel: ListJourneysViewModel = viewModel(factory = ListJourneysViewModel.Factory),
    navigationActions: NavigationActions
) {

    val journey = listJourneysViewModel.selectedJourney.collectAsState().value

//    val journey = Journey(
//        uid = "Ksd22S9M4pD4JswrHefa",
//        imageUrl = "https://firebasestorage.googleapis.com/v0/b/brewr-epfl.appspot.com/o/images%2F448195f9-c8bc-4bdc-a8da-c7691c053b16?alt=media&token=bcc21fec-04d4-4dda-8972-be949c29bd23",
//        description = "Matcha Latte looks like android",
//        coffeeShopName = "",
//        coffeeOrigin = CoffeeOrigin.BRAZIL,
//        brewingMethod = BrewingMethod.ESPRESSO_MACHINE,
//        coffeeTaste = CoffeeTaste.SWEET,
//        coffeeRate = CoffeeRate.FIVE,
//        date = Timestamp.now(),
//        location = "Home"
//    )

    // Display the selected journey details
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Journey Record") },
                navigationIcon = {
                    IconButton(onClick = { navigationActions.goBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                modifier = Modifier.testTag("journeyRecordScreen")
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Display the journey details

                // Coffee Shop Name
                Text(text = "Coffee Shop: ${journey?.coffeeShopName}", style = MaterialTheme.typography.bodyLarge,modifier = Modifier.testTag("coffeeShopName"))

                // Image placeholder
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .border(1.dp, Color.Gray)
                ) {
                    if (journey?.imageUrl?.isNotEmpty() == true) {
                        // Show a placeholder for the image (replace with actual image loading when ready)
                        Text(
                            text = "Image Placeholder",
                            modifier = Modifier.align(Alignment.Center),
                            color = Color.Gray
                        )
                    } else {
                        Text("No photo added.", modifier = Modifier.align(Alignment.Center), color = Color.Gray)
                    }
                }

                // Description
                Text(text = "Description: ${journey?.description}", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.testTag("description"))

                // Coffee Origin
                Text(text = "Origin: ${journey?.coffeeOrigin?.name}", style = MaterialTheme.typography.bodyLarge,modifier = Modifier.testTag("origin"))

                // Brewing Method
                Text(text = "Brewing Method: ${journey?.brewingMethod?.name}", style = MaterialTheme.typography.bodyLarge,modifier = Modifier.testTag("brewingMethod"))

                // Taste
                Text(text = "Taste: ${journey?.coffeeTaste?.name}", style = MaterialTheme.typography.bodyLarge,modifier = Modifier.testTag("taste"))

                // Coffee Rating
                Text(text = "Rating: ${journey?.coffeeRate?.name}", style = MaterialTheme.typography.bodyLarge,modifier = Modifier.testTag("rating"))

                // Date
                Text(text = "Date: ${journey?.date?.toDate()}", style = MaterialTheme.typography.bodyLarge,modifier = Modifier.testTag("date"))

                // Location
                if (journey?.location?.isNotEmpty() == true) {
                    Text(text = "Location: ${journey?.location}", style = MaterialTheme.typography.bodyLarge,modifier = Modifier.testTag("location"))
                }
            }
        }
    )
}


