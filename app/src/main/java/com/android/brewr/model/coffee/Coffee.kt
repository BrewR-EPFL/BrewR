package com.android.brewr.model.coffee

import com.android.brewr.model.location.Location

data class Coffee(
    val id: String,
    val coffeeShopName: String,
    val location: Location,
    val rating: Double,
    val hours: Hours,
    val reviews: List<Review>?,
    val imagesUrls: List<String>
)

data class Hours(val open: String, val close: String)
