package com.android.brewr.ui.explore

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import com.android.brewr.R
import com.android.brewr.model.coffee.Coffee
import com.android.brewr.model.journey.Journey
import com.android.brewr.model.journey.ListJourneysViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.tasks.await

/**
 * Composable function to display a map screen with coffee shop markers and user location.
 *
 * @param coffees List of Coffee objects representing coffee shops to be displayed on the map.
 * @param listJourneysViewModel ViewModel containing the list of journeys.
 */
@Composable
fun MapScreen(coffees: List<Coffee>, listJourneysViewModel: ListJourneysViewModel) {
  val context = LocalContext.current
  var userLocation by remember { mutableStateOf<LatLng?>(null) }
  val cameraPositionState = rememberCameraPositionState {
    position = CameraPosition.fromLatLngZoom(LatLng(46.5197, 6.6323), 14f)
  }
  LaunchedEffect(Unit) {
    getCurrentLocation(
        context,
        onSuccess = {
          userLocation = it
          cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 14f)
        })
  }

  Scaffold(
      content = { paddingValues ->
        GoogleMap(
            modifier = Modifier.fillMaxSize().padding(paddingValues).testTag("mapScreen"),
            cameraPositionState = cameraPositionState) {
              coffees.forEach { coffee ->
                Log.d(
                    "MapScreen",
                    "Adding marker for ${coffee.location.name} at (${coffee.location.latitude}, ${coffee.location.longitude})")
                val markerIcon = getMarkerIcon(coffee, listJourneysViewModel)
                Marker(
                    state =
                        remember {
                          MarkerState(
                              position =
                                  LatLng(coffee.location.latitude!!, coffee.location.longitude!!))
                        },
                    title = coffee.coffeeShopName,
                    icon = markerIcon,
                    snippet = "Address: ${coffee.location.name}")
              }

              userLocation?.let {
                Log.d(
                    "MapScreen", "Adding user location marker at (${it.latitude}, ${it.longitude})")
                Marker(state = MarkerState(position = it), title = "Current Location")
              }
            }
      })
}

/**
 * Retrieves the current location of the user.
 *
 * @param context The context used to access the location services.
 * @param onSuccess A callback function to be invoked with the user's current location as a LatLng
 *   object.
 */
@SuppressLint("MissingPermission")
suspend fun getCurrentLocation(context: Context, onSuccess: (LatLng) -> Unit) {
  try {
    val locationClient = LocationServices.getFusedLocationProviderClient(context)
    val location = locationClient.lastLocation.await()
    if (location != null) {
      onSuccess(LatLng(location.latitude, location.longitude)) // Success case
    } else {
      onSuccess(LatLng(46.5197, 6.6323)) // Fallback case
    }
  } catch (e: Exception) {
    e.printStackTrace()
    onSuccess(LatLng(46.5197, 6.6323))
  }
}

/**
 * Composable function to get the appropriate marker icon for a coffee shop.
 *
 * @param coffee The coffee object containing details about the coffee shop.
 * @param listJourneysViewModel The ViewModel containing the list of journeys.
 * @return A BitmapDescriptor representing the marker icon.
 */
@Composable
fun getMarkerIcon(coffee: Coffee, listJourneysViewModel: ListJourneysViewModel): BitmapDescriptor {
  val context = LocalContext.current

  // Helper function to load and resize the icon
  fun loadAndResizeIcon(resourceId: Int): BitmapDescriptor {
    val originalBitmap = BitmapFactory.decodeResource(context.resources, resourceId)
    val resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, 100, 100, false)
    return BitmapDescriptorFactory.fromBitmap(resizedBitmap)
  }

  return when {
    coffee.coffeeShopName.contains("Starbucks", ignoreCase = true) ->
        loadAndResizeIcon(R.drawable.starbucks_icon)
    coffee.coffeeShopName.contains("McDonald's", ignoreCase = true) ->
        loadAndResizeIcon(R.drawable.mcdonald_icon)
    isJourney(coffee, listJourneysViewModel.journeys.collectAsState().value) ->
        loadAndResizeIcon(R.drawable.journeys_icon)
    else -> loadAndResizeIcon(R.drawable.default_coffee_icon) // Default icon
  }
}

/**
 * Checks if the given coffee shop is part of any journey.
 *
 * @param coffee The coffee object containing details about the coffee shop.
 * @param journeys The list of journeys to check against.
 * @return True if the coffee shop is part of any journey, false otherwise.
 */
fun isJourney(coffee: Coffee, journeys: List<Journey>): Boolean {
  val epsilon = 0.01
  journeys.forEach { journey ->
    if (journey.location.name != "At Home" &&
        kotlin.math.abs(journey.location.latitude?.minus(coffee.location.latitude!!) ?: epsilon) <
            epsilon &&
        kotlin.math.abs(journey.location.longitude?.minus(coffee.location.longitude!!) ?: epsilon) <
            epsilon) {
      return true
    }
  }
  return false
}
