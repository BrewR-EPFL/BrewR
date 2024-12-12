package com.android.brewr.model.coffee

import android.util.Log
import com.android.brewr.model.location.Location
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class FavoriteCoffeesRepositoryFirestore(
    private val db: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : FavoriteCoffeesRepository {
  private val collectionPath = "coffees"
  private val userPath = "users"
  private var currentUserUid = ""

  /**
   * Initializes the repository by adding an authentication state listener and retrieving the
   * current user's data.
   *
   * This function listens for changes in the authentication state. If a user is logged in, it
   * retrieves the user's document from Firestore using their UID. If the document exists, it
   * invokes the provided success callback. Any errors during the document retrieval are logged for
   * debugging purposes.
   *
   * @param onSuccess A callback function to be invoked when the user's document is successfully
   *   retrieved.
   */
  override fun init(onSuccess: () -> Unit) {
    firebaseAuth.addAuthStateListener {
      val user = Firebase.auth.currentUser
      if (user != null) {
        currentUserUid = user.uid
        db.collection(userPath)
            .document(currentUserUid)
            .get()
            .addOnSuccessListener { document ->
              if (document.exists()) {
                onSuccess()
              }
            }
            .addOnFailureListener { e -> Log.e("UserCheck", "Error getting user document", e) }
      } else {
        Log.e("UserCheck", "User is not logged in")
      }
    }
  }

  /**
   * Retrieves the list of coffee documents associated with the current user.
   *
   * This function listens for changes to the user's coffee list in Firestore and retrieves the
   * corresponding coffee documents. It uses Firestore snapshot listeners to provide real-time
   * updates. If there is an error during any Firestore operation, the provided failure callback is
   * invoked with the exception.
   *
   * @param onSuccess A callback function invoked with the list of retrieved `Coffee` objects when
   *   the operation is successful.
   * @param onFailure A callback function invoked with an `Exception` if an error occurs during the
   *   operation.
   */
  override fun getCoffees(onSuccess: (List<Coffee>) -> Unit, onFailure: (Exception) -> Unit) {
    db.collection(userPath).document(getCurrentUserUid()).addSnapshotListener {
        userSnapshot,
        userError ->
      if (userError != null) {
        Log.d("CoffeesRepositoryFirestore", "Error listening to user snapshots", userError)
        onFailure(userError)
        return@addSnapshotListener
      }

      val coffeeIds = userSnapshot?.get("coffees") as? List<String> ?: emptyList()
      if (coffeeIds.isEmpty()) {
        onSuccess(emptyList())
        return@addSnapshotListener
      }

      db.collection(collectionPath).whereIn("id", coffeeIds).addSnapshotListener {
          coffeeSnapshot,
          coffeeError ->
        if (coffeeError != null) {
          Log.d("CoffeesRepositoryFirestore", "Error listening to coffee snapshots", coffeeError)
          onFailure(coffeeError)
          return@addSnapshotListener
        }

        if (coffeeSnapshot != null && !coffeeSnapshot.isEmpty) {
          val coffees = coffeeSnapshot.documents.mapNotNull { documentToCoffee(it) }
          Log.d("CoffeesRepositoryFirestore", "coffee Id:${coffees[0].id}")
          onSuccess(coffees)
        } else {
          onSuccess(emptyList()) // Pass an empty list if there are no documents
        }
      }
    }
  }

  /**
   * Adds a coffee document to Firestore and associates it with the current user.
   *
   * This function uses a Firestore batch operation to:
   * 1. Add the coffee ID to the user's list of coffees in their document.
   * 2. Save the coffee document in the main coffee collection. The operation is committed
   *    atomically. If successful, the provided success callback is invoked. If an error occurs
   *    during the batch operation, the failure callback is invoked with the exception.
   *
   * @param coffee The `Coffee` object to be added to Firestore.
   * @param onSuccess A callback function invoked when the coffee is successfully added.
   * @param onFailure A callback function invoked with an `Exception` if an error occurs.
   */
  override fun addCoffee(coffee: Coffee, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    val batch = db.batch()
    batch.update(
        db.collection(userPath).document(getCurrentUserUid()),
        "coffees",
        FieldValue.arrayUnion(coffee.id))
    batch.set(db.collection(collectionPath).document(coffee.id), coffee)
    batch
        .commit()
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { exception -> onFailure(exception) }
  }

  /**
   * Removes a coffee ID from the current user's list of coffees in Firestore.
   *
   * This function uses a Firestore batch operation to atomically remove the specified coffee ID
   * from the "coffees" array field in the user's document. If the operation is successful, the
   * provided success callback is invoked. If an error occurs, the failure callback is invoked with
   * the exception.
   *
   * @param id The ID of the coffee to be removed from the user's list.
   * @param onSuccess A callback function invoked when the coffee ID is successfully removed.
   * @param onFailure A callback function invoked with an `Exception` if an error occurs.
   */
  override fun deleteCoffeeById(id: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    val batch = db.batch()
    val userRef = db.collection("users").document(getCurrentUserUid())
    batch.update(userRef, "coffees", FieldValue.arrayRemove(id))
    batch
        .commit()
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { exception -> onFailure(exception) }
  }

  /**
   * Retrieves the UID of the currently authenticated user.
   *
   * This function ensures that a user is logged in before attempting to fetch the UID. If no user
   * is logged in, an `IllegalStateException` is thrown.
   *
   * @return The UID of the currently authenticated user.
   * @throws IllegalStateException if no user is logged in.
   */
  private fun getCurrentUserUid(): String {
    val user = firebaseAuth.currentUser ?: throw IllegalStateException("User not logged in")
    val uid = user.uid
    return uid
  }

  /**
   * Converts a Firestore document snapshot into a `Coffee` object.
   *
   * This function extracts the required fields from the document snapshot to construct a `Coffee`
   * object. If any required fields are missing or an error occurs during extraction, the function
   * logs the error and returns `null`.
   *
   * @param document The Firestore document snapshot to be converted.
   * @return A `Coffee` object if the conversion is successful, or `null` if an error occurs.
   */
  private fun documentToCoffee(document: DocumentSnapshot): Coffee? {
    return try {
      val id = document.id
      val coffeeShopName = document.getString("coffeeShopName") ?: return null
      val locationData = document["location"] as? Map<*, *> ?: return null
      val location =
          locationData.let {
            Location(
                latitude = it["latitude"] as? Double ?: 0.0,
                longitude = it["longitude"] as? Double ?: 0.0,
                address = it["address"] as? String ?: "")
          }
      val rating = document.getDouble("rating") ?: 0.0
      val hoursList =
          (document.get("hours") as? List<Map<String, String>>)?.mapNotNull { hourMap ->
            val day = hourMap["day"]
            val open = hourMap["open"]
            val close = hourMap["close"]
            if (day != null && open != null && close != null) {
              Hours(day, open, close)
            } else {
              null
            }
          } ?: emptyList()
      val reviewsList =
          (document.get("reviews") as? List<Map<String, Any>>)?.mapNotNull { reviewMap ->
            val authorName = reviewMap["authorName"] as? String
            val review = reviewMap["review"] as? String
            val rating = (reviewMap["rating"] as? Double) ?: 0.0
            if (authorName != null && review != null) {
              Review(authorName, review, rating)
            } else {
              null
            }
          }
      val imagesUrls = document.get("imagesUrls") as? List<String> ?: emptyList()

      Coffee(id, coffeeShopName, location, rating, hoursList, reviewsList, imagesUrls)
    } catch (e: Exception) {
      Log.e("CoffeesRepositoryFirestore", "Error converting document to Coffee", e)
      null
    }
  }
}
