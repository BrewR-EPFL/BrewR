package com.android.brewr.model.user

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Implementation of [UserRepository] using Firestore and Firebase Authentication.
 *
 * This class provides methods to retrieve user data, such as Gmail, username, and profile picture,
 * from Firebase services.
 *
 * @property db An instance of [FirebaseFirestore] to interact with Firestore.
 * @property firebaseAuth An instance of [FirebaseAuth] to retrieve authenticated user information.
 */
class UserRepositoryFirestore(
    private val db: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : UserRepository {
  companion object {
    const val LOG_TAG = "UserRepository"
    const val LOG_MSG = "User not found"
    const val EXCEPTION_MSG = "User is not logged in"
  }

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
      Log.e(LOG_TAG, LOG_MSG)
      onFailure(Exception(EXCEPTION_MSG))
    }
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
      Log.e(LOG_TAG, LOG_MSG)
      onFailure(Exception(EXCEPTION_MSG))
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
      Log.e(LOG_TAG, LOG_MSG)
      onFailure(Exception(EXCEPTION_MSG))
    }
  }
}
