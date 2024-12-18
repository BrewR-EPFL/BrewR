package com.android.brewr.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.android.brewr.BuildConfig
import com.android.brewr.model.coffee.CoffeeShop
import com.android.brewr.model.coffee.Hours
import com.android.brewr.model.coffee.Review
import com.android.brewr.model.map.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.CircularBounds
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchResolvedPhotoUriRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.SearchNearbyRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

fun fetchCoffeeShopsByLocationQuery(
    scope: CoroutineScope,
    context: Context,
    locationQuery: String,
    userLocation: LatLng?,
    onSuccess: (List<CoffeeShop>) -> Unit
) {
  // Initialize Places if not already initialized
  if (!Places.isInitialized()) {
    val apiKey = BuildConfig.MAPS_API_KEY
    Places.initialize(context, apiKey)
  }
  val placesClient: PlacesClient = Places.createClient(context)

  val predictionRequest =
      FindAutocompletePredictionsRequest.builder()
          .setQuery(locationQuery)
          .apply { userLocation?.let { setOrigin(it) } }
          .build()

  scope.launch {
    try {
      val predictionResponse = placesClient.findAutocompletePredictions(predictionRequest).await()
      val predictions = predictionResponse.autocompletePredictions

      if (predictions.isEmpty()) {
        Log.d("PlacesAPI", "No coffee shops found for query: $locationQuery")
        onSuccess(emptyList())
        return@launch
      }

      // We'll fetch details for each prediction
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

      val coffeeShops = mutableListOf<CoffeeShop>()

      for (prediction in predictions) {
        val placeId = prediction.placeId ?: continue
        val fetchPlaceRequest = FetchPlaceRequest.builder(placeId, placeFields).build()
        val placeResponse = placesClient.fetchPlace(fetchPlaceRequest).await()
        val place = placeResponse.place

        place.location?.let { loc ->
          coffeeShops.add(
              CoffeeShop(
                  id = place.id ?: "Undefined",
                  coffeeShopName = place.displayName ?: "Undefined",
                  location =
                      Location(
                          latitude = loc.latitude,
                          longitude = loc.longitude,
                          name = place.formattedAddress ?: "Undefined"),
                  rating = place.rating ?: 0.0,
                  hours = getHours(place.openingHours?.weekdayText),
                  reviews =
                      place.reviews
                          ?.map { review ->
                            Review(
                                authorName = review.authorAttribution.name,
                                review = review.text ?: "Undefined",
                                rating = review.rating)
                          }
                          .orEmpty(),
                  imagesUrls = listOf("")
                  // If desired: imagesUrls = fetchAllPhotoUris(place, placesClient)
                  ))
        }
      }

      if (coffeeShops.isNotEmpty()) {
        Log.d("PlacesAPI", "Found ${coffeeShops.size} coffee shops for query $locationQuery")
      } else {
        Log.d("PlacesAPI", "No coffee shops found for query $locationQuery")
      }
      onSuccess(coffeeShops)
    } catch (e: Exception) {
      e.printStackTrace()
      onSuccess(emptyList())
    }
  }
}

fun fetchNearbyCoffeeShops(
    scope: CoroutineScope,
    context: Context,
    currentLocation: LatLng,
    radius: Double = 3000.0,
    onSuccess: (List<CoffeeShop>) -> Unit
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
          .setMaxResultCount(1)
          .build()
  // Check if location permissions are granted
  if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
      PackageManager.PERMISSION_GRANTED) {
    try {
      placesClient
          .searchNearby(request)
          .addOnSuccessListener { response ->
            val coffeeShopShops = mutableListOf<CoffeeShop>()
            scope.launch {
              response.places.map { place ->
                place.location?.let {
                  coffeeShopShops.add(
                      CoffeeShop(
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
                          // use this image to avoid using API to fetch photos as it is very
                          // expensive
                          imagesUrls =
                              listOf(
                                  "https://th.bing.com/th/id/OIP.gNiGdodNdn2Bck61_x18dAHaFi?rs=1&pid=ImgDetMain")))
                  // imagesUrls = fetchAllPhotoUris(place, placesClient)))
                }
                if (coffeeShopShops.isNotEmpty()) {
                  Log.d(
                      "PlacesAPI",
                      "Coffee shops founded: ${coffeeShopShops.size} ${coffeeShopShops[0].coffeeShopName}")
                } else {
                  Log.d("PlacesAPI", "No coffee shops found.")
                }
                onSuccess(coffeeShopShops)
              }
            }
          }
          .addOnFailureListener { exception ->
            Log.e("PlacesAPI", "Place not found: ${exception.message}")
          }
    } catch (e: Exception) {
      e.printStackTrace()
    }
  } else {
    return
  }
}

private suspend fun fetchAllPhotoUris(place: Place, placesClient: PlacesClient): List<String> {
  val metadata = place.photoMetadatas?.get(0)
  try {
    metadata?.let {
      val photoUriRequest =
          FetchResolvedPhotoUriRequest.builder(it).setMaxWidth(500).setMaxHeight(300).build()
      // Fetch the URI and wait for the result
      val result = placesClient.fetchResolvedPhotoUri(photoUriRequest).await()?.uri.toString()
      return listOf(result)
    }
    return listOf("https://th.bing.com/th/id/OIP.gNiGdodNdn2Bck61_x18dAHaFi?rs=1&pid=ImgDetMain")
  } catch (e: Exception) {
    e.printStackTrace()
    return listOf("https://th.bing.com/th/id/OIP.gNiGdodNdn2Bck61_x18dAHaFi?rs=1&pid=ImgDetMain")
  }
}

private fun getHours(weekdayText: List<String>?): List<Hours> {
  val listHour =
      weekdayText?.map { dayText ->
        // Split by colon to separate the day name from the time range
        val (day, timeRange) = dayText.split(": ", limit = 2)
        // Split the time range by "–" to get the opening and closing times
        val (openTime, closeTime) =
            if (timeRange == "Closed" || "–" !in timeRange) {
              "Undefined" to "Undefined"
            } else {
              timeRange.split("–").let { it[0].trim() to it.getOrElse(1) { "Undefined" }.trim() }
            }
        // Return the Hours object with the parsed values
        Hours(day, openTime.trim(), closeTime.trim())
      } ?: emptyList()
  return listHour.ifEmpty { listOf(Hours("Undefined", "Undefined", "Undefined")) }
}

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
