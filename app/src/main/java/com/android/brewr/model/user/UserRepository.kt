package com.android.brewr.model.user

interface UserRepository {
  fun getUserGmail(onSuccess: (String?) -> Unit, onFailure: (Exception) -> Unit)

  fun setUsername(username: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  fun getUsername(onSuccess: (String?) -> Unit, onFailure: (Exception) -> Unit)
}
