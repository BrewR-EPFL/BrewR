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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.brewr.model.coffee.CoffeesViewModel
import com.android.brewr.model.coffee.FavoriteCoffeeShopsViewModel
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
    coffeesViewModel: CoffeesViewModel,
    favoriteCoffeeShopsViewModel: FavoriteCoffeeShopsViewModel =
        viewModel(factory = FavoriteCoffeeShopsViewModel.Factory)
) {
  var showPrivateCoffeeInfos by remember { mutableStateOf(false) }
  val privateList = favoriteCoffeeShopsViewModel.favoriteCoffees.collectAsState().value

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
                CoffeeList(privateList) {
                  coffeesViewModel.selectCoffee(it)
                  showPrivateCoffeeInfos = true
                }
              } else {
                navigationActions.navigateTo(Screen.USER_PRIVATE_LIST_INFOS)
              }
            }
      })
}
