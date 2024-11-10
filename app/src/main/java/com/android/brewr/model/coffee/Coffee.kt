package com.android.brewr.model.coffee

import com.android.brewr.model.location.Location

data class Coffee(
    val location: Location,
    val imageUrl: String,
    val hours: Hours,
    val about: String
)

data class Hours(val open: String, val close: String)
