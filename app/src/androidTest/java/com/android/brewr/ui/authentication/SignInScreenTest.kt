package com.android.brewr.ui.authentication

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.brewr.MainActivity
import org.junit.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignInScreenTest {

  @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

  @Test
  fun signInScreen_displaysCorrectly() {
    // Assert that all UI elements are displayed
    composeTestRule.onNodeWithContentDescription("App Logo").assertIsDisplayed()
    composeTestRule.onNodeWithTag("loginButton").assertIsDisplayed()
    composeTestRule.onNodeWithText("Sign in with Google").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Google Logo").assertIsDisplayed()
  }

  @Test
  fun testLoginButtonStartsSignInProcess() {
    // Verify that the login button exists and can be clicked
    val loginButton = composeTestRule.onNodeWithTag("loginButton")
    loginButton.assertHasClickAction()
    loginButton.performClick()

    // Verify that the loading spinner appears after the button is clicked
    composeTestRule.onNodeWithTag("loadingSpinner").assertIsDisplayed()

    // Verify that the login button is no longer displayed while loading
    loginButton.assertDoesNotExist()
  }
}
