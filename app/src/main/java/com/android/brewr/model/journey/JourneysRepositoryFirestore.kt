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
   * override fun getJourneys(onSuccess: (List<Journey>) -> Unit, onFailure: (Exception) -> Unit) {
   * Log.d("JourneysRepositoryFirestore", "getjourneys")
   * db.collection(collectionPath).get().addOnCompleteListener { task -> if (task.isSuccessful) {
   * val journeys = task.result?.mapNotNull { document -> documentTojourney(document) } ?:
   * emptyList() onSuccess(journeys) } else { task.exception?.let { e ->
   * Log.e("JourneysRepositoryFirestore", "Error getting documents", e) onFailure(e) } } } }
   */
  override fun getJourneys(onSuccess: (List<Journey>) -> Unit, onFailure: (Exception) -> Unit) {
    db.collection(collectionPath).addSnapshotListener { snapshot, error ->
      if (error != null) {
        Log.e("JourneysRepositoryFirestore", "Error listening to snapshots", error)
        onFailure(error)
        return@addSnapshotListener
      }

      if (snapshot != null && !snapshot.isEmpty) {
        val journeys = snapshot.documents.mapNotNull { document -> documentTojourney(document) }
        onSuccess(journeys)
      } else {
        onSuccess(emptyList()) // Pass an empty list if there are no documents
      }
    }
  }

  override fun addJourney(journey: Journey, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    performFirestoreOperation(
        db.collection(collectionPath).document(journey.uid).set(journey), onSuccess, onFailure)
  }

  override fun updateJourney(
      journey: Journey,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    performFirestoreOperation(
        db.collection(collectionPath).document(journey.uid).set(journey), onSuccess, onFailure)
  }

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
      val locationData = document.get("location") as? Map<*, *> ?: return null
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
