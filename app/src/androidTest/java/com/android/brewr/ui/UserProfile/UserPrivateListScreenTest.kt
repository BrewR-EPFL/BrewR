package com.android.brewr.ui.UserProfile

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.android.brewr.model.coffee.CoffeesViewModel
import com.android.brewr.ui.navigation.NavigationActions
import com.android.brewr.ui.userProfile.UserPrivateListScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify

class UserPrivateListScreenTest {

  @get:Rule val composeTestRule = createComposeRule()
  private lateinit var navigationActions: NavigationActions
  private lateinit var coffeesViewModel: CoffeesViewModel

  @Before
  fun setUp() {
    // Mock NavController
    navigationActions = mock(NavigationActions::class.java)
    coffeesViewModel = spy(CoffeesViewModel::class.java)
  }

  @Test
  fun userPrivateListScreen_displaysComponents() {
    composeTestRule.setContent { UserPrivateListScreen(navigationActions, coffeesViewModel) }
    composeTestRule.onNodeWithTag("UserPrivateListScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("topBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("privateList").assertIsDisplayed()
    composeTestRule.onNodeWithTag("goBackButton").assertIsDisplayed()
    //composeTestRule.onNodeWithTag("coffeeImage:1").performClick()
  }

  @Test
  fun userPrivateListScreen_goBackButton() {
    composeTestRule.setContent { UserPrivateListScreen(navigationActions, coffeesViewModel) }
    composeTestRule.onNodeWithTag("goBackButton").performClick()
    verify(navigationActions).goBack()
  }
}
