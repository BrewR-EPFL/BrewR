package com.android.brewr.model.user

import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.kotlin.any
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
}
