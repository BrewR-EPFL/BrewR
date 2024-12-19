package com.android.brewr.ui.authentication

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.brewr.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignInScreenTest {

  @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

  @Test
  fun testSignInScreenElementsVisible() {
    // Verify that all key elements are displayed on the screen
    composeTestRule.onNodeWithTag("loginScreen").assertExists()
    composeTestRule.onNodeWithTag("appLogo", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("loginButton", useUnmergedTree = true)
        .assertExists()
        .assertHasClickAction()
    composeTestRule.onNodeWithTag("googleLogo", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("signInText", useUnmergedTree = true)
        .assertIsDisplayed()
        .assertTextEquals("Sign in with Google")
  }
}
