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
import com.android.brewr.model.coffee.CoffeeShop
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

@Composable
fun MapScreen(coffeeShops: List<CoffeeShop>) {
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
              coffeeShops.forEach { coffee ->
                Log.d(
                    "MapScreen",
                    "Adding marker for ${coffee.location.name} at (${coffee.location.latitude}, ${coffee.location.longitude})")
                val markerIcon = getMarkerIcon(coffee.coffeeShopName)
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

@SuppressLint("MissingPermission")
private suspend fun getCurrentLocation(context: Context, onSuccess: (LatLng) -> Unit) {
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

@Composable
private fun getMarkerIcon(shopName: String): BitmapDescriptor {
  val context = LocalContext.current

  // Helper function to load and resize the icon
  fun loadAndResizeIcon(resourceId: Int): BitmapDescriptor {
    val originalBitmap = BitmapFactory.decodeResource(context.resources, resourceId)
    val resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, 100, 100, false)
    return BitmapDescriptorFactory.fromBitmap(resizedBitmap)
  }

  return when {
    shopName.contains("Starbucks", ignoreCase = true) ->
        loadAndResizeIcon(R.drawable.starbucks_icon)
    shopName.contains("McDonald's", ignoreCase = true) ->
        loadAndResizeIcon(R.drawable.mcdonald_icon)
    else -> loadAndResizeIcon(R.drawable.default_coffee_icon) // Default icon
  }
}
