package com.android.brewr.model.map

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Represents a geographic location with latitude, longitude, and an optional name.
 *
 * This class is commonly used to specify locations such as coffee shop addresses, user-defined
 * places, or default fallback locations.
 *
 * @property latitude The latitude coordinate of the location. Default is `0.0` if not specified.
 * @property longitude The longitude coordinate of the location. Default is `0.0` if not specified.
 * @property name The name or label of the location. Default is `"At home"` if not specified.
 */
data class Location(
    val latitude: Double? = 0.0,
    val longitude: Double? = 0.0,
    val name: String = "At home"
) {
  /**
   * Determines if the current location is not "At home".
   *
   * This function checks whether the location's latitude and longitude are non-zero and whether the
   * name of the location is not "At home".
   *
   * @return `true` if the location is not "At home", otherwise `false`.
   */
  fun isNotAtHome(): Boolean {
    return (latitude != 0.0 || longitude != 0.0) && name != "At home"
  }

  /**
   * Calculates the distance between the current location and a given location.
   *
   * This function uses the Haversine formula to calculate the great-circle distance between two
   * geographical points specified by their latitude and longitude.
   *
   * @param journeyLocation The destination location to calculate the distance to.
   * @return The distance between the two locations in meters.
   */
  fun distanceTo(journeyLocation: Location): Double {
    val earthRadius = 6371000.0 // Earth's radius in meters

    // Convert latitude and longitude from degrees to radians
    val lat1 = Math.toRadians(this.latitude ?: 0.0)
    val lon1 = Math.toRadians(this.longitude ?: 0.0)
    val lat2 = Math.toRadians(journeyLocation.latitude ?: 0.0)
    val lon2 = Math.toRadians(journeyLocation.longitude ?: 0.0)

    // Calculate the differences in latitude and longitude
    val deltaLat = lat2 - lat1
    val deltaLon = lon2 - lon1

    // Apply the Haversine formula
    val a = sin(deltaLat / 2).pow(2) + cos(lat1) * cos(lat2) * sin(deltaLon / 2).pow(2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    // Calculate the distance
    return earthRadius * c
  }
}
