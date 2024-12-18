package com.android.brewr.ui.overview

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.brewr.R
import com.android.brewr.model.coffee.CoffeeShop
import com.android.brewr.model.coffee.CoffeesViewModel
import com.android.brewr.model.coffee.sortCoffeeShopsByRating
import com.android.brewr.model.journey.ListJourneysViewModel
import com.android.brewr.ui.explore.ExploreScreen
import com.android.brewr.ui.navigation.NavigationActions
import com.android.brewr.ui.navigation.Screen
import com.android.brewr.ui.theme.CoffeeBrown
import com.android.brewr.ui.theme.LightBrown
import com.android.brewr.utils.fetchNearbyCoffeeShops
import com.android.brewr.utils.getCurrentLocation
import kotlinx.coroutines.launch

/**
 * Displays the main overview screen with two sections: Gallery and Explore.
 *
 * The screen fetches nearby coffee shops based on the user's location and displays either the
 * Gallery or Explore section depending on the selected navigation tab.
 *
 * @param listJourneysViewModel The ViewModel for managing journey data.
 * @param coffeesViewModel The ViewModel for managing coffee shop data.
 * @param navigationActions Navigation actions to navigate between screens.
 */
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun OverviewScreen(
    listJourneysViewModel: ListJourneysViewModel =
        viewModel(factory = ListJourneysViewModel.Factory),
    coffeesViewModel: CoffeesViewModel = viewModel(factory = CoffeesViewModel.Factory),
    navigationActions: NavigationActions
) {
  // State to track whether we're in "Gallery" or "Explore" mode
  var currentSection by remember { mutableStateOf("Gallery") }
  var curatedCoffeeShops by rememberSaveable { mutableStateOf<List<CoffeeShop>>(emptyList()) }

  val coroutineScope = rememberCoroutineScope()
  val context = LocalContext.current
  var permissionGranted by remember { mutableStateOf(false) }
  var isFetched by rememberSaveable { mutableStateOf(false) }

  val locationPermissionLauncher =
      rememberLauncherForActivityResult(
          contract = ActivityResultContracts.RequestMultiplePermissions(),
          onResult = { permissions ->
            permissionGranted =
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
          })

  LaunchedEffect(Unit) {
    permissionGranted =
        ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED

    if (!permissionGranted) {
      locationPermissionLauncher.launch(
          arrayOf(
              Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
    }
  }

  LaunchedEffect(permissionGranted) {
    if (permissionGranted && !isFetched) {
      coroutineScope.launch {
        getCurrentLocation(
            context,
            onSuccess = { location ->
              // Fetch coffee shops once
              fetchNearbyCoffeeShops(
                  scope = coroutineScope,
                  context = context,
                  currentLocation = location,
                  onSuccess = { coffees ->
                    coffeesViewModel.addCoffees(coffees)

                    // Sort fetched coffee shops by rating
                    curatedCoffeeShops = sortCoffeeShopsByRating(coffees)
                  })
            })
      }
      isFetched = true
    }
  }

  Scaffold(
      modifier = Modifier.testTag("overviewScreen"),
      topBar = {
        Column {
          TopAppBar(
              title = {
                Image(
                    painter = painterResource(id = R.drawable.app_title),
                    contentDescription = "App Title Logo",
                    modifier = Modifier.testTag("appTitle").fillMaxHeight().testTag("appTitle"))
              },
              actions = {
                Row {
                  IconButton(
                      onClick = { navigationActions.navigateTo(Screen.ADD_JOURNEY) },
                      modifier = Modifier.testTag("addButton")) {
                        Icon(imageVector = Icons.Outlined.Add, contentDescription = "Add")
                      }
                  Spacer(modifier = Modifier.width(16.dp))
                  IconButton(
                      onClick = { navigationActions.navigateTo(Screen.USERPROFILE) },
                      modifier = Modifier.testTag("accountButton")) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Account")
                      }
                }
              })
          Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.LightGray))
          Spacer(modifier = Modifier.height(8.dp))
          SubNavigationBar(
              currentSection = currentSection,
              onSectionChange = { section -> currentSection = section })
        }
      },
      content = { pd ->
        if (currentSection == "Gallery") {
          GalleryScreen(listJourneysViewModel, pd, navigationActions)
        } else {
          ExploreScreen(coffeesViewModel, listJourneysViewModel, curatedCoffeeShops)
        }
      })
}

@Composable
fun SubNavigationBar(currentSection: String, onSectionChange: (String) -> Unit) {
  Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp, horizontal = 8.dp)) {
    SubNavigationButton(
        text = "Gallery",
        isSelected = currentSection == "Gallery",
        onClick = { onSectionChange("Gallery") },
        modifier = Modifier.testTag("Gallery"))
    Spacer(modifier = Modifier.width(6.dp))
    SubNavigationButton(
        text = "Explore",
        isSelected = currentSection == "Explore",
        onClick = { onSectionChange("Explore") },
        modifier = Modifier.testTag("Explore"))
  }
}

/**
 * Displays a navigation button within the sub-navigation bar.
 *
 * @param text The text displayed on the button.
 * @param isSelected Whether the button is currently selected.
 * @param onClick Callback invoked when the button is clicked.
 * @param modifier Modifier for styling and layout configuration.
 */
@Composable
fun SubNavigationButton(
    text: String,
    isSelected: Boolean = false,
    onClick: () -> Unit = {},
    modifier: Modifier
) {
  Text(
      text = text,
      color = if (isSelected) Color.White else CoffeeBrown,
      modifier =
          Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
              .clickable { onClick() }
              .background(if (isSelected) CoffeeBrown else LightBrown, RoundedCornerShape(8.dp))
              .padding(8.dp)
              .testTag(text))
}
