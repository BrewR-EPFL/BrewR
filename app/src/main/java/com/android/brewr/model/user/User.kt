package com.android.brewr.model.user

import com.android.brewr.model.coffee.Coffee

data class User(
    val uid: String,
    val name: String,
    val journeys: List<String> = emptyList(),
    val favoriteList: List<Coffee> = emptyList()
)
