package com.android.brewr.model.recommendation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.brewr.model.coffee.Coffee
import com.android.brewr.model.journey.Journey
import com.android.brewr.model.journey.JourneysRepository
import com.android.brewr.utils.KNNHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RecommendationViewModel(private val journeyRepository: JourneysRepository) {

  private val data_ = MutableStateFlow<List<Pair<List<Journey>, String>>>(emptyList())
  val data: StateFlow<List<Pair<List<Journey>, String>>> = data_.asStateFlow()

  private val journeys_ = MutableStateFlow<List<Journey>>(emptyList())
  val journeys: StateFlow<List<Journey>> = journeys_.asStateFlow()

  private val usersJourneys_ = MutableStateFlow<List<Journey>>(emptyList())
  val usersJourneys: StateFlow<List<Journey>> = journeys_.asStateFlow()

  private val _predictedUserId = MutableLiveData<String>()
  val predictedUserId: LiveData<String>
    get() = _predictedUserId

  private val recommendedCoffees_ = MutableStateFlow<List<Coffee>>(emptyList())
  val recommendedCoffees: StateFlow<List<Coffee>> = recommendedCoffees_.asStateFlow()

  private val knnHelper = KNNHelper()

  /**
   * Predicts the user ID based on the provided user journeys and current user's journey features.
   *
   * This function uses the K-Nearest Neighbors (KNN) algorithm to predict the user ID by processing
   * the features of the journeys of all other users and comparing them with the feature vector of
   * the current user's journeys. The prediction is then stored in a mutable state variable.
   *
   * @param usersJourneys A list of pairs, where each pair contains a list of Journey objects and a
   *   user ID.
   * @param userJourneys A list of doubles representing the feature vector of the current user's
   *   journeys.
   */
  fun predictUser(usersJourneys: List<Pair<List<Journey>, String>>, userJourneys: List<Double>) {
    val data = knnHelper.featuresPreProcessing(usersJourneys)
    val predictedUserId = knnHelper.predictKNN(data, userJourneys)
    journeyRepository.getJourneysOfTheUser(
        predictedUserId,
        onSuccess = {
          val journeys = it
          // add new coffee shop into the coffee shop list
          // recommendedCoffees_.value
          TODO()
        },
        onFailure = {})
  }

  /**
   * Prepares the data by retrieving the journeys of all users and the current user.
   *
   * This function fetches journey data for all users and the current user from the repository. The
   * retrieved data is stored in corresponding mutable state variables.
   */
  fun prepareData() {
    journeyRepository.getJourneysOfAllOtherUsers(onSuccess = { data_.value = it }, onFailure = {})
    journeyRepository.getJourneysOfCurrentUser(onSuccess = { journeys_.value = it }, onFailure = {})
  }

  //  /**
  //   * Gets recommended coffee shop locations based on the current location and the predicted user
  // ID.
  //   *
  //   * This function fetches the journey data of the predicted user and selects relevant coffee
  // shops
  //   * that are within a threshold distance from the current location. The list of recommended
  //   * locations is stored in a mutable state variable.
  //   *
  //   * @param currentLocation The current location from which the distance to coffee shop
  // locations
  //   *   will be calculated.
  //   */
  //  fun getRecommendedLocation(currentLocation: Location) {
  //    predictedUserId.value?.let {
  //      journeyRepository.getJourneysOfTheUser(
  //          it, onSuccess = { usersJourneys_.value = it }, onFailure = {})
  //    }
  //    recommendedCoffees_.value =
  //        knnHelper.selectRecordsOfUser(usersJourneys_.value, currentLocation, 0.1)
  //  }

  //    fun updateRecommendedJourneys(){
  //        val uid = knnHelper.getKNNResult()
  //        journeyRepository.getJourneysOfTheUser(uid,onSuccess = { recommendedJourneys_.value = it
  // }, onFailure = {})
  //    }
  //    fun updateRecommendedCoffees(){
  //        //get location of each, filter current location 20km
  //    }
  //    fun getRecommendedCoffee(){
  //        //get the highest rated one
  //    }
  //    fun getNextRecommendedCoffee(){
  //        //change the recommended to then next second of the list, if there is no more recommend,
  // show"..."
  //    }
}
