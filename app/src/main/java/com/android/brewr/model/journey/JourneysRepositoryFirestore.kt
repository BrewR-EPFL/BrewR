package com.android.brewr.model.journey

import android.util.Log
import com.android.brewr.model.coffee.CoffeeShop
import com.android.brewr.model.coffee.Hours
import com.android.brewr.model.coffee.Review
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

  /**
   * Initializes the repository and creates a user document if it doesn't already exist.
   *
   * @param onSuccess The callback to call when initialization is successful.
   */
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
   * Retrieves all journeys of the current user from the Firestore database.
   *
   * @param onSuccess The callback to call with the list of journeys if the operation is successful.
   * @param onFailure The callback to call if the operation fails.
   */
  override fun getJourneys(onSuccess: (List<Journey>) -> Unit, onFailure: (Exception) -> Unit) {
    getJourneysOf(getCurrentUserUid(), onSuccess, onFailure)
  }

  /**
   * Retrieves all journeys of users other than current user from the Firestore database.
   *
   * @param onSuccess The callback to call with the map of user uid and list of journeys if the
   *   operation is successful.
   * @param onFailure The callback to call if the operation fails.
   */
  override fun retrieveJourneysOfAllOtherUsers(
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
            getJourneysOf(
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
    Log.v("update journey", journey.toString())
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
  private fun documentToJourney(document: DocumentSnapshot): Journey? {
    return try {
      val uid = document.id
      val imageUrl = document.getString("imageUrl") ?: return null
      val description = document.getString("description") ?: return null

      val coffeeShopData = document.get("coffeeShop") as? Map<*, *> ?: return null
      val locationData = coffeeShopData["location"] as? Map<*, *> ?: return null

      val location =
          Location(
              latitude = locationData["latitude"] as? Double ?: 0.0,
              longitude = locationData["longitude"] as? Double ?: 0.0,
              name = locationData["name"] as? String ?: "")
      val hoursData = coffeeShopData["hours"] as? Map<String, Map<String, String>> ?: emptyMap()
      val hours = hoursData.map { Hours(it.key, it.value["open"] ?: "", it.value["close"] ?: "") }
      val reviewsData = coffeeShopData["reviews"] as? List<Map<String, Any>> ?: emptyList()
      val reviews =
          reviewsData.map {
            Review(
                authorName = it["author"] as? String ?: "",
                review = it["comment"] as? String ?: "",
                rating = it["rating"] as? Double ?: 0.0)
          }

      val coffeeShop =
          CoffeeShop(
              id = coffeeShopData["id"] as? String ?: "",
              coffeeShopName = coffeeShopData["coffeeShopName"] as? String ?: "",
              location = location,
              rating = coffeeShopData["rating"] as? Double ?: 0.0,
              hours = hours,
              reviews = reviews,
              imagesUrls = coffeeShopData["imagesUrls"] as? List<String> ?: emptyList())
      val coffeeOrigin = CoffeeOrigin.valueOf(document.getString("coffeeOrigin") ?: return null)
      val brewingMethod = BrewingMethod.valueOf(document.getString("brewingMethod") ?: return null)
      val coffeeTaste = CoffeeTaste.valueOf(document.getString("coffeeTaste") ?: return null)
      val coffeeRate = CoffeeRate.valueOf(document.getString("coffeeRate") ?: return null)
      val date = document.getTimestamp("date") ?: return null

      // Create Journey
      Journey(
          uid = uid,
          imageUrl = imageUrl,
          description = description,
          coffeeShop = coffeeShop,
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
   * Retrieves the current logged-in user's UID.
   *
   * @return The UID of the logged-in user.
   * @throws IllegalStateException If the user is not logged in.
   */
  private fun getCurrentUserUid(): String {
    val user = firebaseAuth.currentUser ?: throw IllegalStateException("User not logged in")
    val uid = user.uid
    return uid
  }

  /**
   * get all journeys of the specific user
   *
   * @param uid The uid of the user that you want to know
   * @param onSuccess The callback to call with the list of journeys if the operation is successful.
   * @param onFailure The callback to call if the operation fails.
   */
  private fun getJourneysOf(
      uid: String,
      onSuccess: (List<Journey>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    db.collection(userPath).document(uid).addSnapshotListener { userSnapshot, userError ->
      if (userError != null) {
        Log.e("JourneysRepositoryFirestore", "Error listening to user snapshots", userError)
        onFailure(userError)
        return@addSnapshotListener
      }

      val journeyIds = userSnapshot?.get("journeys") as? List<String> ?: emptyList()
      if (journeyIds.isEmpty()) {
        onSuccess(emptyList())
        return@addSnapshotListener
      }

      db.collection(collectionPath).whereIn("uid", journeyIds).addSnapshotListener {
          journeySnapshot,
          journeyError ->
        if (journeyError != null) {
          Log.e("JourneysRepositoryFirestore", "Error listening to journey snapshots", journeyError)
          onFailure(journeyError)
          return@addSnapshotListener
        }

        if (journeySnapshot != null && !journeySnapshot.isEmpty) {
          val journeys = journeySnapshot.documents.mapNotNull { documentToJourney(it) }
          onSuccess(journeys)
        } else {
          onSuccess(emptyList()) // Pass an empty list if there are no documents
        }
      }
    }
  }
}
