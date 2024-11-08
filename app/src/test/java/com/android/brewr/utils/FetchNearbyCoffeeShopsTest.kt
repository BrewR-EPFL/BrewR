package com.android.brewr.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.OpeningHours
import com.google.android.libraries.places.api.model.PhotoMetadata
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.Review
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.SearchNearbyResponse
import io.mockk.*
import org.json.JSONObject
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

const val LOCATION_PERMISSION_REQUEST_CODE = 1

class FetchNearbyCoffeeShopsTest {

  private lateinit var context: Context
  private lateinit var placesClient: PlacesClient
  private val currentLocation = LatLng(37.7749, -122.4194)
  private val radius = 3000.0

  @Before
  fun setUp() {
    context = mockk<Activity>(relaxed = true)
    placesClient = mockk()
    mockkStatic(Places::class)
    mockkStatic(Log::class)
    every { Places.isInitialized() } returns true
    every { Places.initialize(any(), any()) } just Runs
    every { Places.createClient(any()) } returns placesClient
  }

  @Test
  fun fetchNearbyCoffeeShops_succeeds() {
    // Create a mock context and PlacesClient
    val task: Task<SearchNearbyResponse> = mockk()

    // Mock PlacesClient to return the mock task
    every { placesClient.searchNearby(any()) } returns task

    // Mock the Places API response (successful scenario with places)
    val searchNearbyResponse: SearchNearbyResponse = mockk()
    val place1: Place = mockk()
    every { place1.id } returns "1"
    every { place1.displayName } returns "Coffee Shop 1"
    every { place1.formattedAddress } returns "123 Coffee St"
    every { place1.location } returns currentLocation
    every { place1.rating } returns 4.5
    val mockOpeningHours = mockk<OpeningHours>()
    every { place1.openingHours } returns mockOpeningHours
    val mockReview = mockk<Review>()
    every { mockReview.authorAttribution.name } returns "John Doe"
    every { mockReview.text } returns "Great coffee shop!"
    every { mockReview.rating } returns 5.0
    every { place1.reviews } returns listOf(mockReview)
    val mockPhotoMetadata = mockk<PhotoMetadata>()
    every { place1.photoMetadatas } returns listOf(mockPhotoMetadata)
    val places = listOf(place1)
    every { searchNearbyResponse.places } returns places

    // Capture the success callback for the task
    val successSlot = slot<OnSuccessListener<SearchNearbyResponse>>()
    every { task.addOnSuccessListener(capture(successSlot)) } returns task

    // Capture the failure callback
    val failureSlot = slot<OnFailureListener>()
    every { task.addOnFailureListener(capture(failureSlot)) } returns task

    // Mock permission granted for location
    every {
      ContextCompat.checkSelfPermission(any(), Manifest.permission.ACCESS_FINE_LOCATION)
    } returns PackageManager.PERMISSION_GRANTED
    every { ActivityCompat.requestPermissions(any<Activity>(), any(), any()) } answers {}

    // Create a mock LatLng for the current location
    val currentLocation = LatLng(40.7128, -74.0060)

    var result = emptyList<JSONObject>()

    // Call the function to fetch coffee shops
    fetchNearbyCoffeeShops(
        context = context, currentLocation = currentLocation, onSuccess = { result = it })
    // Simulate the success callback being triggered
    successSlot.captured.onSuccess(searchNearbyResponse)

    assertTrue(result.isNotEmpty()) // Ensure results are not empty
  }

  @Test
  fun fetchNearbyCoffeeShops_request_permission() {
    mockkStatic(ContextCompat::class)
    mockkStatic(ActivityCompat::class)

    every {
      ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
    } returns PackageManager.PERMISSION_DENIED
    every { ActivityCompat.requestPermissions(any<Activity>(), any(), any()) } just Runs

    fetchNearbyCoffeeShops(context, currentLocation, radius) {}

    verify {
      ActivityCompat.requestPermissions(
          context as Activity,
          arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
          LOCATION_PERMISSION_REQUEST_CODE)
    }
  }

  @Test
  fun fetchNearbyCoffeeShops_fails() {
    // Simulate an API failure using Tasks.forException
    val apiException = Exception("API call failed")
    val failedTask =
        Tasks.forException<SearchNearbyResponse>(apiException) as Task<SearchNearbyResponse>

    // Mock the PlacesClient to return the failed task
    every { placesClient.searchNearby(any()) } returns failedTask

    var result = emptyList<JSONObject>()

    // Execute the method
    fetchNearbyCoffeeShops(context, currentLocation, radius) { response -> result = response }
    // Verify that result is empty as the API call failed
    assertTrue(result.isEmpty())
  }
}
