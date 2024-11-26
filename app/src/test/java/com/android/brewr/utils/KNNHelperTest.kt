package com.android.brewr.utils

import android.app.Activity
import android.content.Context
import com.android.brewr.model.journey.BrewingMethod
import com.android.brewr.model.journey.CoffeeOrigin
import com.android.brewr.model.journey.CoffeeRate
import com.android.brewr.model.journey.CoffeeTaste
import com.android.brewr.model.journey.Journey
import com.android.brewr.model.map.Location
import com.google.firebase.Timestamp
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlin.math.sqrt
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test

class KNNHelperTest {
  private lateinit var context: Context
  private lateinit var knn: KNNHelper
  private val user =
      Pair(
          listOf(
              Journey(
                  "jid1",
                  "",
                  "",
                  Location(0.0, 0.0, ""),
                  CoffeeOrigin.COSTA_RICA,
                  BrewingMethod.COLD_BREW,
                  CoffeeTaste.FRUITY,
                  CoffeeRate.FIVE,
                  Timestamp.now()),
              Journey(
                  "jid2",
                  "",
                  "",
                  Location(0.0, 0.0, ""),
                  CoffeeOrigin.ETHIOPIA,
                  BrewingMethod.ESPRESSO_MACHINE,
                  CoffeeTaste.CHOCOLATE,
                  CoffeeRate.FOUR,
                  Timestamp.now()),
              Journey(
                  "jid3",
                  "",
                  "",
                  Location(0.0, 0.0, ""),
                  CoffeeOrigin.HONDURAS,
                  BrewingMethod.COLD_BREW,
                  CoffeeTaste.FRUITY,
                  CoffeeRate.FIVE,
                  Timestamp.now()),
              Journey(
                  "jid4",
                  "",
                  "",
                  Location(0.0, 0.0, ""),
                  CoffeeOrigin.COSTA_RICA,
                  BrewingMethod.COLD_BREW,
                  CoffeeTaste.NUTTY,
                  CoffeeRate.FIVE,
                  Timestamp.now()),
              Journey(
                  "jid5",
                  "",
                  "",
                  Location(0.0, 0.0, ""),
                  CoffeeOrigin.KENYA,
                  BrewingMethod.COLD_BREW,
                  CoffeeTaste.BITTER,
                  CoffeeRate.TWO,
                  Timestamp.now())),
          "uid1")
  private val otherUsers =
      listOf(
          Pair(
              listOf(
                  Journey(
                      "jid6",
                      "",
                      "",
                      Location(0.0, 0.0, ""),
                      CoffeeOrigin.BRAZIL,
                      BrewingMethod.FRENCH_PRESS,
                      CoffeeTaste.SWEET,
                      CoffeeRate.THREE,
                      Timestamp.now()),
                  Journey(
                      "jid7",
                      "",
                      "",
                      Location(0.0, 0.0, ""),
                      CoffeeOrigin.COLOMBIA,
                      BrewingMethod.POUR_OVER,
                      CoffeeTaste.FLORAL,
                      CoffeeRate.FOUR,
                      Timestamp.now()),
                  Journey(
                      "jid8",
                      "",
                      "",
                      Location(0.0, 0.0, ""),
                      CoffeeOrigin.GUATEMALA,
                      BrewingMethod.MOKA_POT,
                      CoffeeTaste.SPICY,
                      CoffeeRate.FIVE,
                      Timestamp.now()),
                  Journey(
                      "jid9",
                      "",
                      "",
                      Location(0.0, 0.0, ""),
                      CoffeeOrigin.INDONESIA,
                      BrewingMethod.COLD_BREW,
                      CoffeeTaste.FRUITY,
                      CoffeeRate.ONE,
                      Timestamp.now()),
                  Journey(
                      "jid10",
                      "",
                      "",
                      Location(0.0, 0.0, ""),
                      CoffeeOrigin.UGANDA,
                      BrewingMethod.MOKA_POT,
                      CoffeeTaste.SWEET,
                      CoffeeRate.FOUR,
                      Timestamp.now())),
              "uid2"),
          Pair(
              listOf(
                  Journey(
                      "jid11",
                      "",
                      "",
                      Location(0.0, 0.0, ""),
                      CoffeeOrigin.VIETNAM,
                      BrewingMethod.POUR_OVER,
                      CoffeeTaste.BITTER,
                      CoffeeRate.THREE,
                      Timestamp.now()),
                  Journey(
                      "jid12",
                      "",
                      "",
                      Location(0.0, 0.0, ""),
                      CoffeeOrigin.PANAMA,
                      BrewingMethod.COLD_BREW,
                      CoffeeTaste.FLORAL,
                      CoffeeRate.FIVE,
                      Timestamp.now()),
                  Journey(
                      "jid13",
                      "",
                      "",
                      Location(0.0, 0.0, ""),
                      CoffeeOrigin.PERU,
                      BrewingMethod.ESPRESSO_MACHINE,
                      CoffeeTaste.NUTTY,
                      CoffeeRate.TWO,
                      Timestamp.now()),
                  Journey(
                      "jid14",
                      "",
                      "",
                      Location(0.0, 0.0, ""),
                      CoffeeOrigin.MEXICO,
                      BrewingMethod.COLD_BREW,
                      CoffeeTaste.CHOCOLATE,
                      CoffeeRate.FOUR,
                      Timestamp.now()),
                  Journey(
                      "jid15",
                      "",
                      "",
                      Location(0.0, 0.0, ""),
                      CoffeeOrigin.UGANDA,
                      BrewingMethod.FRENCH_PRESS,
                      CoffeeTaste.BITTER,
                      CoffeeRate.ONE,
                      Timestamp.now())),
              "uid3"),
          Pair(
              listOf(
                  Journey(
                      "jid16",
                      "",
                      "",
                      Location(0.0, 0.0, ""),
                      CoffeeOrigin.TANZANIA,
                      BrewingMethod.POUR_OVER,
                      CoffeeTaste.FLORAL,
                      CoffeeRate.FOUR,
                      Timestamp.now()),
                  Journey(
                      "jid17",
                      "",
                      "",
                      Location(0.0, 0.0, ""),
                      CoffeeOrigin.RWANDA,
                      BrewingMethod.ESPRESSO_MACHINE,
                      CoffeeTaste.CHOCOLATE,
                      CoffeeRate.FIVE,
                      Timestamp.now()),
                  Journey(
                      "jid18",
                      "",
                      "",
                      Location(0.0, 0.0, ""),
                      CoffeeOrigin.BURUNDI,
                      BrewingMethod.FRENCH_PRESS,
                      CoffeeTaste.NUTTY,
                      CoffeeRate.THREE,
                      Timestamp.now()),
                  Journey(
                      "jid19",
                      "",
                      "",
                      Location(0.0, 0.0, ""),
                      CoffeeOrigin.MALAWI,
                      BrewingMethod.COLD_BREW,
                      CoffeeTaste.SWEET,
                      CoffeeRate.TWO,
                      Timestamp.now()),
                  Journey(
                      "jid20",
                      "",
                      "",
                      Location(0.0, 0.0, ""),
                      CoffeeOrigin.ZAMBIA,
                      BrewingMethod.MOKA_POT,
                      CoffeeTaste.SPICY,
                      CoffeeRate.ONE,
                      Timestamp.now())),
              "uid4"),
          Pair(
              listOf(
                  Journey(
                      "jid21",
                      "",
                      "",
                      Location(0.0, 0.0, ""),
                      CoffeeOrigin.NICARAGUA,
                      BrewingMethod.FRENCH_PRESS,
                      CoffeeTaste.SPICY,
                      CoffeeRate.FIVE,
                      Timestamp.now()),
                  Journey(
                      "jid22",
                      "",
                      "",
                      Location(0.0, 0.0, ""),
                      CoffeeOrigin.EL_SALVADOR,
                      BrewingMethod.FRENCH_PRESS,
                      CoffeeTaste.SWEET,
                      CoffeeRate.FOUR,
                      Timestamp.now()),
                  Journey(
                      "jid23",
                      "",
                      "",
                      Location(0.0, 0.0, ""),
                      CoffeeOrigin.PANAMA,
                      BrewingMethod.MOKA_POT,
                      CoffeeTaste.FRUITY,
                      CoffeeRate.THREE,
                      Timestamp.now()),
                  Journey(
                      "jid24",
                      "",
                      "",
                      Location(0.0, 0.0, ""),
                      CoffeeOrigin.KENYA,
                      BrewingMethod.COLD_BREW,
                      CoffeeTaste.FLORAL,
                      CoffeeRate.TWO,
                      Timestamp.now()),
                  Journey(
                      "jid25",
                      "",
                      "",
                      Location(0.0, 0.0, ""),
                      CoffeeOrigin.ETHIOPIA,
                      BrewingMethod.ESPRESSO_MACHINE,
                      CoffeeTaste.NUTTY,
                      CoffeeRate.ONE,
                      Timestamp.now())),
              "uid5"),
          Pair(
              listOf(
                  Journey(
                      "jid26",
                      "",
                      "",
                      Location(0.0, 0.0, ""),
                      CoffeeOrigin.HONDURAS,
                      BrewingMethod.COLD_BREW,
                      CoffeeTaste.FRUITY,
                      CoffeeRate.FOUR,
                      Timestamp.now()),
                  Journey(
                      "jid27",
                      "",
                      "",
                      Location(0.0, 0.0, ""),
                      CoffeeOrigin.ETHIOPIA,
                      BrewingMethod.ESPRESSO_MACHINE,
                      CoffeeTaste.CHOCOLATE,
                      CoffeeRate.FIVE,
                      Timestamp.now()),
                  Journey(
                      "jid28",
                      "",
                      "",
                      Location(0.0, 0.0, ""),
                      CoffeeOrigin.HONDURAS,
                      BrewingMethod.COLD_BREW,
                      CoffeeTaste.FLORAL,
                      CoffeeRate.THREE,
                      Timestamp.now()),
                  Journey(
                      "jid29",
                      "",
                      "",
                      Location(0.0, 0.0, ""),
                      CoffeeOrigin.HONDURAS,
                      BrewingMethod.MOKA_POT,
                      CoffeeTaste.CHOCOLATE,
                      CoffeeRate.FIVE,
                      Timestamp.now()),
                  Journey(
                      "jid30",
                      "",
                      "",
                      Location(0.0, 0.0, ""),
                      CoffeeOrigin.ETHIOPIA,
                      BrewingMethod.POUR_OVER,
                      CoffeeTaste.BITTER,
                      CoffeeRate.THREE,
                      Timestamp.now())),
              "uid6"))

