package com.android.brewr.model.recommendation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.brewr.model.coffee.CoffeeShop
import com.android.brewr.model.journey.Journey
import com.android.brewr.model.journey.JourneysRepository
import com.android.brewr.model.journey.JourneysRepositoryFirestore
import com.android.brewr.utils.KNNHelper
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

open class RecommendationViewModel(private val journeysRepository: JourneysRepository) :
    ViewModel() {

  private val recommendedCoffees_ = MutableStateFlow<MutableSet<CoffeeShop>>(mutableSetOf())
  open val recommendedCoffees: StateFlow<MutableSet<CoffeeShop>> = recommendedCoffees_.asStateFlow()

  private val knnHelper = KNNHelper()

  /**
   * Companion object providing a factory for creating instances of [RecommendationViewModel].
   *
   * This factory is useful when a ViewModel needs to be created programmatically or injected into a
   * lifecycle owner (e.g., in Android's ViewModelProvider).
   */
  companion object {
    val Factory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RecommendationViewModel::class.java)) {
              return RecommendationViewModel(
                  JourneysRepositoryFirestore(Firebase.firestore, FirebaseAuth.getInstance()))
                  as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
          }
        }
  }
  /**
   * Initializes the `RecommendationViewModel`.
   *
   * This block is executed immediately upon creation of the ViewModel. It initializes the
   * `journeysRepository` by invoking its `init` method with a callback to `addRecommends()`.
   *
   * The initialization ensures that recommendations are updated as soon as the ViewModel is
   * created.
   */
  init {
    journeysRepository.init { addRecommends() }
  }

  /**
   * Fetches and calculates coffee shop recommendations.
   *
   * This method retrieves journey data for the current user and other users, applies the K-Nearest
   * Neighbors (KNN) algorithm to determine recommendations, and updates the `recommendedCoffees`
   * state.
   *
   * The flow is as follows:
   * 1. Fetches the current user's journey data using `journeysRepository.getJourneys`.
   * 2. Fetches journey data of all other users using
   *    `journeysRepository.retrieveJourneysOfAllOtherUsers`.
   * 3. Applies the KNN algorithm to recommend coffee shops based on similarities between users.
   * 4. Updates the `recommendedCoffees` state with the recommended coffee shops.
   *
   * Logs intermediate results for debugging:
   * - Logs the current user's journey data.
   * - Logs the other users' journey data.
   * - Logs the generated recommendations.
   */
  fun addRecommends() {
    var usersData: List<Pair<List<Journey>, String>>
    var currentUserData: List<Journey>

    journeysRepository.getJourneys(
        onSuccess = {
          currentUserData = it
          Log.d("recommendation", it.toString())
          journeysRepository.retrieveJourneysOfAllOtherUsers(
              onSuccess = {
                usersData = it
                Log.d("recommendation", it.toString())

                val recommendations =
                    knnHelper.getRecommendation(
                        recommendedCoffees.value, usersData, currentUserData)
                Log.d("recommendation", recommendations.toString())
                recommendedCoffees_.value = recommendations
              },
              onFailure = {})
        },
        onFailure = {})
  }
}
