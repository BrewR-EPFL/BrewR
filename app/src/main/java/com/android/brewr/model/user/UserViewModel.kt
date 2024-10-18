package com.android.brewr.model.user

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserViewModel(private val repository: UserRepository) : ViewModel() {
  private val username_ = MutableStateFlow<String?>(null)
  val username: StateFlow<String?> = username_.asStateFlow()

  private val userEmail_ = MutableStateFlow<String?>(null)
  val userEmail: StateFlow<String?> = userEmail_.asStateFlow()

  private val userProfilePicture_ = MutableStateFlow<Uri?>(null)
  val userProfilePicture: StateFlow<Uri?> = userProfilePicture_.asStateFlow()

  init {
    Log.d("ViewModelInit", "it is initiated")
    fetchUserGmail()
    fetchUsername()
    fetchProfilePicture()
  }

  /** Fetches the user's Gmail from the repository and updates the userEmail_ state. */
  private fun fetchUserGmail() {
    repository.getUserGmail(onSuccess = { userEmail_.value = it }, onFailure = {})
  }

  /** Fetches the current username from the repository and updates the username_ state. */
  private fun fetchUsername() {
    repository.getUsername(onSuccess = { username_.value = it }, onFailure = {})
  }

  /**
   * Fetches the current profile picture from the repository and updates the userProfilePicture
   * state.
   */
  private fun fetchProfilePicture() {
    repository.getProfilePicture(onSuccess = { userProfilePicture_.value = it }, onFailure = {})
  }

  /**
   * Sets a new username for the user.
   *
   * @param username The new username to set.
   * @param onSuccess Callback invoked on success.
   * @param onFailure Callback invoked on failure.
   */
  fun setUsername(username: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    repository.setUsername(
        username,
        onSuccess = {
          fetchUsername() // Refresh the username state after setting it
          onSuccess()
        },
        onFailure = { e -> onFailure(e) })
  }

  fun updateUserInfo() {
    fetchUserGmail()
    fetchUsername()
    fetchProfilePicture()
    Log.d("ViewModelUpdated", "it is updated")
  }

  companion object {
    val Factory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            // Provide the repository instance here
            return UserViewModel(UserRepositoryFirestore(Firebase.firestore)) as T
          }
        }
  }
}
