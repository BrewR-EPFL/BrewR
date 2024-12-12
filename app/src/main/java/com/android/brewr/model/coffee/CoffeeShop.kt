package com.android.brewr.model.coffee

import com.android.brewr.model.location.Location

data class CoffeeShop(
    val id: String,
    val coffeeShopName: String,
    val location: Location,
    val rating: Double,
    val hours: List<Hours>,
    val reviews: List<Review>?,
    val imagesUrls: List<String>
)

data class Hours(val day: String, val open: String, val close: String)
