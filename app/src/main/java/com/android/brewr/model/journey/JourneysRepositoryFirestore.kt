package com.android.brewr.model.journey

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class JourneysRepositoryFirestore(private val db: FirebaseFirestore) : JourneysRepository {

  private val collectionPath = "journeys"

  // Clearly ask TODO it
  override fun init(onSuccess: () -> Unit) {
    Firebase.auth.addAuthStateListener {
      if (it.currentUser != null) {
        onSuccess()
      }
    }
  }

  override fun getNewUid(): String {
    return db.collection(collectionPath).document().id
  }

  override fun addJourney(journey: Journey, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    performFirestoreOperation(
        db.collection(collectionPath).document(journey.uid).set(journey), onSuccess, onFailure)
  }

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

  override fun deleteJourneyById(
      id: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    performFirestoreOperation(
        db.collection(collectionPath).document(id).delete(), onSuccess, onFailure)
  }

  override fun updateJourney(
      journey: Journey,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    performFirestoreOperation(
        db.collection(collectionPath).document(journey.uid).set(journey), onSuccess, onFailure)
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
      val description = document.getString("description") ?: return null
      val imageUrl = document.getString("imageUrl") ?: return null
      val originString = document.getString("coffeeOrigin") ?: return null
      val coffeeOrigin = CoffeeOrigin.valueOf(originString)
      val coffeeShopName = document.getString("coffeeShopName") ?: return null
      val methodString = document.getString("brewingMethod") ?: return null
      val brewingMethod = BrewingMethod.valueOf(methodString)
      val rateString = document.getString("coffeeRate") ?: return null
      val coffeeRate = CoffeeRate.valueOf(rateString)
      val date = document.getTimestamp("date") ?: return null
      val tasteString = document.getString("coffeeTaste") ?: return null
      val coffeeTaste = CoffeeTaste.valueOf(tasteString)

      Journey(
          uid = uid,
          imageUrl = imageUrl,
          description = description,
          coffeeShopName = coffeeShopName,
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
}
