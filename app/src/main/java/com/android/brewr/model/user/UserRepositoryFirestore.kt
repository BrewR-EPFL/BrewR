package com.android.brewr.model.user

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

class UserRepositoryFirestore(
    private val db: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : UserRepository {

  /**
   * Retrieves the current user's Gmail address.
   *
   * @param onSuccess A callback triggered with user's Gmail address upon successful retrieval
   * @param onFailure A callback triggered with an exception upon unsuccessful retrieval (null user)
   */
  override fun getUserGmail(onSuccess: (String?) -> Unit, onFailure: (Exception) -> Unit) {
    val user: FirebaseUser? = firebaseAuth.currentUser
    if (user != null) {
      onSuccess(user.email)
    } else {
      Log.e("UserRepository", "User not found")
      onFailure(Exception("User is not logged in"))
    }
  }

  /**
   * Updates the current user's username.
   *
   * @param username The new username
   * @param onSuccess A callback triggered upon successful update of username
   * @param onFailure A callback triggered with an exception upon unsuccessful update of the
   *   username
   */
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
   * @param onSuccess A callback triggered with the user's display name upon successful retrieval
   * @param onFailure A callback triggered with an Exception upon unsuccessful retrieval (null user)
   */
  override fun getUsername(onSuccess: (String?) -> Unit, onFailure: (Exception) -> Unit) {
    val user: FirebaseUser? = firebaseAuth.currentUser
    if (user != null) {
      onSuccess(user.displayName)
    } else {
      Log.e("UserRepository", "User not found")
      onFailure(Exception("User is not logged in"))
    }
  }

  /**
   * Retrieves the current user's profile picture URI.
   *
   * @param onSuccess A callback triggered with the user's profile picture URI upon successful
   *   retrieval
   * @param onFailure A callback triggered with the user's profile picture URI upon unsuccessful
   *   retrieval
   */
  override fun getProfilePicture(onSuccess: (Uri?) -> Unit, onFailure: (Exception) -> Unit) {
    val user: FirebaseUser? = firebaseAuth.currentUser
    if (user != null) {
      onSuccess(user.photoUrl)
    } else {
      Log.e("UserRepository", "User not found")
      onFailure(Exception("User is not logged in"))
    }
  }
}
