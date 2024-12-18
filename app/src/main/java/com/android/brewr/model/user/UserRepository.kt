package com.android.brewr.model.user

import android.net.Uri

/**
 * Interface defining user repository operations for the BrewR application. Provides methods to
 * fetch user-related data such as Gmail, username, and profile picture.
 */
interface UserRepository {
  /**
   * Retrieves the user's Gmail address.
   *
   * @param onSuccess A callback invoked with the Gmail address as a [String] if the operation is
   *   successful. The value can be null if no Gmail address is found.
   * @param onFailure A callback invoked with an [Exception] if the operation fails.
   */
  fun getUserGmail(onSuccess: (String?) -> Unit, onFailure: (Exception) -> Unit)

  /**
   * Retrieves the user's display name.
   *
   * @param onSuccess A callback invoked with the username as a [String] if the operation is
   *   successful. The value can be null if no username is found.
   * @param onFailure A callback invoked with an [Exception] if the operation fails.
   */
  fun getUsername(onSuccess: (String?) -> Unit, onFailure: (Exception) -> Unit)

  /**
   * Retrieves the user's display name.
   *
   * @param onSuccess A callback invoked with the username as a [String] if the operation is
   *   successful. The value can be null if no username is found.
   * @param onFailure A callback invoked with an [Exception] if the operation fails.
   */
  fun getProfilePicture(onSuccess: (Uri?) -> Unit, onFailure: (Exception) -> Unit)
}
