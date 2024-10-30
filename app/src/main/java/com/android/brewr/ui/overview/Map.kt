package com.android.brewr.ui.overview

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.android.brewr.model.journey.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.tasks.await

@Composable
fun MapScreen(
    listLocations: List<Location>
) {
    val context = LocalContext.current
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    var permissionGranted by remember { mutableStateOf(false) }

    // Permission launcher to request location access if not granted
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            permissionGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        }
    )

    // Check and request permissions
    LaunchedEffect(Unit) {
        permissionGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!permissionGranted) {
            locationPermissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    // Fetch the user's location if permission is granted
    LaunchedEffect(permissionGranted) {
        if (permissionGranted) {
            userLocation = getCurrentLocation(context)
        }
    }

    Scaffold(
        content = { paddingValues ->
            // Use userLocation if available, otherwise fallback to San Francisco
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(
                    userLocation ?: LatLng(37.7749, -122.4194), // Default fallback
                    10f
                )
            }

            GoogleMap(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                cameraPositionState = cameraPositionState
            ) {
                listLocations.forEach { location ->
                    Marker(
                        state = MarkerState(
                            position = LatLng(location.latitude, location.longitude)
                        ),
                        title = "Location Marker",
                        snippet = "Lat: ${location.latitude}, Lng: ${location.longitude}"
                    )
                }

                // Add a marker for the user's location if available
                userLocation?.let {
                    Marker(
                        state = MarkerState(position = it),
                        title = "You are here",
                        snippet = "Current location"
                    )
                }
            }
        }
    )
}

// Function to get the current location
@SuppressLint("MissingPermission")
private suspend fun getCurrentLocation(context: Context): LatLng? {
    return try {
        val locationClient = LocationServices.getFusedLocationProviderClient(context)
        val location = locationClient.lastLocation.await()
        location?.let {
            LatLng(it.latitude, it.longitude)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
