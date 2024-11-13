package com.android.brewr.model.coffee

import android.content.Context
import com.android.brewr.utils.fetchNearbyCoffeeShops
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope

// Function to fetch and sort coffee shops by rating
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
