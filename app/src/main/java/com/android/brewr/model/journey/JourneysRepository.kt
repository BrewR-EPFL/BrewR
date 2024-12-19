package com.android.brewr.model.journey

/**
 * An interface that defines the operations for managing coffee journeys.
 *
 * This repository provides functions to create, retrieve, update, and delete coffee journeys, as
 * well as handle initialization and generate unique identifiers.
 */
interface JourneysRepository {

  /**
   * Generates a new unique identifier (UID) for a journey.
   *
   * @return A string representing a new unique identifier.
   */
  fun getNewUid(): String

  /**
   * Initializes the repository to prepare it for use.
   *
   * @param onSuccess A callback function to be invoked when initialization is successful.
   */
  fun init(onSuccess: () -> Unit)

  /**
   * Retrieves all journeys from the repository.
   *
   * @param onSuccess A callback function invoked with a list of [Journey] objects on successful
   *   retrieval.
   * @param onFailure A callback function invoked with an [Exception] if retrieval fails.
   */
  fun getJourneys(onSuccess: (List<Journey>) -> Unit, onFailure: (Exception) -> Unit)

  /**
   * Adds a new journey to the repository.
   *
   * @param journey The [Journey] object to be added.
   * @param onSuccess A callback function invoked when the journey is successfully added.
   * @param onFailure A callback function invoked with an [Exception] if the addition fails.
   */
  fun addJourney(journey: Journey, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  /**
   * Updates an existing journey in the repository.
   *
   * @param journey The updated [Journey] object.
   * @param onSuccess A callback function invoked when the journey is successfully updated.
   * @param onFailure A callback function invoked with an [Exception] if the update fails.
   */
  fun updateJourney(journey: Journey, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  /**
   * Deletes a journey from the repository using its unique identifier.
   *
   * @param id The unique identifier (UID) of the journey to be deleted.
   * @param onSuccess A callback function invoked when the journey is successfully deleted.
   * @param onFailure A callback function invoked with an [Exception] if the deletion fails.
   */
  fun deleteJourneyById(id: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
}
