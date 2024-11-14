package com.android.brewr.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.android.brewr.BuildConfig
import com.android.brewr.model.coffee.Coffee
import com.android.brewr.model.coffee.Hours
import com.android.brewr.model.coffee.Review
import com.android.brewr.model.location.Location
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.CircularBounds
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchResolvedPhotoUriRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.SearchNearbyRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private const val LOCATION_PERMISSION_REQUEST_CODE = 1

fun fetchNearbyCoffeeShops(
    scope: CoroutineScope,
    context: Context,
    currentLocation: LatLng,
    radius: Double = 3000.0,
    onSuccess: (List<Coffee>) -> Unit
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
          val coffeeShops = mutableListOf<Coffee>()
          scope.launch {
            response.places.map { place ->
              place.location?.let {
                coffeeShops.add(
                    Coffee(
                        id = place.id ?: "Undefined",
                        coffeeShopName = place.displayName ?: "Undefined",
                        location =
                            Location(
                                it.latitude, it.longitude, place.formattedAddress ?: "Undefined"),
                        rating = place.rating ?: 0.0,
                        hours = getHours(place.openingHours?.weekdayText),
                        reviews =
                            place.reviews?.map { review ->
                              Review(
                                  authorName = review.authorAttribution.name,
                                  review = review.text ?: "Undefined",
                                  rating = review.rating)
                            },
                        imagesUrls = fetchAllPhotoUris(place, placesClient)))
              }
            }
            if (coffeeShops.isNotEmpty()) {
              Log.d("PlacesAPI", "Coffee shops founded: ${coffeeShops.size}")
            } else {
              Log.d("PlacesAPI", "No coffee shops found.")
            }
            onSuccess(coffeeShops)
          }
        }
        .addOnFailureListener { exception ->
          Log.e("PlacesAPI", "Place not found: ${exception.message}")
        }
  } else {
    return
  }
}

private suspend fun fetchAllPhotoUris(place: Place, placesClient: PlacesClient): List<String> {
  return place.photoMetadatas?.map { metadata ->
    val photoUriRequest =
        FetchResolvedPhotoUriRequest.builder(metadata).setMaxWidth(500).setMaxHeight(300).build()

    // Fetch the URI and wait for the result
    placesClient.fetchResolvedPhotoUri(photoUriRequest).await()?.uri.toString()
  } ?: emptyList() // If no photo metadata, return an empty list
}

private fun getHours(weekdayText: List<String>?): List<Hours> {
  val listHour =
      weekdayText?.map { dayText ->
        // Split by colon to separate the day name from the time range
        val (_, timeRange) = dayText.split(": ", limit = 2)
        // Split the time range by "–" to get the opening and closing times

        val (openTime, closeTime) =
            if (timeRange == "Closed" || "–" !in timeRange) {
              "Undefined" to "Undefined"
            } else {
              timeRange.split("–").let { it[0].trim() to it.getOrElse(1) { "Undefined" }.trim() }
            }
        // Return the Hours object with the parsed values
        Hours(openTime.trim(), closeTime.trim())
      } ?: emptyList()
  return listHour.ifEmpty { listOf(Hours("Undefined", "Undefined")) }
}
