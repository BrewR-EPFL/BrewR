package com.android.brewr.ui.UserProfile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.brewr.model.user.UserViewModel
import com.android.brewr.ui.navigation.NavigationActions
import com.android.brewr.ui.userProfile.UserMainProfileScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock

@RunWith(AndroidJUnit4::class)
class UserMainProfileScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions

  @Before
  fun setUp() {
    // Mock NavController
    navigationActions = mock(NavigationActions::class.java)

    // Set up the screen before each test
    composeTestRule.setContent {
      val userViewModel: UserViewModel = viewModel(factory = UserViewModel.Factory)
      UserMainProfileScreen(userViewModel, navigationActions)
    }
  }

  @Test
  fun testProfileScreenUI() {

    // Verify top bar and back button
    composeTestRule.onNodeWithTag("topBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("goBackButton").performClick()

    // Verify username and email
    composeTestRule.onNodeWithTag("Username").assertTextEquals("Username")
    composeTestRule.onNodeWithTag("User Email").assertTextEquals("pablorobin4@gmail.com")
    composeTestRule.onNodeWithTag("User Profile Photo").assertIsDisplayed()

    // Test clicking unimplemented feature buttons
    composeTestRule.onNodeWithTag("Favorite button").performClick()
    composeTestRule.onNodeWithTag("Notification button").performClick()
    composeTestRule.onNodeWithTag("TBD button").performClick()
    composeTestRule.onNodeWithTag("anything").performClick()

    // Test clicking the "sign out" button
    composeTestRule.onNodeWithTag("sign out").performClick()

    // Verify the AlertDialog is displayed
    composeTestRule.onNodeWithTag("Alter dialog").assertIsDisplayed()
    composeTestRule.onNodeWithText("Confirmation logout").assertIsDisplayed()

    // Test confirming logout
    composeTestRule.onNodeWithTag("button Yes").performClick()
  }

  @Test
  fun testLogoutCancel() {

    // Click the sign out button
    composeTestRule.onNodeWithTag("sign out").performClick()

    // Verify AlertDialog is displayed
    composeTestRule.onNodeWithTag("Alter dialog").assertIsDisplayed()

    // Click "No" to dismiss
    composeTestRule.onNodeWithTag("button No").performClick()

    // Verify AlertDialog is dismissed
    composeTestRule.onNodeWithTag("Alter dialog").assertDoesNotExist()
  }
}
