package com.android.brewr.model.journey

interface JourneysRepository {
  fun getNewUid(): String

  fun init(onSuccess: () -> Unit)

  fun getJourneysOfCurrentUser(onSuccess: (List<Journey>) -> Unit, onFailure: (Exception) -> Unit)

  fun getJourneysOfAllOtherUsers(
      onSuccess: (List<Pair<List<Journey>, String>>) -> Unit,
      onFailure: (Exception) -> Unit
  )

  fun getJourneysOfTheUser(
      uid: String,
      onSuccess: (List<Journey>) -> Unit,
      onFailure: (Exception) -> Unit
  )

  fun addJourney(journey: Journey, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  fun updateJourney(journey: Journey, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  fun deleteJourneyById(id: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
}
