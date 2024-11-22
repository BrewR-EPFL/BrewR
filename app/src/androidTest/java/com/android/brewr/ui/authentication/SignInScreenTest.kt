package com.android.brewr.ui.authentication

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.brewr.MainActivity
import org.junit.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignInScreenTest {

  @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

  @Test
  fun signInScreen_displaysCorrectly() {
    composeTestRule.onNodeWithContentDescription("App Logo").assertIsDisplayed()
    composeTestRule.onNodeWithTag("loginTitle").assertIsDisplayed()
    composeTestRule.onNodeWithText("BrewR").assertIsDisplayed()
    composeTestRule.onNodeWithTag("loginButton").assertIsDisplayed()
    composeTestRule.onNodeWithText("Sign in with Google").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Google Logo").assertIsDisplayed()
  }

  @Test
  fun testLoginButtonStartsSignInProcess() {
    // Click the login button
    composeTestRule.onNodeWithTag("loginButton").assertHasClickAction()
    composeTestRule.onNodeWithTag("loginButton").performClick()
  }
}
