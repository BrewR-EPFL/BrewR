package com.android.brewr.utils

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
   * Calculates the mean of sublists.
   *
   * @param featuresLists A list of lists containing feature values.
   * @return A list containing the mean of each sublist.
   * @throws IllegalArgumentException if the sublists do not have the same size.
   */
  fun meanOfSubLists(featuresLists: List<List<Double>>): List<Double> {
    val size = featuresLists[0].size
    for (featuresList in featuresLists) {
      require(featuresList.size == size) { "Lists must have the same size" }
    }
    return List(size) { index -> featuresLists.map { it[index] }.average() }
  }

  /**
   * Preprocesses a list of journeys into feature vectors.
   *
   * @param journeys The list of Journey objects to preprocess.
   * @param uid The user ID associated with the journeys.
   * @return A pair containing the list of feature vectors and the user ID.
   */
  fun journeysPreProcessing(
      journeys: List<Journey>,
      uid: String
  ): Pair<List<Double>, String> { // For each journey, extract the features and store them in a list
    val features =
        journeys.map {
          listOf(
              it.coffeeOrigin.ordinal.toDouble(),
              it.brewingMethod.ordinal.toDouble(),
              it.coffeeTaste.ordinal.toDouble(),
              it.coffeeRate.ordinal.toDouble())
        }
    return Pair(meanOfSubLists(features), uid)
  }

  /**
   * Preprocesses a list of users' journey data into feature vectors and user IDs.
   *
   * @param usersData A list of pairs, where each pair contains a list of Journey objects and a user
   *   ID.
   * @return A pair containing a list of lists of feature vectors and a list of user IDs.
   */
  fun featuresPreProcessing(
      usersData: List<Pair<List<Journey>, String>>
  ): Pair<List<List<Double>>, List<String>> {
    val processedJourneys = mutableListOf<List<Double>>()
    val userIds = mutableListOf<String>()

    usersData.forEach { (journeys, uid) ->
      val (processedJourney, userId) = journeysPreProcessing(journeys, uid)
      processedJourneys.add(processedJourney)
      userIds.add(userId)
    }

    return Pair(processedJourneys, userIds)
  }

  /**
   * Predicts the user ID using the K-Nearest Neighbors (KNN) algorithm.
   *
   * @param features A list of feature vectors for each user.
   * @param labels A list of user IDs corresponding to the feature vectors.
   * @param userJourneys The feature vector of the user to predict.
   * @param k The number of nearest neighbors to consider (default is 1).
   * @return The predicted user ID.
   */
  fun predictKNN(
      features: List<List<Double>>,
      labels: List<String>,
      userJourneys: List<Double>,
      k: Int = 1
  ): String {
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
