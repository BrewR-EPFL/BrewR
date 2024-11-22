package com.android.brewr.model.user

data class User(
    val uid: String,
    val name: String,
    val journeys: List<String> = emptyList(),
)
