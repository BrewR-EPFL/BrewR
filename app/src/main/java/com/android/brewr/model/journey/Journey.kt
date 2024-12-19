package com.android.brewr.model.journey

import com.android.brewr.model.coffee.CoffeeShop
import com.google.firebase.Timestamp

/**
 * Represents a review for a coffee shop, including the author's name, the review text, and the
 * rating given by the reviewer.
 *
 * @property authorName The name of the person who wrote the review.
 * @property review A textual description or comment provided by the reviewer.
 * @property rating The numerical rating given by the reviewer, typically on a scale of 1.0 to 5.0.
 */
data class Journey(
    val uid: String,
    val imageUrl: String,
    val description: String,
    val coffeeShop: CoffeeShop?,
    val coffeeOrigin: CoffeeOrigin,
    val brewingMethod: BrewingMethod,
    val coffeeTaste: CoffeeTaste,
    val coffeeRate: CoffeeRate,
    val date: Timestamp
)

/** Enum class representing various coffee origins. */
enum class CoffeeOrigin {
  DEFAULT,
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
  DEFAULT,
  ESPRESSO_MACHINE,
  POUR_OVER,
  FRENCH_PRESS,
  COLD_BREW,
  MOKA_POT
}

/** Enum class representing various taste profiles. */
enum class CoffeeTaste {
  DEFAULT,
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
  DEFAULT,
  ONE,
  TWO,
  THREE,
  FOUR,
  FIVE
}
