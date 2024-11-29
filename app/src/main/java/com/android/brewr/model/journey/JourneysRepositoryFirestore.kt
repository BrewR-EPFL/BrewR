package com.android.brewr.model.journey

import android.util.Log
import com.android.brewr.model.map.Location
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class JourneysRepositoryFirestore(private val db: FirebaseFirestore) : JourneysRepository {

  private val collectionPath = "journeys"

  /**
   * Generates a new unique identifier (UID) for a journey.
   *
   * @return A new UID as a String.
   */
  override fun getNewUid(): String {
    return db.collection(collectionPath).document().id
  }

  // Clearly ask TODO it
  override fun init(onSuccess: () -> Unit) {
    Firebase.auth.addAuthStateListener {
      if (it.currentUser != null) {
        onSuccess()
      }
    }
  }

  /**
   * Retrieves all journeys from the Firestore database.
   *
   * @param onSuccess The callback to call with the list of journeys if the operation is successful.
   * @param onFailure The callback to call if the operation fails.
   */
  override fun getJourneys(onSuccess: (List<Journey>) -> Unit, onFailure: (Exception) -> Unit) {
    Log.d("JourneysRepositoryFirestore", "getjourneys")
    db.collection(collectionPath).get().addOnCompleteListener { task ->
      if (task.isSuccessful) {
        val journeys =
            task.result?.mapNotNull { document -> documentTojourney(document) } ?: emptyList()
        onSuccess(journeys)
      } else {
        task.exception?.let { e ->
          Log.e("JourneysRepositoryFirestore", "Error getting documents", e)
          onFailure(e)
        }
      }
    }
  }

  /**
   * Adds a new journey to the Firestore database.
   *
   * @param journey The journey object to add.
   * @param onSuccess The callback to call if the operation is successful.
   * @param onFailure The callback to call if the operation fails.
   */
  override fun addJourney(journey: Journey, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    performFirestoreOperation(
        db.collection(collectionPath).document(journey.uid).set(journey), onSuccess, onFailure)
  }

  /**
   * Updates an existing journey in the Firestore database.
   *
   * @param journey The journey object to update.
   * @param onSuccess The callback to call if the operation is successful.
   * @param onFailure The callback to call if the operation fails.
   */
  override fun updateJourney(
      journey: Journey,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    performFirestoreOperation(
        db.collection(collectionPath).document(journey.uid).set(journey), onSuccess, onFailure)
  }

  /**
   * Deletes a journey by its ID.
   *
   * @param id The ID of the journey to delete.
   * @param onSuccess The callback to call if the operation is successful.
   * @param onFailure The callback to call if the operation fails.
   */
  override fun deleteJourneyById(
      id: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    performFirestoreOperation(
        db.collection(collectionPath).document(id).delete(), onSuccess, onFailure)
  }

  /**
   * Performs a Firestore operation and calls the appropriate callback based on the result.
   *
   * @param task The Firestore task to perform.
   * @param onSuccess The callback to call if the operation is successful.
   * @param onFailure The callback to call if the operation fails.
   */
  private fun performFirestoreOperation(
      task: Task<Void>,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    task.addOnCompleteListener { result ->
      if (result.isSuccessful) {
        onSuccess()
      } else {
        result.exception?.let { e ->
          Log.e("JourneysRepositoryFirestore", "Error performing Firestore operation", e)
          onFailure(e)
        }
      }
    }
  }

  /**
   * Converts a Firestore document to a Journey object.
   *
   * @param document The Firestore document to convert.
   * @return The Journey object.
   */
  private fun documentTojourney(document: DocumentSnapshot): Journey? {
    return try {
      val uid = document.id
      val imageUrl = document.getString("imageUrl") ?: return null
      val description = document.getString("description") ?: return null
      val locationData = document["location"] as? Map<*, *> ?: return null
      val location =
          locationData.let {
            Location(
                latitude = it["latitude"] as? Double ?: 0.0,
                longitude = it["longitude"] as? Double ?: 0.0,
                name = it["name"] as? String ?: "home")
          }
      val originString = document.getString("coffeeOrigin") ?: return null
      val coffeeOrigin = CoffeeOrigin.valueOf(originString)
      val methodString = document.getString("brewingMethod") ?: return null
      val brewingMethod = BrewingMethod.valueOf(methodString)
      val tasteString = document.getString("coffeeTaste") ?: return null
      val coffeeTaste = CoffeeTaste.valueOf(tasteString)
      val rateString = document.getString("coffeeRate") ?: return null
      val coffeeRate = CoffeeRate.valueOf(rateString)
      val date = document.getTimestamp("date") ?: return null
      Journey(
          uid = uid,
          imageUrl = imageUrl,
          description = description,
          location = location,
          coffeeOrigin = coffeeOrigin,
          brewingMethod = brewingMethod,
          coffeeTaste = coffeeTaste,
          coffeeRate = coffeeRate,
          date = date)
    } catch (e: Exception) {
      Log.e("JourneysRepositoryFirestore", "Error converting document to Journey", e)
      null
    }
  }
}
