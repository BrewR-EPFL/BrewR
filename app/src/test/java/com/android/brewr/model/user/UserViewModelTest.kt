package com.android.brewr.model.user

import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class UserViewModelTest {
  private lateinit var userRepository: UserRepository
  private lateinit var userViewModel: UserViewModel

  @Before
  fun setup() {
    userRepository = mock(UserRepository::class.java)
    // `when`(userRepository.getUserGmail(any(), any())).thenReturn("test@gmail.com")
    userViewModel = UserViewModel(userRepository)
  }

  @Test
  fun `updateUserInfo() calls the repository`() {

    userViewModel.updateUserInfo()

    // one call for init, one call for updateUserInfo() call
    verify(userRepository, times(2)).getUserGmail(any(), any())
    verify(userRepository, times(2)).getProfilePicture(any(), any())
    verify(userRepository, times(2)).getUsername(any(), any())
  }

  @Test
  fun `setUsername() calls the repository`() {

    val onSuccess: () -> Unit = mock()
    val onFailure: (Exception) -> Unit = mock()
    val username = "newUsername"

    userViewModel.setUsername(username, onSuccess, onFailure)
    verify(userRepository).setUsername(eq(username), any(), any())
  }

  @Test
  fun `setUserName() refreshes the username`() {

    val onSuccess: () -> Unit = mock()
    val onFailure: (Exception) -> Unit = mock()
    val username = "newUsername"

    userViewModel.setUsername(username, onSuccess, onFailure)
    verify(userRepository).getUsername(any(), any())
  }
}
