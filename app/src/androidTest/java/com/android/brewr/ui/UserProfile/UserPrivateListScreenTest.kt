package com.android.brewr.ui.userProfile

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.android.brewr.ui.navigation.NavigationActions
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify

class UserPrivateListScreenTest {

  @get:Rule val composeTestRule = createComposeRule()
  private lateinit var navigationActions: NavigationActions

  @Before
  fun setUp() {
    // Mock NavController
    navigationActions = mock(NavigationActions::class.java)
  }

  @Test
  fun userPrivateListScreen_displaysComponents() {
    composeTestRule.setContent { UserPrivateListScreen(navigationActions) }

    composeTestRule.onNodeWithTag("UserPrivateListScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("topBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("privateList").assertIsDisplayed()
    composeTestRule.onNodeWithTag("coffeeImage:1").performClick()
    composeTestRule.onNodeWithTag("goBackButton").assertIsDisplayed().performClick()
    verify(navigationActions).goBack()
  }
}
