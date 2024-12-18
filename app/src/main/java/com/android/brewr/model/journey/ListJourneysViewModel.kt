package com.android.brewr.model.journey

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.*

/**
 * ViewModel responsible for managing a list of journeys and selected journey state.
 *
 * This ViewModel acts as the intermediary between the UI and the [JourneysRepository]. It provides
 * functionality to retrieve, add, update, and delete Journey documents while maintaining state
 * flows for reactive updates to the UI.
 *
 * @param repository An instance of [JourneysRepository] for interacting with journey data.
 */
open class ListJourneysViewModel(private val repository: JourneysRepository) : ViewModel() {
  private val journeys_ = MutableStateFlow<List<Journey>>(emptyList())
  val journeys: StateFlow<List<Journey>> = journeys_.asStateFlow()

  // Selected journey, i.e the journey for the detail view
  private val selectedJourney_ = MutableStateFlow<Journey?>(null)
  open val selectedJourney: StateFlow<Journey?> = selectedJourney_.asStateFlow()

  init {
    repository.init { getJourneys() }
  }

  /**
   * Companion object that provides a factory for creating instances of [ListJourneysViewModel].
   *
   * The factory pattern ensures that dependencies such as the repository are provided during
   * ViewModel creation.
   */
  companion object {
    val Factory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ListJourneysViewModel(
                JourneysRepositoryFirestore(Firebase.firestore, FirebaseAuth.getInstance()))
                as T
          }
        }
  }

  /**
   * Generates a new unique ID.
   *
   * @return A new unique ID.
   */
  fun getNewUid(): String {
    return repository.getNewUid()
  }

  /** Gets all Journey documents of current user. */
  fun getJourneys() {
    repository.getJourneys(onSuccess = { journeys_.value = it }, onFailure = {})
  }

  /**
   * Adds a Journey document.
   *
   * @param journey The Journey document to be added.
   */
  fun addJourney(journey: Journey) {
    repository.addJourney(journey = journey, onSuccess = { getJourneys() }, onFailure = {})
  }

  /**
   * Updates a Journey document.
   *
   * @param journey The Journey document to be updated.
   */
  fun updateJourney(journey: Journey) {
    repository.updateJourney(journey = journey, onSuccess = { getJourneys() }, onFailure = {})
  }

  /**
   * Deletes a Journey document by its ID.
   *
   * @param id The ID of the Journey document to be deleted.
   */
  fun deleteJourneyById(id: String) {
    repository.deleteJourneyById(id = id, onSuccess = { getJourneys() }, onFailure = {})
  }

  /**
   * Selects a Journey document.
   *
   * @param journey The Journey document to be selected.
   */
  fun selectJourney(journey: Journey) {
    selectedJourney_.value = journey
  }
}
