package com.android.brewr.model.coffee

import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Sorts a list of coffee shops by their rating in descending order.
 *
 * @param coffeeShopShops A [List] of [CoffeeShop] objects representing coffee shops to be sorted.
 * @return A [List] of [CoffeeShop] objects sorted by rating in descending order.
 */
fun sortCoffeeShopsByRating(coffeeShopShops: List<CoffeeShop>): List<CoffeeShop> {
  return coffeeShopShops.sortedByDescending { it.rating }
}

/**
 * Filters a list of coffee shops to include only those that are currently open based on their
 * opening and closing hours.
 *
 * @param coffeeShopShops A [List] of [CoffeeShop] objects representing coffee shops. Each
 *   [CoffeeShop] object contains details such as its name, location, and a list of [Hours] objects.
 *   The [Hours] objects specify the opening and closing times of the coffee shop.
 * @return A [List] of [CoffeeShop] objects that are currently open. Coffee shops with invalid or
 *   missing time information are excluded.
 */
fun filterOpenCoffeeShops(coffeeShopShops: List<CoffeeShop>): List<CoffeeShop> {
  val currentTime = LocalTime.now() // Bring Current time
  return coffeeShopShops.filter { coffee ->
    coffee.hours.any { hour ->
      try {
        val openTime = LocalTime.parse(hour.open, DateTimeFormatter.ofPattern("h:mm a"))
        val closeTime = LocalTime.parse(hour.close, DateTimeFormatter.ofPattern("h:mm a"))
        currentTime.isAfter(openTime) && currentTime.isBefore(closeTime)
      } catch (e: Exception) {
        println("Invalid time for Coffee Shop: ${coffee.coffeeShopName}, Error: ${e.message}")
        false
      }
    }
  }
}
