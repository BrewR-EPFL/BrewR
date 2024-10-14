package com.android.brewr.ui.authentication

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.brewr.ui.navigation.NavigationActions
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.Mockito.*

@RunWith(AndroidJUnit4::class)
class SignInScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions

  @Before
  fun setup() {
    navigationActions = mock()
  }

  @Test
  fun signInScreen_displaysCorrectly() {
    composeTestRule.setContent { SignInScreen(navigationActions) }
    composeTestRule.onNodeWithContentDescription("App Logo").assertIsDisplayed()
    composeTestRule.onNodeWithTag("loginTitle").assertIsDisplayed()
    composeTestRule.onNodeWithText("BrewR").assertIsDisplayed()
    composeTestRule.onNodeWithTag("loginButton").assertIsDisplayed()
    composeTestRule.onNodeWithText("Sign in with Google").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Google Logo").assertIsDisplayed()
    composeTestRule.onNodeWithTag("loginButton").assertHasClickAction()
  }

  @Test
  fun testLoginButtonStartsSignInProcess() {
    composeTestRule.setContent { SignInScreen(navigationActions) }
    // Click the login button
    composeTestRule.onNodeWithTag("loginButton").performClick()
  }
}
