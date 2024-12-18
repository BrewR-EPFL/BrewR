package com.android.brewr.model.map

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
)
