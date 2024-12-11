package com.android.brewr.utils

import com.android.brewr.model.journey.BrewingMethod
import com.android.brewr.model.journey.CoffeeOrigin
import com.android.brewr.model.journey.CoffeeRate
import com.android.brewr.model.journey.CoffeeTaste
import com.android.brewr.model.journey.Journey
import com.android.brewr.model.map.Location
import kotlin.math.pow
import kotlin.math.sqrt

/** Helper class for K-Nearest Neighbors (KNN) algorithm. */
class KNNHelper {
  /** Stores the predicted user ID. */
  private var predictedUid = ""
  private var predictedJourneys: List<Location> = emptyList()
  private var index: Int = 0

  /**
   * Returns the predicted user ID from the KNN algorithm.
   *
   * @return The predicted user ID.
   */
  fun getKNNResult(): String {
    return this.predictedUid
  }

  /**
   * Calculates the Euclidean distance between two points.
   *
   * @param point1 The first point as a list of doubles.
   * @param point2 The second point as a list of doubles.
   * @return The Euclidean distance between the two points.
   * @throws IllegalArgumentException if the points do not have the same number of dimensions.
   */
  fun euclideanDistance(point1: List<Double>, point2: List<Double>): Double {
    require(point1.size == point2.size) { "Points must have the same number of dimensions" }
    return sqrt(point1.zip(point2).sumOf { (a, b) -> (a - b).pow(2) })
  }

  /**
   * Converts a `CoffeeRate` enum value to its corresponding integer value.
   *
   * @param rate The `CoffeeRate` enum value to convert.
   * @return The integer value corresponding to the given `CoffeeRate`.
   */
  fun getRatingValue(rate: CoffeeRate): Double {
    return when (rate) {
      CoffeeRate.ONE -> 1.0
      CoffeeRate.TWO -> 2.0
      CoffeeRate.THREE -> 3.0
      CoffeeRate.FOUR -> 4.0
      CoffeeRate.FIVE -> 5.0
      CoffeeRate.DEFAULT -> 0.0 // Or handle as invalid
    }
  }

  /**
   * Preprocesses a list of Journey objects into a list of weighted feature values.
   *
   * @param journeys A list of Journey objects to preprocess.
   * @return A list of doubles representing the weighted feature values.
   */
  fun journeysPreProcessing(journeys: List<Journey>): List<Double> {
    // Weighted frequency computation for categorical features
    val totalWeight = journeys.sumOf { getRatingValue(it.coffeeRate) }

    val weightedOrigin =
        CoffeeOrigin.entries
            .filter { it != CoffeeOrigin.DEFAULT }
            .map { origin ->
              journeys
                  .filter { it.coffeeOrigin == origin }
                  .sumOf { getRatingValue(it.coffeeRate) } / totalWeight
            }

    val weightedMethod =
        BrewingMethod.entries
            .filter { it != BrewingMethod.DEFAULT }
            .map { method ->
              journeys
                  .filter { it.brewingMethod == method }
                  .sumOf { getRatingValue(it.coffeeRate) } / totalWeight
            }

    val weightedTaste =
        CoffeeTaste.entries
            .filter { it != CoffeeTaste.DEFAULT }
            .map { taste ->
              journeys.filter { it.coffeeTaste == taste }.sumOf { getRatingValue(it.coffeeRate) } /
                  totalWeight
            }

    // Weighted average rating
    val weightedAvgRating =
        journeys.sumOf { getRatingValue(it.coffeeRate) } /
            (journeys.size * (CoffeeRate.entries.size - 1))

    return weightedOrigin + weightedMethod + weightedTaste + weightedAvgRating
  }

  /**
   * Preprocesses user data into feature vectors and corresponding user IDs.
   *
   * @param usersData A list of pairs, where each pair contains a list of Journey objects and a user
   *   ID.
   * @return A pair containing a list of feature vectors and a list of user IDs.
   */
  fun featuresPreProcessing(
      usersData: List<Pair<List<Journey>, String>>
  ): Pair<List<List<Double>>, List<String>> {
    val processedJourneys = mutableListOf<List<Double>>()
    val userIds = mutableListOf<String>()

    usersData.forEach { (journeys, uid) ->
      processedJourneys.add(journeysPreProcessing(journeys))
      userIds.add(uid)
    }

    return Pair(processedJourneys, userIds)
  }

  /**
   * Predicts the user ID using the K-Nearest Neighbors (KNN) algorithm.
   *
   * @param featuresAndLabels A pair containing a list of feature vectors and a list of user IDs.
   * @param userJourneys A list of doubles representing the feature vector of the user's journeys.
   * @param k The number of nearest neighbors to consider (default is 1).
   * @return The predicted user ID.
   */
  fun predictKNN(
      featuresAndLabels: Pair<List<List<Double>>, List<String>>,
      userJourneys: List<Double>,
      k: Int = 1
  ): String {
    val features = featuresAndLabels.first
    val labels = featuresAndLabels.second
    val distances =
        features
            .mapIndexed { index, feature ->
              euclideanDistance(feature, userJourneys) to labels[index]
            }
            .sortedBy { it.first }

    predictedUid = distances[k].second // not sure if the nearest one is the best one
    return predictedUid
  }

  /**
   * Selects relevant records of coffee shop locations from a user's journeys based on a threshold
   * distance.
   *
   * This function filters the user's journey data to include only locations that are not marked as
   * "home" and have a rating of 4 stars or better. It then calculates the distance between each
   * journey's location and the given location, and selects the locations that are within the
   * specified threshold distance. The resulting list of selected locations is returned.
   *
   * @param userJourneys A list of `Journey` objects representing the user's journey data.
   * @param givenLocation The location from which distances to the user's journey locations will be
   *   calculated.
   * @param thresholdDistance The maximum distance (in meters) to consider when selecting coffee
   *   shop locations.
   * @return A list of `Location` objects that are within the threshold distance and meet the rating
   *   criteria.
   */
  fun selectRecordsOfUser(
      userJourneys: List<Journey>,
      givenLocation: Location,
      thresholdDistance: Double
  ): List<Location> {
    val coffeeShopSet = mutableListOf<Location>()

    for (journey in userJourneys) {
      val journeyLocation = journey.location
      val journeyRate = getRatingValue(journey.coffeeRate)
      // make journey has a location and location is not null and rating better than 4 stars
      if (journeyLocation.isNotAtHome() && journeyRate >= 4) {
        // calculate the distance
        val distance = givenLocation.distanceTo(journeyLocation)
        if (distance < thresholdDistance) {
          coffeeShopSet.add(journeyLocation)
        }
      }
    }
    return coffeeShopSet
  }

  /**
   * Updates the K-Nearest Neighbors (KNN) algorithm by processing the provided journey data and
   * predicting the user ID.
   *
   * This function prepares the feature vectors for the given user journeys, processes them, and
   * then uses the KNN algorithm to predict the user ID based on the current user's journey
   * features. The predicted user ID is stored internally for later use.
   *
   * @param usersJourneys A list of pairs, where each pair contains a list of `Journey` objects and
   *   a user ID.
   * @param userJourneys A list of doubles representing the feature vector of the current user's
   *   journeys.
   */
  fun updateKNN(usersJourneys: List<Pair<List<Journey>, String>>, userJourneys: List<Double>) {
    val data = featuresPreProcessing(usersJourneys)
    val predictUid = predictKNN(data, userJourneys)
  }

  //    fun getNextRecommend(
  //        predictedJourneys: List<Location>
  //    ):Location?{
  //        if (predictedJourneys.isNotEmpty()){
  //
  //        }else{
  //
  //        }
  //
  //    }

}