  @Before
  fun setUp() {
    context = mockk<Activity>(relaxed = true)
    knn = KNNHelper()
  }

  @Test
  fun testEuclideanDistance_validPoints() {
    val point1 = listOf(1.0, 2.0, 3.0, 4.0)
    val point2 = listOf(5.0, 6.0, 7.0, 8.0)
    val expected = 8.0

    val result = knn.euclideanDistance(point1, point2)
    assertEquals(expected, result)
  }

  @Test
  fun testEuclideanDistance_negativeValues() {
    val point1 = listOf(-1.0, -2.0, -3.0)
    val point2 = listOf(-4.0, -5.0, -6.0)
    val expected = sqrt(27.0)

    val result = knn.euclideanDistance(point1, point2)
    assertEquals(expected, result)
  }

  @Test
  fun testEuclideanDistance_emptyPoints() {
    val point1 = emptyList<Double>()
    val point2 = emptyList<Double>()

    val result = knn.euclideanDistance(point1, point2)
    assertEquals(0.0, result)
  }

  @Test
  fun testEuclideanDistance_differentDimensions() {
    val point1 = listOf(1.0, 2.0)
    val point2 = listOf(3.0)
    assertThrows(IllegalArgumentException::class.java) { knn.euclideanDistance(point1, point2) }
  }

