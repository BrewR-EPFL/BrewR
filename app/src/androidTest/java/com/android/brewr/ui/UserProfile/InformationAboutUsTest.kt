package com.android.brewr.ui.UserProfile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.android.brewr.ui.navigation.NavigationActions
import com.android.brewr.ui.userProfile.InformationAboutUsScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock

class InformationAboutUsTest {
  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions

  @Before
  fun setUp() {
    // Mock NavController
    navigationActions = mock(NavigationActions::class.java)

    // Set up the screen before each test
    composeTestRule.setContent { InformationAboutUsScreen(navigationActions) }
  }

  @Test
  fun testProfileScreenUI() {
    composeTestRule.onNodeWithTag("topBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("goBackButton").performClick()

    composeTestRule.onNodeWithTag("text1").assertIsDisplayed()
    composeTestRule.onNodeWithTag("text2").assertIsDisplayed()
    composeTestRule.onNodeWithTag("text3").assertIsDisplayed()
  }
}
