package com.android.brewr.model.journey

import com.android.brewr.model.coffee.CoffeeShop
import com.google.firebase.Timestamp

/**
 * Represents a journey detailing a coffee experience.
 *
 * @property uid A unique identifier for the journey.
 * @property imageUrl The URL of the image associated with the journey.
 * @property description A textual description of the journey.
 * @property coffeeShop The coffee shop associated with the journey, or null if not applicable.
 * @property coffeeOrigin The origin of the coffee beans, represented by a [CoffeeOrigin] object.
 * @property brewingMethod The method used to brew the coffee, represented by a [BrewingMethod]
 *   object.
 * @property coffeeTaste The taste profile of the coffee, represented by a [CoffeeTaste] object.
 * @property coffeeRate A user-provided rating of the coffee experience, represented by a
 *   [CoffeeRate] object.
 * @property date The timestamp when the journey was recorded, represented by a [Timestamp] object.
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
