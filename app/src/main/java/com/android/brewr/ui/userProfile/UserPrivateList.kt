package com.android.brewr.ui.userProfile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.brewr.model.coffee.Coffee
import com.android.brewr.model.coffee.CoffeesViewModel
import com.android.brewr.model.coffee.Hours
import com.android.brewr.model.coffee.Review
import com.android.brewr.model.location.Location
import com.android.brewr.ui.explore.CoffeeList
import com.android.brewr.ui.navigation.NavigationActions
import com.android.brewr.ui.navigation.Screen

/**
 * Displays the user's private coffee list screen.
 *
 * This composable is responsible for showing a list of private coffee entries associated with the
 * user. It interacts with the provided `CoffeesViewModel` to fetch and manage the data. Navigation
 * to other screens is handled via the `navigationActions` parameter.
 *
 * @param navigationActions Handles navigation events to other screens.
 * @param coffeesViewModel Provides data and business logic related to private coffee entries.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun UserPrivateListScreen(
    navigationActions: NavigationActions,
    coffeesViewModel: CoffeesViewModel
) {
  var showPrivateCoffeeInfos by remember { mutableStateOf(false) }

  val mockCoffee =
      Coffee(
          "1",
          coffeeShopName = "Café tranquille",
          Location(
              latitude = 48.87847905807652,
              longitude = 2.3562626423266946,
              address = "147 Rue du Faubourg Saint-Denis, 75010 Paris, France"),
          rating = 4.9,
          hours =
              listOf(
                  Hours("Monday", open = "8:00 AM", close = "5:00 PM"),
                  Hours("Tuesday", open = "8:00 AM", close = "5:00 PM"),
                  Hours("Wednesday", open = "8:00 AM", close = "5:00 PM"),
                  Hours("Thursday", open = "8:00 AM", close = "5:00 PM"),
                  Hours("Friday", open = "8:00 AM", close = "5:00 PM"),
                  Hours("Saturday", open = "8:00 AM", close = "5:00 PM"),
                  Hours("Sunday", open = "8:00 AM", close = "5:00 PM")),
          reviews =
              listOf(
                  Review("Pablo", "Best coffee in the 10th arrondissement of Paris", 5.0),
                  Review("Thomas", "The staff is super friendly. Love their cappuccino!", 4.9),
                  Review("Claire", "Great spot to catch up with friends over a latte.", 4.8),
                  Review("Nicolas", "Delicious coffee, but seating is a bit limited.", 4.3),
                  Review("Alice", "Quiet and cozy, perfect for working in the morning.", 4.5),
                  Review("Camille", "Would come back just for the flat white!", 4.6)),
          imagesUrls =
              listOf(
                  "https://firebasestorage.googleapis.com/v0/b/brewr-epfl.appspot.com/o/images%2F2023-09-29.jpg?alt=media&token=eaaa9dbf-f402-4d12-b5ac-7c5589231a35"))
  // To be changed later with a get user private list function
  val privateList = listOf(mockCoffee)

  coffeesViewModel.addCoffees(privateList)
  Scaffold(
      modifier = Modifier.fillMaxSize().testTag("UserPrivateListScreen"),
      topBar = {
        TopAppBar(
            title = { Text("User private list") },
            modifier = Modifier.testTag("topBar"),
            navigationIcon = {
              IconButton(
                  onClick = { navigationActions.goBack() },
                  modifier = Modifier.testTag("goBackButton")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back")
                  }
            })
      },
      content = { paddingValues ->
        Column(
            modifier =
                Modifier.fillMaxSize().testTag("privateList").padding(16.dp).padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
              if (!showPrivateCoffeeInfos) {
                CoffeeList(listOf(mockCoffee)) {
                  coffeesViewModel.selectCoffee(it)
                  showPrivateCoffeeInfos = true
                }
              } else {
                navigationActions.navigateTo(Screen.USER_PRIVATE_LIST_INFOS)
              }
            }
      })
}
