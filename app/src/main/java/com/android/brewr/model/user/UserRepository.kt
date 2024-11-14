package com.android.brewr.model.user

import android.net.Uri

interface UserRepository {
  fun getUserGmail(onSuccess: (String?) -> Unit, onFailure: (Exception) -> Unit)

  fun getUsername(onSuccess: (String?) -> Unit, onFailure: (Exception) -> Unit)

  fun getProfilePicture(onSuccess: (Uri?) -> Unit, onFailure: (Exception) -> Unit)
}
