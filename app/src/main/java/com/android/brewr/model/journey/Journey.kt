package com.android.brewr.model.journey

import com.google.firebase.Timestamp

data class Journey(
    val uid: String,
    val imageUrl: String,
    val description: String,
    val coffeeShopName: String,
    val coffeeOrigin: CoffeeOrigin,
    val brewingMethod: BrewingMethod,
    val coffeeTaste: CoffeeTaste,
    val coffeeRate: CoffeeRate,
    val date: Timestamp,
    val location: String, // Change to location once location is implemented
)

/** Enum class representing various coffee origins. */
enum class CoffeeOrigin {
  BRAZIL,
  VIETNAM,
  COLOMBIA,
  INDONESIA,
  HONDURAS,
  ETHIOPIA,
  GUATEMALA,
  COSTA_RICA,
  MEXICO,
  PERU,
  NICARAGUA,
  EL_SALVADOR,
  PANAMA,
  KENYA,
  TANZANIA,
  RWANDA,
  BURUNDI,
  UGANDA,
  MALAWI,
  ZAMBIA,
}

/** Enum class representing various brewing methods. */
enum class BrewingMethod {
  ESPRESSO_MACHINE,
  POUR_OVER,
  FRENCH_PRESS,
  COLD_BREW,
  MOKA_POT
}

/** Enum class representing various taste profiles. */
enum class CoffeeTaste {
  FLORAL,
  CHOCOLATE,
  NUTTY,
  SPICY,
  SWEET,
  FRUITY,
  BITTER
}

/** Enum class representing various coffee rates. */
enum class CoffeeRate {
  ONE,
  ONE_HALF,
  TWO,
  TWO_HALF,
  THREE,
  THREE_HALF,
  FOUR,
  FOUR_HALF,
  FIVE
}
