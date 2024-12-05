package com.android.brewr.model.journey

import android.util.Log
import com.android.brewr.model.map.Location
import com.android.brewr.model.user.User
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class JourneysRepositoryFirestore(
    private val db: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : JourneysRepository {

  private val collectionPath = "journeys"
  private val userPath = "users"
  private var currentUserUid = ""

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
    firebaseAuth.addAuthStateListener {
      val user = Firebase.auth.currentUser
      if (user != null) {
        currentUserUid = user.uid
        val currentUserName = user.email ?: ""
        db.collection(userPath)
            .document(currentUserUid)
            .get()
            .addOnSuccessListener { document ->
              if (document.exists()) {
                onSuccess()
              } else {
                val newUser =
                    User(uid = currentUserUid, name = currentUserName, journeys = emptyList())
                db.collection(userPath)
                    .document(currentUserUid)
                    .set(newUser)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { e ->
                      Log.e("UserCheck", "Error creating user document", e)
                    }
              }
            }
            .addOnFailureListener { e -> Log.e("UserCheck", "Error getting user document", e) }
      } else {
        Log.e("UserCheck", "User is not logged in")
      }
    }
  }

  /**
   * Retrieves all journeys of current user from the Firestore database.
   *
   * @param onSuccess The callback to call with the list of journeys if the operation is successful.
   * @param onFailure The callback to call if the operation fails.
   */
  override fun getJourneysOfCurrentUser(
      onSuccess: (List<Journey>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    getJourneysOfTheUser(getCurrentUserUid(), onSuccess, onFailure)
  }

  /**
   * Retrieves all journeys of users other than current user from the Firestore database.
   *
   * @param onSuccess The callback to call with the map of user uid and list of journeys if the
   *   operation is successful.
   * @param onFailure The callback to call if the operation fails.
   */
  override fun getJourneysOfAllOtherUsers(
      onSuccess: (List<Pair<List<Journey>, String>>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    // Fetch all user documents from the userPath collection
    db.collection(userPath)
        .get()
        .addOnSuccessListener { querySnapshot ->
          // Extract all user IDs (UIDs) from the retrieved documents
          val allUsers =
              querySnapshot.documents.mapNotNull { it.id }.filter { it != getCurrentUserUid() }

          // If there are no users, return an empty list and exit
          if (allUsers.isEmpty()) {
            onSuccess(emptyList())
            return@addOnSuccessListener
          }

          val resultList =
              mutableListOf<Pair<List<Journey>, String>>() // List to store results as pairs
          var completedCount = 0 // Counter to track how many requests have completed
          var hasErrorOccurred = false // Flag to track if any request has failed

          // Loop through each user UID and fetch their journeys
          allUsers.forEach { uid ->
            getJourneysOfTheUser(
                uid,
                onSuccess = { journeys ->
                  // Only process if no errors have occurred so far
                  if (!hasErrorOccurred) {
                    synchronized(resultList) {
                      resultList.add(journeys to uid) // Add the journeys and UID as a pair
                    }
                    completedCount++ // Increment the completed request count
                    // If all requests are completed, invoke onSuccess with the result list
                    if (completedCount == allUsers.size) {
                      onSuccess(resultList)
                    }
                  }
                },
                onFailure = { exception ->
                  // If any request fails, set the error flag and invoke onFailure
                  if (!hasErrorOccurred) {
                    hasErrorOccurred = true
                    onFailure(exception)
                  }
                })
          }
        }
        .addOnFailureListener { exception ->
          // Handle failure when fetching all users
          onFailure(exception)
        }
  }

  /**
   * Retrieves all journeys of a specific user from the Firestore database.
   *
   * @param onSuccess The callback to call with the list of journeys if the operation is successful.
   * @param onFailure The callback to call if the operation fails.
   */
  override fun getJourneysOfTheUser(
      uid: String,
      onSuccess: (List<Journey>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    db.collection(userPath)
        .document(uid)
        .get()
        .addOnSuccessListener { document ->
          val journeyIds = document.get("journeys") as? List<String> ?: emptyList()
          if (journeyIds.isEmpty()) {
            onSuccess(emptyList())
            return@addOnSuccessListener
          }
          db.collection(collectionPath)
              .whereIn("uid", journeyIds)
              .get()
              .addOnSuccessListener { querySnapshot ->
                val journeys = querySnapshot.documents.mapNotNull { documentTojourney(it) }
                onSuccess(journeys)
              }
              .addOnFailureListener { onFailure(it) }
        }
        .addOnFailureListener { onFailure(it) }
  }

  /**
   * Adds a new journey to the Firestore database.
   *
   * @param journey The journey object to add.
   * @param onSuccess The callback to call if the operation is successful.
   * @param onFailure The callback to call if the operation fails.
   */
  override fun addJourney(journey: Journey, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    val batch = db.batch()
    batch.update(
        db.collection(userPath).document(getCurrentUserUid()),
        "journeys",
        FieldValue.arrayUnion(journey.uid))
    batch.set(db.collection(collectionPath).document(journey.uid), journey)

    batch
        .commit()
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { exception -> onFailure(exception) }
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
    val batch = db.batch()
    val userRef = db.collection("users").document(getCurrentUserUid())
    batch.update(userRef, "journeys", FieldValue.arrayRemove(id))
    batch.delete(db.collection(collectionPath).document(id))
    batch
        .commit()
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { exception -> onFailure(exception) }
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

  private fun getCurrentUserUid(): String {
    val user = firebaseAuth.currentUser ?: throw IllegalStateException("User not logged in")
    val uid = user.uid
    return uid
  }
}
