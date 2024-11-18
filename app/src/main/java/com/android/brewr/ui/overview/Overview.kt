package com.android.brewr.ui.overview

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.brewr.model.coffee.Coffee
import com.android.brewr.model.journey.ListJourneysViewModel
import com.android.brewr.ui.navigation.NavigationActions
import com.android.brewr.ui.navigation.Screen
import com.android.brewr.ui.theme.CoffeeBrown
import com.android.brewr.ui.theme.LightBrown
import com.android.brewr.utils.fetchNearbyCoffeeShops
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun OverviewScreen(
    listJourneysViewModel: ListJourneysViewModel =
        viewModel(factory = ListJourneysViewModel.Factory),
    navigationActions: NavigationActions
) {
  // State to track whether we're in "Gallery" or "Explore" mode
  var currentSection by remember { mutableStateOf("Gallery") }
  var coffeeShops by remember { mutableStateOf<List<Coffee>>(emptyList()) }

  val coroutineScope = rememberCoroutineScope()
  val context = LocalContext.current
  var permissionGranted by remember { mutableStateOf(false) }

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
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED

    if (!permissionGranted) {
      locationPermissionLauncher.launch(
          arrayOf(
              Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
    }
  }

  LaunchedEffect(permissionGranted) {
    if (permissionGranted) {
      coroutineScope.launch {
        val currentLocation =
            withContext(Dispatchers.IO) { getCurrentLocation(context) } ?: LatLng(46.5197, 6.6323)
        fetchNearbyCoffeeShops(
            coroutineScope, context, currentLocation, onSuccess = { coffeeShops = it })
      }
    }
  }

  Scaffold(
      modifier = Modifier.testTag("overviewScreen"),
      topBar = {
        Column {
          TopAppBar(
              title = { Text(text = "BrewR", modifier = Modifier.testTag("appTitle")) },
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
          Box(
              modifier =
                  Modifier.fillMaxWidth()
                      .height(1.dp)
                      .background(Color.LightGray))
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
          ExploreScreen(coffeeShops, navigationActions)
        }
      })
}

@Composable
fun SubNavigationBar(currentSection: String, onSectionChange: (String) -> Unit) {
  Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp, horizontal = 8.dp)) {
    SubNavigationButton(
        text = "Gallery",
        isSelected = currentSection == "Gallery",
        onClick = { onSectionChange("Gallery") })
    Spacer(modifier = Modifier.width(6.dp))
    SubNavigationButton(
        text = "Explore",
        isSelected = currentSection == "Explore",
        onClick = { onSectionChange("Explore") })
  }
}

@Composable
fun SubNavigationButton(text: String, isSelected: Boolean = false, onClick: () -> Unit = {}) {
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

@SuppressLint("MissingPermission")
private suspend fun getCurrentLocation(context: Context): LatLng? {
  return try {
    val locationClient = LocationServices.getFusedLocationProviderClient(context)
    val location = locationClient.lastLocation.await()
    location?.let { LatLng(it.latitude, it.longitude) }
  } catch (e: Exception) {
    e.printStackTrace()
    null
  }
}
