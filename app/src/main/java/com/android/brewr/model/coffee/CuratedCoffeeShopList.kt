package com.android.brewr.model.coffee

import android.content.Context
import com.android.brewr.utils.fetchNearbyCoffeeShops
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope

// Function to fetch and sort coffee shops by rating
/**
 * Fetches nearby coffee shops from the user's current location, sorts them by rating in descending
 * order, and returns the sorted list.
 *
 * @param scope A [CoroutineScope] used to execute asynchronous operations.
 * @param context A [Context] required to initialize the Google Places API and handle permissions.
 * @param currentLocation A [LatLng] object representing the user's current geographical location.
 * @param radius An optional [Double] specifying the search radius in meters (default is 3000.0
 *   meters).
 * @param onSuccess A callback function to handle the sorted list of [Coffee] objects.
 * @sample Usage:
 * ```
 * fetchAndSortCoffeeShopsByRating(
 *     scope = coroutineScope,
 *     context = this,
 *     currentLocation = LatLng(46.5191, 6.6339), // Example: Lausanne, Switzerland
 *     radius = 5000.0
 * ) { sortedCoffeeShops ->
 *     // Handle the sorted coffee shop list here
 * }
 * ```
 *
 * The function:
 * - Fetches coffee shop data using the [fetchNearbyCoffeeShops] function.
 * - Sorts the fetched coffee shops by their rating in descending order.
 * - Passes the sorted list to the provided [onSuccess] callback.
 *
 * Note:
 * - Ensure location permissions are granted before calling this function.
 * - A valid Google Maps API key must be configured in your project.
 */
fun fetchAndSortCoffeeShopsByRating(
    scope: CoroutineScope,
    context: Context,
    currentLocation: LatLng,
    radius: Double = 3000.0,
    onSuccess: (List<Coffee>) -> Unit
) {
  fetchNearbyCoffeeShops(
      scope = scope, context = context, currentLocation = currentLocation, radius = radius) {
          coffeeShops ->
        // Sort the fetched coffee shops by rating in descending order
        val sortedCoffeeShops = coffeeShops.sortedByDescending { it.rating }
        onSuccess(sortedCoffeeShops)
      }
}
