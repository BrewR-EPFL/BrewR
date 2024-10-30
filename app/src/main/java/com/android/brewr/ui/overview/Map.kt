package com.android.brewr.ui.overview

import android.annotation.SuppressLint
import android.content.Context
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

    // Fetch the user's current location
    LaunchedEffect(Unit) {
        userLocation = getCurrentLocation(context) ?: LatLng(37.7749, -122.4194) // Default fallback
    }

    Scaffold(
        content = { paddingValues ->
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(userLocation ?: LatLng(37.7749, -122.4194), 10f)
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
                        title = "Location Marker", // Replace with any appropriate title if available
                        snippet = "Lat: ${location.latitude}, Lng: ${location.longitude}"
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
