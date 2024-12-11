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

  override fun deleteCoffeeById(id: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    val batch = db.batch()
    val userRef = db.collection("users").document(getCurrentUserUid())
    batch.update(userRef, "coffees", FieldValue.arrayRemove(id))
    batch
        .commit()
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { exception -> onFailure(exception) }
  }

  private fun getCurrentUserUid(): String {
    val user = firebaseAuth.currentUser ?: throw IllegalStateException("User not logged in")
    val uid = user.uid
    return uid
  }

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
