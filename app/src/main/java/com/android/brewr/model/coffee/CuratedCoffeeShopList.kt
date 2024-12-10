package com.android.brewr.model.coffee

import java.time.LocalTime
import java.time.format.DateTimeFormatter

// Function to fetch and sort coffee shops by rating
/**
 * Sorts a list of coffee shops by their rating in descending order.
 *
 * @param coffeeShops A [List] of [Coffee] objects representing coffee shops to be sorted.
 * @return A [List] of [Coffee] objects sorted by rating in descending order.
 */
fun sortCoffeeShopsByRating(coffeeShops: List<Coffee>): List<Coffee> {
  return coffeeShops.sortedByDescending { it.rating }
}

/**
 * Filters a list of coffee shops to include only those that are currently open based on their
 * opening and closing hours.
 *
 * @param coffeeShops A [List] of [Coffee] objects representing coffee shops. Each [Coffee] object
 *   contains details such as its name, location, and a list of [Hours] objects. The [Hours] objects
 *   specify the opening and closing times of the coffee shop.
 * @return A [List] of [Coffee] objects that are currently open. Coffee shops with invalid or
 *   missing time information are excluded.
 */
fun filterOpenCoffeeShops(coffeeShops: List<Coffee>): List<Coffee> {
  val currentTime = LocalTime.now() // Bring Current time
  return coffeeShops.filter { coffee ->
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
