package com.android.brewr.ui.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Preview
@Composable
fun SimpleMap() {
    // Create a CameraPositionState to control the camera position
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(37.7749, -122.4194), 10f) // San Francisco
    }
    // Display the Google Map
    GoogleMap(modifier = Modifier.fillMaxSize(), cameraPositionState = cameraPositionState)
}
