package com.android.brewr.model.location

data class Location(
    val name: String,
    val imageUrl: String,
    val hours: Hours,
    val latitude: Double,
    val longitude: Double,
    val about: String
)

data class Hours(val open: String, val close: String)
