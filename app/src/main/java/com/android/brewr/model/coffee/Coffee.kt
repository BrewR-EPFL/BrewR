package com.android.brewr.model.coffee

import com.android.brewr.model.location.Location

data class Coffee(
    val coffeeShopName: String,
    val location: Location,
    val rating: Float,
    val hours: Hours,
    val reviews: List<Review>,
    val imagesUrls: List<String>
)

data class Hours(val open: String, val close: String)
