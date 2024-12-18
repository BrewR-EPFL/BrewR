package com.android.brewr.model.user

/**
 * Data class representing a user in the BrewR application.
 *
 * @property uid Unique identifier for the user.
 * @property name The name of the user.
 * @property journeys A list of journey IDs associated with the user. Default value is an empty list
 *   if no journeys are available.
 */
data class User(
    val uid: String,
    val name: String,
    val journeys: List<String> = emptyList(),
    val favoriteList: List<Coffee> = emptyList()
)
