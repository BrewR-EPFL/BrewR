package com.android.brewr.model.journey

interface JourneysRepository {
  fun getNewUid(): String

  fun init(onSuccess: () -> Unit)

  fun getJourneys(onSuccess: (List<Journey>) -> Unit, onFailure: (Exception) -> Unit)

  fun addJourney(journey: Journey, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  fun updateJourney(journey: Journey, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  fun deleteJourneyById(id: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
}
