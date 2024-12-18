package com.android.brewr.utils

import android.app.Activity
import android.content.Context
import com.android.brewr.model.journey.BrewingMethod
import com.android.brewr.model.journey.CoffeeOrigin
import com.android.brewr.model.journey.CoffeeRate
import com.android.brewr.model.journey.CoffeeTaste
import com.android.brewr.model.journey.Journey
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
                  null,
                  CoffeeOrigin.COSTA_RICA,
                  BrewingMethod.COLD_BREW,
                  CoffeeTaste.FRUITY,
                  CoffeeRate.FIVE,
                  Timestamp.now()),
              Journey(
                  "jid2",
                  "",
                  "",
                  null,
                  CoffeeOrigin.ETHIOPIA,
                  BrewingMethod.ESPRESSO_MACHINE,
                  CoffeeTaste.CHOCOLATE,
                  CoffeeRate.FOUR,
                  Timestamp.now()),
              Journey(
                  "jid3",
                  "",
                  "",
                  null,
                  CoffeeOrigin.HONDURAS,
                  BrewingMethod.COLD_BREW,
                  CoffeeTaste.FRUITY,
                  CoffeeRate.FIVE,
                  Timestamp.now()),
              Journey(
                  "jid4",
                  "",
                  "",
                  null,
                  CoffeeOrigin.COSTA_RICA,
                  BrewingMethod.COLD_BREW,
                  CoffeeTaste.NUTTY,
                  CoffeeRate.FIVE,
                  Timestamp.now()),
              Journey(
                  "jid5",
                  "",
                  "",
                  null,
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
                      null,
                      CoffeeOrigin.BRAZIL,
                      BrewingMethod.FRENCH_PRESS,
                      CoffeeTaste.SWEET,
                      CoffeeRate.THREE,
                      Timestamp.now()),
                  Journey(
                      "jid7",
                      "",
                      "",
                      null,
                      CoffeeOrigin.COLOMBIA,
                      BrewingMethod.POUR_OVER,
                      CoffeeTaste.FLORAL,
                      CoffeeRate.FOUR,
                      Timestamp.now()),
                  Journey(
                      "jid8",
                      "",
                      "",
                      null,
                      CoffeeOrigin.GUATEMALA,
                      BrewingMethod.MOKA_POT,
                      CoffeeTaste.SPICY,
                      CoffeeRate.FIVE,
                      Timestamp.now()),
                  Journey(
                      "jid9",
                      "",
                      "",
                      null,
                      CoffeeOrigin.INDONESIA,
                      BrewingMethod.COLD_BREW,
                      CoffeeTaste.FRUITY,
                      CoffeeRate.ONE,
                      Timestamp.now()),
                  Journey(
                      "jid10",
                      "",
                      "",
                      null,
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
                      null,
                      CoffeeOrigin.VIETNAM,
                      BrewingMethod.POUR_OVER,
                      CoffeeTaste.BITTER,
                      CoffeeRate.THREE,
                      Timestamp.now()),
                  Journey(
                      "jid12",
                      "",
                      "",
                      null,
                      CoffeeOrigin.PANAMA,
                      BrewingMethod.COLD_BREW,
                      CoffeeTaste.FLORAL,
                      CoffeeRate.FIVE,
                      Timestamp.now()),
                  Journey(
                      "jid13",
                      "",
                      "",
                      null,
                      CoffeeOrigin.PERU,
                      BrewingMethod.ESPRESSO_MACHINE,
                      CoffeeTaste.NUTTY,
                      CoffeeRate.TWO,
                      Timestamp.now()),
                  Journey(
                      "jid14",
                      "",
                      "",
                      null,
                      CoffeeOrigin.MEXICO,
                      BrewingMethod.COLD_BREW,
                      CoffeeTaste.CHOCOLATE,
                      CoffeeRate.FOUR,
                      Timestamp.now()),
                  Journey(
                      "jid15",
                      "",
                      "",
                      null,
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
                      null,
                      CoffeeOrigin.TANZANIA,
                      BrewingMethod.POUR_OVER,
                      CoffeeTaste.FLORAL,
                      CoffeeRate.FOUR,
                      Timestamp.now()),
                  Journey(
                      "jid17",
                      "",
                      "",
                      null,
                      CoffeeOrigin.RWANDA,
                      BrewingMethod.ESPRESSO_MACHINE,
                      CoffeeTaste.CHOCOLATE,
                      CoffeeRate.FIVE,
                      Timestamp.now()),
                  Journey(
                      "jid18",
                      "",
                      "",
                      null,
                      CoffeeOrigin.BURUNDI,
                      BrewingMethod.FRENCH_PRESS,
                      CoffeeTaste.NUTTY,
                      CoffeeRate.THREE,
                      Timestamp.now()),
                  Journey(
                      "jid19",
                      "",
                      "",
                      null,
                      CoffeeOrigin.MALAWI,
                      BrewingMethod.COLD_BREW,
                      CoffeeTaste.SWEET,
                      CoffeeRate.TWO,
                      Timestamp.now()),
                  Journey(
                      "jid20",
                      "",
                      "",
                      null,
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
                      null,
                      CoffeeOrigin.NICARAGUA,
                      BrewingMethod.FRENCH_PRESS,
                      CoffeeTaste.SPICY,
                      CoffeeRate.FIVE,
                      Timestamp.now()),
                  Journey(
                      "jid22",
                      "",
                      "",
                      null,
                      CoffeeOrigin.EL_SALVADOR,
                      BrewingMethod.FRENCH_PRESS,
                      CoffeeTaste.SWEET,
                      CoffeeRate.FOUR,
                      Timestamp.now()),
                  Journey(
                      "jid23",
                      "",
                      "",
                      null,
                      CoffeeOrigin.PANAMA,
                      BrewingMethod.MOKA_POT,
                      CoffeeTaste.FRUITY,
                      CoffeeRate.THREE,
                      Timestamp.now()),
                  Journey(
                      "jid24",
                      "",
                      "",
                      null,
                      CoffeeOrigin.KENYA,
                      BrewingMethod.COLD_BREW,
                      CoffeeTaste.FLORAL,
                      CoffeeRate.TWO,
                      Timestamp.now()),
                  Journey(
                      "jid25",
                      "",
                      "",
                      null,
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
                      null,
                      CoffeeOrigin.HONDURAS,
                      BrewingMethod.COLD_BREW,
                      CoffeeTaste.FRUITY,
                      CoffeeRate.FOUR,
                      Timestamp.now()),
                  Journey(
                      "jid27",
                      "",
                      "",
                      null,
                      CoffeeOrigin.ETHIOPIA,
                      BrewingMethod.ESPRESSO_MACHINE,
                      CoffeeTaste.CHOCOLATE,
                      CoffeeRate.FIVE,
                      Timestamp.now()),
                  Journey(
                      "jid28",
                      "",
                      "",
                      null,
                      CoffeeOrigin.HONDURAS,
                      BrewingMethod.COLD_BREW,
                      CoffeeTaste.FLORAL,
                      CoffeeRate.THREE,
                      Timestamp.now()),
                  Journey(
                      "jid29",
                      "",
                      "",
                      null,
                      CoffeeOrigin.HONDURAS,
                      BrewingMethod.MOKA_POT,
                      CoffeeTaste.CHOCOLATE,
                      CoffeeRate.FIVE,
                      Timestamp.now()),
                  Journey(
                      "jid30",
                      "",
                      "",
                      null,
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
  fun testGetRatingValue() {
    val expectedValues = listOf(1.0, 2.0, 3.0, 4.0, 5.0, 0.0)
    val rates =
        listOf(
            CoffeeRate.ONE,
            CoffeeRate.TWO,
            CoffeeRate.THREE,
            CoffeeRate.FOUR,
            CoffeeRate.FIVE,
            CoffeeRate.DEFAULT)

    rates.forEachIndexed { index, rate ->
      val result = knn.getRatingValue(rate)
      assertEquals(expectedValues[index], result)
    }
  }

  @Test
  fun testJourneysPreProcessing() {
    val expectedFeatures =
        listOf(
            0.0,
            0.0,
            0.0,
            0.0,
            0.23809523809523808,
            0.19047619047619047,
            0.0,
            0.47619047619047616,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.09523809523809523,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.19047619047619047,
            0.0,
            0.0,
            0.8095238095238095,
            0.0,
            0.0,
            0.19047619047619047,
            0.23809523809523808,
            0.0,
            0.0,
            0.47619047619047616,
            0.09523809523809523,
            0.84)

    val result = knn.journeysPreProcessing(user.first)
    assert(result == expectedFeatures)
  }

  @Test
  fun testFeaturesPreProcessing() {
    val expectedFeatures =
        Pair(
            listOf(
                listOf(
                    0.17647058823529413,
                    0.0,
                    0.23529411764705882,
                    0.058823529411764705,
                    0.0,
                    0.0,
                    0.29411764705882354,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.23529411764705882,
                    0.0,
                    0.0,
                    0.0,
                    0.23529411764705882,
                    0.17647058823529413,
                    0.058823529411764705,
                    0.5294117647058824,
                    0.23529411764705882,
                    0.0,
                    0.0,
                    0.29411764705882354,
                    0.4117647058823529,
                    0.058823529411764705,
                    0.0,
                    0.68),
                listOf(
                    0.0,
                    0.2,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.26666666666666666,
                    0.13333333333333333,
                    0.0,
                    0.0,
                    0.3333333333333333,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.06666666666666667,
                    0.0,
                    0.0,
                    0.13333333333333333,
                    0.2,
                    0.06666666666666667,
                    0.6,
                    0.0,
                    0.3333333333333333,
                    0.26666666666666666,
                    0.13333333333333333,
                    0.0,
                    0.0,
                    0.0,
                    0.26666666666666666,
                    0.6),
                listOf(
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.26666666666666666,
                    0.3333333333333333,
                    0.2,
                    0.0,
                    0.13333333333333333,
                    0.06666666666666667,
                    0.3333333333333333,
                    0.26666666666666666,
                    0.2,
                    0.13333333333333333,
                    0.06666666666666667,
                    0.26666666666666666,
                    0.3333333333333333,
                    0.2,
                    0.06666666666666667,
                    0.13333333333333333,
                    0.0,
                    0.0,
                    0.6),
                listOf(
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.06666666666666667,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.3333333333333333,
                    0.26666666666666666,
                    0.2,
                    0.13333333333333333,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.06666666666666667,
                    0.0,
                    0.6,
                    0.13333333333333333,
                    0.2,
                    0.13333333333333333,
                    0.0,
                    0.06666666666666667,
                    0.3333333333333333,
                    0.26666666666666666,
                    0.2,
                    0.0,
                    0.6),
                listOf(
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.6,
                    0.4,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.25,
                    0.15,
                    0.0,
                    0.35,
                    0.25,
                    0.15,
                    0.5,
                    0.0,
                    0.0,
                    0.0,
                    0.2,
                    0.15,
                    0.8)),
            listOf("uid2", "uid3", "uid4", "uid5", "uid6"))

    val result = knn.featuresPreProcessing(otherUsers)
    assertEquals(expectedFeatures, result)
  }

  @Test
  fun testPredictKNN() {
    val expectedPredict = "uid3"
    knn.predictKNN(knn.featuresPreProcessing(otherUsers), knn.journeysPreProcessing(user.first))
    val result = knn.getKNNResult()
    assertEquals(expectedPredict, result)
  }
}
