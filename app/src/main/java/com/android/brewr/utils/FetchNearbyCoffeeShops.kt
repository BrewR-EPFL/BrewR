package com.android.brewr.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.brewr.BuildConfig
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.CircularBounds
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.SearchNearbyRequest
import org.json.JSONArray
import org.json.JSONObject

private const val LOCATION_PERMISSION_REQUEST_CODE = 1

fun fetchNearbyCoffeeShops(
    context: Context,
    currentLocation: LatLng,
    radius: Double = 3000.0,
    onSuccess: (List<JSONObject>) -> Unit
) {
  // Initialize the Places API with the API key if it is not already initialized
  if (!Places.isInitialized()) {
    val apiKey = BuildConfig.MAPS_API_KEY
    Places.initialize(context, apiKey)
  }

  val circle = CircularBounds.newInstance(currentLocation, radius)
  val type = listOf("cafe")
  // Specify the fields we want in the Place API response
  val placeFields =
      listOf(
          Place.Field.ID,
          Place.Field.DISPLAY_NAME,
          Place.Field.FORMATTED_ADDRESS,
          Place.Field.LOCATION,
          Place.Field.OPENING_HOURS,
          Place.Field.REVIEWS,
          Place.Field.RATING,
          Place.Field.PHOTO_METADATAS)
  // Create a PlacesClient instance for accessing the Places API
  val placesClient: PlacesClient = Places.createClient(context)
  // Build the search request with the specified bounds, fields, and types
  val request =
      SearchNearbyRequest.builder(circle, placeFields)
          .setIncludedTypes(type)
          .setMaxResultCount(20)
          .build()
  // Check if location permissions are granted
  if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
      PackageManager.PERMISSION_GRANTED) {
    placesClient
        .searchNearby(request)
        .addOnSuccessListener { response ->
          val coffeeShops =
              response.places.map { place ->
                JSONObject().apply {
                  put("id", place.id)
                  put("name", place.displayName)
                  put("address", place.formattedAddress)
                  put("latitude", place.location?.latitude)
                  put("longitude", place.location?.longitude)
                  put("openingHours", place.openingHours)
                  put("rating", place?.rating)
                  put(
                      "reviews",
                      place.reviews?.map { review ->
                        JSONObject().apply {
                          put("authorName", review.authorAttribution.name)
                          put("text", review.text)
                          put("rating", review.rating)
                        }
                      } ?: JSONArray())
                  put(
                      "photos",
                      place.photoMetadatas?.map { photoMetadata -> photoMetadata } ?: JSONArray())
                }
              }
          if (coffeeShops.isNotEmpty()) {
            Log.d("PlacesAPI", "Coffee shops founded: ${coffeeShops.size}")
          } else {
            Log.d("PlacesAPI", "No coffee shops found.")
          }
          onSuccess(coffeeShops)
        }
        .addOnFailureListener { exception ->
          Log.e("PlacesAPI", "Place not found: ${exception.message}")
        }
  } else {
    ActivityCompat.requestPermissions(
        context as Activity,
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
        LOCATION_PERMISSION_REQUEST_CODE)
    return
  }
}
