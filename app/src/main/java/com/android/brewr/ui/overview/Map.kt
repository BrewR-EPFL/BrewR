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
import com.android.brewr.model.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.tasks.await

@Composable
fun MapScreen(listLocations: List<Location>) {
  val context = LocalContext.current
  var userLocation by remember { mutableStateOf<LatLng?>(null) }
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
      userLocation = getCurrentLocation(context)
    }
  }

  Scaffold(
      content = { paddingValues ->
        val cameraPositionState = rememberCameraPositionState {
          position = CameraPosition.fromLatLngZoom(userLocation ?: LatLng(37.7749, -122.4194), 10f)
        }

        GoogleMap(
            modifier =
                Modifier.fillMaxSize()
                    .padding(paddingValues)
                    .testTag("mapScreen"), // Tag for GoogleMap
            cameraPositionState = cameraPositionState) {
              listLocations.forEach { location ->
                Log.d(
                    "MapScreen",
                    "Adding marker for ${location.name} at (${location.latitude}, ${location.longitude})")
                Marker(
                    state =
                        remember {
                          MarkerState(position = LatLng(location.latitude, location.longitude))
                        },
                    title = location.name, // Use name as title for logging purposes
                    snippet = "Lat: ${location.latitude}, Lng: ${location.longitude}")
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
