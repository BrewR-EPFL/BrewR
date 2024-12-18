package com.android.brewr.model.coffee

import com.android.brewr.model.map.Location

/**
 * Represents a coffee shop with detailed information including its name, location, ratings,
 * operating hours, reviews, and associated image URLs.
 *
 * @property id A unique identifier for the coffee shop.
 * @property coffeeShopName The name of the coffee shop.
 * @property location The geographical location of the coffee shop.
 * @property rating The average rating of the coffee shop, represented as a [Double].
 * @property hours A list of [Hours] representing the operating hours of the coffee shop.
 * @property reviews An optional list of [Review] objects containing user reviews.
 * @property imagesUrls A list of image URLs showcasing the coffee shop.
 */
data class Coffee(
    val id: String,
    val coffeeShopName: String,
    val location: Location,
    val rating: Double,
    val hours: List<Hours>,
    val reviews: List<Review>?,
    val imagesUrls: List<String>
)

/**
 * Represents the operating hours of a coffee shop for a specific day.
 *
 * @property day The day of the week (e.g., "Monday", "Tuesday").
 * @property open The opening time in 24-hour format (e.g., "08:00").
 * @property close The closing time in 24-hour format (e.g., "20:00").
 */
data class Hours(val day: String, val open: String, val close: String)
