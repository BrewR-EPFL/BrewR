package com.android.brewr.model.user

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

class UserRepositoryFirestore(private val db: FirebaseFirestore) : UserRepository {

  private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

  /**
   * Retrieves the current user's Gmail address.
   *
   * @return User's Gmail address or null (if the user is not logged in or doesn't have a Gmail
   *   associated).
   */
  override fun getUserGmail(onSuccess: (String?) -> Unit, onFailure: (Exception) -> Unit) {
    val user: FirebaseUser? = firebaseAuth.currentUser
    if (user != null) {
      onSuccess(user.email)
    } else {
      Log.e("UserRepository", "User not found")
      onFailure(Exception("User not logged in"))
    }
  }

  override fun setUsername(
      username: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val user: FirebaseUser? = firebaseAuth.currentUser
    user?.let {
      val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(username).build()

      user.updateProfile(profileUpdates).addOnCompleteListener { task ->
        if (task.isSuccessful) {
          onSuccess()
        } else {
          task.exception?.let { exception -> onFailure(exception) }
              ?: onFailure(Exception("Unknown error occurred"))
        }
      }
    } ?: onFailure(Exception("User is not logged in"))
  }

  /**
   * Retrieves the current user's username.
   *
   * @return The user's display name (username), or null (if the user is not logged in or has not
   *   set a username).
   */
  override fun getUsername(onSuccess: (String?) -> Unit, onFailure: (Exception) -> Unit) {
    val user: FirebaseUser? = firebaseAuth.currentUser
    if (user != null) {
      onSuccess(user.displayName)
    } else {
      Log.e("UserRepository", "User not found")
      onFailure(Exception("User not logged in"))
    }
  }
}
