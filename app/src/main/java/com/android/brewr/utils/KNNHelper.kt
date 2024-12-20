package com.android.brewr.utils

import android.util.Log
import com.android.brewr.model.coffee.CoffeeShop
import com.android.brewr.model.journey.BrewingMethod
import com.android.brewr.model.journey.CoffeeOrigin
import com.android.brewr.model.journey.CoffeeRate
import com.android.brewr.model.journey.CoffeeTaste
import com.android.brewr.model.journey.Journey
import kotlin.math.pow
import kotlin.math.sqrt

/** Helper class for K-Nearest Neighbors (KNN) algorithm. */
class KNNHelper {
  /** Stores the predicted user ID. */
  private var predictedUid = ""

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

    //      Log.d("recommendation feature in journey form",journeys.toString())
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

    //      Log.d("recommendation feature in double form",(weightedOrigin + weightedMethod +
    // weightedTaste + weightedAvgRating).toString())
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

    Log.d("recommendation feature in journey form", usersData.toString())
    val processedJourneys = mutableListOf<List<Double>>()
    val userIds = mutableListOf<String>()

    usersData.forEach { (journeys, uid) ->
      processedJourneys.add(journeysPreProcessing(journeys))
      userIds.add(uid)
    }
    Log.d("recommendation feature in double form", Pair(processedJourneys, userIds).toString())
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
  ): String? {

    val features = featuresAndLabels.first
    val labels = featuresAndLabels.second
    if (features.isEmpty() || labels.isEmpty()) {
      return null
    } else {
      val distances =
          features
              .mapIndexed { index, feature ->
                euclideanDistance(feature, userJourneys) to labels[index]
              }
              .sortedBy { it.first }

      predictedUid = distances[k].second // not sure if the nearest one is the best one
      return predictedUid
    }
  }

  /**
   * Adds coffee shops from a user's journeys to the recommended set based on specific criteria.
   *
   * This method iterates through the provided list of user journeys and evaluates each journey to
   * determine if its associated coffee shop meets the recommendation criteria. Coffee shops are
   * added to the recommendation set if they:
   * - Have a valid (non-null) location.
   * - Have a rating of 4 stars or higher.
   *
   * @param userJourneys A list of journeys belonging to a specific user. Each journey contains
   *   details such as the associated coffee shop and its rating.
   * @param coffeeShopSet A mutable set of coffee shops to which the eligible coffee shops will be
   *   added.
   * @return The updated set of recommended coffee shops.
   */
  fun AddJourneysOfUserToRecommendation(
      userJourneys: List<Journey>,
      coffeeShopSet: MutableSet<CoffeeShop>
  ): MutableSet<CoffeeShop> {
    for (journey in userJourneys) {
      val journeyCoffeeShop = journey.coffeeShop
      val journeyRate = getRatingValue(journey.coffeeRate)
      // make journey has a location and location is not null and rating better than 4 stars
      if (journeyCoffeeShop != null && journeyRate >= 4) {
        coffeeShopSet.add(journeyCoffeeShop)
      }
    }
    return coffeeShopSet
  }

  /**
   * Selects the list of journeys associated with a specific user ID from a list of user data.
   *
   * @param usersData A list of pairs, where each pair contains a list of journeys and a user ID.
   * @param userId The user ID for which journeys need to be selected.
   * @return A list of journeys corresponding to the provided user ID. If no matching user ID is
   *   found, returns an empty list.
   */
  fun selectJourneysFromId(
      usersData: List<Pair<List<Journey>, String>>,
      userId: String
  ): List<Journey> {
    // Find the pair where the second element (user ID) matches the given userId
    val userJourneyPair = usersData.find { it.second == userId }

    // If a match is found, return the list of journeys; otherwise, return an empty list
    return userJourneyPair?.first ?: emptyList()
  }

  /**
   * Generates a set of recommended coffee shops based on user journey data.
   *
   * This method applies a K-Nearest Neighbors (KNN) algorithm to predict the most relevant user ID
   * whose journeys are similar to the current user's journey data. It then retrieves and processes
   * the recommended coffee shops from that user's journeys.
   *
   * @param journeysRecommended The set of currently recommended coffee shops to be updated.
   * @param usersData A list of pairs containing all users' journey data and their corresponding
   *   user IDs. Each pair consists of:
   *     - A list of journeys made by the user.
   *     - A string representing the user ID.
   *
   * @param currentUserData The journey data of the current user.
   * @return A set of recommended coffee shops, updated based on the predicted user's journey data.
   */
  fun getRecommendation(
      journeysRecommended: MutableSet<CoffeeShop>,
      usersData: List<Pair<List<Journey>, String>>,
      currentUserData: List<Journey>
  ): MutableSet<CoffeeShop> {
    // Preprocess journey data extract features
    val usersFeatures = featuresPreProcessing(usersData.filter { it.first.isNotEmpty() })
    Log.d("recommendation user feature", usersFeatures.toString())
    val currentFeature = journeysPreProcessing(currentUserData)
    Log.d("recommendation current feature", currentFeature.toString())

    // Predict the ID of the user whose journey data is most similar to the current user
    val predictedId = predictKNN(usersFeatures, currentFeature)
    Log.d("recommendation predict Id", predictedId.toString())

    // If a predicted user ID is found, retrieve that user's journeys
    if (predictedId != null) {
      val newJourneys = selectJourneysFromId(usersData, predictedId)
      // Add the new user's journeys to the recommended coffee shops
      return AddJourneysOfUserToRecommendation(newJourneys, journeysRecommended)
    } else {
      // If no prediction is made, return the current set of recommendations unchanged
      return journeysRecommended
    }
  }
}
