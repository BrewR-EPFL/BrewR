package com.android.brewr.ui.userProfile

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.android.brewr.ui.navigation.NavigationActions
import com.android.brewr.ui.navigation.Screen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class UserPrivateListScreenTest {

  @get:Rule val composeTestRule = createComposeRule()
  private lateinit var navigationActions: NavigationActions

  @Before
  fun setUp() {
    // Mock NavController
    navigationActions = mock()
    `when`(navigationActions.currentRoute()).thenReturn(Screen.USER_PRIVATE_LIST)
  }

  @Test
  fun userPrivateListScreen_displaysComponents() {
    composeTestRule.setContent { UserPrivateListScreen(navigationActions) }
    composeTestRule.onNodeWithTag("UserPrivateListScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("topBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("privateList").assertIsDisplayed()
    composeTestRule.onNodeWithTag("goBackButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("coffeeImage:1").performClick()
  }

  @Test
  fun userPrivateListScreen_goBackButton() {
    composeTestRule.setContent { UserPrivateListScreen(navigationActions) }
    composeTestRule.onNodeWithTag("goBackButton").performClick()
    verify(navigationActions).goBack()
  }
}