  @Test
  fun testMeanOfSubLists_nonEmptyLists() {
    val feature =
        listOf(
            listOf(1.0, 3.0, 5.0, 3.0),
            listOf(3.0, 2.0, 1.0, 4.0),
            listOf(7.0, 5.0, 4.0, 5.0),
            listOf(4.0, 4.0, 6.0, 1.0),
            listOf(18.0, 5.0, 5.0, 4.0))
    val expected = listOf(6.6, 3.8, 4.2, 3.4)

    val result = knn.meanOfSubLists(feature)
    assertEquals(expected, result)
  }

  @Test
  fun testMeanOfSubLists_differentSizes() {
    val feature =
        listOf(
            listOf(1.0, 3.0, 5.0),
            listOf(3.0, 2.0, 1.0, 4.0),
            listOf(7.0, 5.0, 4.0, 5.0),
            listOf(4.0, 4.0, 6.0, 1.0),
            listOf(18.0, 5.0, 5.0, 4.0))
    assertThrows(IllegalArgumentException::class.java) { knn.meanOfSubLists(feature) }
  }

  @Test
  fun testJourneysPreProcessing() {
    val expectedFeatures = listOf(8.2, 3.4, 4.8, 4.2)
    val result = knn.journeysPreProcessing(user.first, user.second)
    assert(result.first == expectedFeatures)
    assert(result.second == user.second)
  }

  @Test
  fun testFeaturesPreProcessing() {
    val expectedFeatures =
        Pair(
            listOf(
                listOf(6.6, 3.8, 4.2, 3.4),
                listOf(10.4, 2.8, 4.0, 3.0),
                listOf(17.4, 3.0, 3.0, 3.0),
                listOf(11.2, 3.2, 3.8, 3.0),
                listOf(5.4, 3.2, 3.6, 4.0)),
            listOf("uid2", "uid3", "uid4", "uid5", "uid6"))

    val result = knn.featuresPreProcessing(otherUsers)
    assertEquals(expectedFeatures, result)
  }

  @Test
  fun testPredictKNN() {
    val expectedPredict = "uid3"
    val userProcessed = knn.journeysPreProcessing(user.first, user.second)
    val otherUsersProcessed = knn.featuresPreProcessing(otherUsers)
    knn.predictKNN(otherUsersProcessed.first, otherUsersProcessed.second, userProcessed.first)
    val result = knn.getKNNResult()
    assertEquals(expectedPredict, result)
  }
}
