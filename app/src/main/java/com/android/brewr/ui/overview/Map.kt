package com.android.brewr.ui.overview

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.core.content.ContextCompat
import com.android.brewr.model.coffee.Coffee
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.tasks.await

@Composable
fun MapScreen(coffees: List<Coffee>) {
  val context = LocalContext.current
  var userLocation by remember { mutableStateOf<LatLng?>(null) }

  LaunchedEffect(Unit) {
      userLocation = getCurrentLocation(context)
  }

  Scaffold(
      content = { paddingValues ->
        val cameraPositionState = rememberCameraPositionState {
          position = CameraPosition.fromLatLngZoom(userLocation ?: LatLng(46.5197, 6.6323), 14f)
        }

        GoogleMap(
            modifier = Modifier.fillMaxSize().padding(paddingValues).testTag("mapScreen"),
            cameraPositionState = cameraPositionState) {
              coffees.forEach { coffee ->
                Log.d(
                    "MapScreen",
                    "Adding marker for ${coffee.location.address} at (${coffee.location.latitude}, ${coffee.location.longitude})")
                Marker(
                    state =
                        remember {
                          MarkerState(
                              position =
                                  LatLng(coffee.location.latitude, coffee.location.longitude))
                        },
                    title = coffee.coffeeShopName,
                    snippet = "Address: ${coffee.location.address}")
              }

              userLocation?.let {
                Log.d(
                    "MapScreen", "Adding user location marker at (${it.latitude}, ${it.longitude})")
                Marker(state = MarkerState(position = it), title = "User Location")
              }
            }
      })
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
