package com.android.brewr.ui

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.android.brewr.model.journey.BrewingMethod
import com.android.brewr.model.journey.CoffeeOrigin
import com.android.brewr.model.journey.CoffeeTaste
import com.android.brewr.model.journey.JourneysRepository
import com.android.brewr.model.journey.ListJourneysViewModel
import com.android.brewr.ui.authentication.SignInScreen
import com.android.brewr.ui.navigation.NavigationActions
import com.android.brewr.ui.navigation.Route
import com.android.brewr.ui.navigation.Screen
import com.android.brewr.ui.overview.AddJourneyScreen
import com.android.brewr.ui.overview.EditJourneyScreen
import com.android.brewr.ui.overview.JourneyRecordScreen
import com.android.brewr.ui.overview.OverviewScreen
import com.android.brewr.ui.userProfile.UserMainProfileScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.spy
import org.mockito.Mockito.`when`
import org.mockito.kotlin.verify

@RunWith(AndroidJUnit4::class)
class E2ETest {

  private val appContext: Context = InstrumentationRegistry.getInstrumentation().targetContext

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var repositoryMock: JourneysRepository
  private lateinit var listJourneysViewModel: ListJourneysViewModel
  private lateinit var navigationActions: NavigationActions

  @Before
  fun setUp() {
    repositoryMock = mock(JourneysRepository::class.java)
    listJourneysViewModel = spy(ListJourneysViewModel(repositoryMock))
    navigationActions = mock(NavigationActions::class.java)

    `when`(navigationActions.currentRoute()).thenReturn(Screen.OVERVIEW)
  }

  @Test
  fun E2ETestFinal() {
    composeTestRule.setContent {
      Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        val navController = rememberNavController()
        val navigationActions = remember(navController) { NavigationActions(navController) }
        NavHost(navController = navController, startDestination = Route.OVERVIEW) {
          // Authentication flow
          navigation(startDestination = Screen.AUTH, route = Route.AUTH) {
            composable(Screen.AUTH) { SignInScreen(navigationActions) }
          }

          // Overview flow
          navigation(startDestination = Screen.OVERVIEW, route = Route.OVERVIEW) {
            composable(Screen.OVERVIEW) { OverviewScreen(listJourneysViewModel, navigationActions) }
            composable(Screen.USERPROFILE) { UserMainProfileScreen(navigationActions) }
            composable(Screen.JOURNEY_RECORD) {
              JourneyRecordScreen(listJourneysViewModel, navigationActions)
            }
          }

          // Journey flow
          navigation(startDestination = Screen.ADD_JOURNEY, route = Route.ADD_JOURNEY) {
            composable(Screen.ADD_JOURNEY) {
              AddJourneyScreen(listJourneysViewModel, navigationActions)
            }
            composable(Screen.EDIT_JOURNEY) {
              EditJourneyScreen(listJourneysViewModel, navigationActions)
            }
          }
        }
      }
    }

    // Assert the SignIn screen is shown first
    //   composeTestRule.onNodeWithTag("SignInScreen").assertIsDisplayed()

    // Navigate to the Overview screen
    // composeTestRule.onNodeWithTag("SignInButton").performClick()
    //     composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("overviewScreen").assertIsDisplayed()

    // Navigate to the User Profile screen
    composeTestRule.onNodeWithTag("accountButton").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("UserProfileScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("backButton").performClick()
    verify(navigationActions).goBack()

    // Navigate to Add Journey Screen
    composeTestRule.onNodeWithTag("AddJourneyButton").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("AddJourneyScreen").assertIsDisplayed()

    // Test Add Photo functionality (simulate photo click)
    composeTestRule.onNodeWithTag("addImageBox").assertIsDisplayed().assertHasClickAction()

    // Check if the description text field is displayed and type text into it
    composeTestRule
        .onNodeWithTag("inputJourneyDescription")
        .assertIsDisplayed()
        .performTextInput("Amazing Coffee Experience")

    // Test the coffee shop checkbox interaction
    composeTestRule.onNodeWithTag("coffeeShopCheckRow").assertHasClickAction().performClick()

    // After clicking, the Coffee Shop Name field should appear
    composeTestRule
        .onNodeWithTag("coffeeShopNameField")
        .assertExists()
        .performTextInput("Starbucks")

    // Test Coffee Origin dropdown (click and select an option)
    composeTestRule.onNodeWithTag("inputCoffeeOrigin").assertIsDisplayed().performClick()
    composeTestRule.onNodeWithTag("dropdownMenuCoffeeOrigin").assertExists()

    // Simulate selecting an origin from the dropdown
    composeTestRule.onNodeWithText(CoffeeOrigin.BRAZIL.name).performClick()

    // Test brewing method button selection
    composeTestRule
        .onNodeWithTag("Button:${BrewingMethod.POUR_OVER.name}")
        .performScrollTo()
        .assertIsDisplayed()
        .performClick()

    // Test taste button selection
    composeTestRule
        .onNodeWithTag("Button:${CoffeeTaste.BITTER.name}")
        .performScrollTo()
        .assertIsDisplayed()
        .performClick()

    // Mock behavior for image upload
    val imageUri = Mockito.mock(Uri::class.java)
    val imageUrl = "mocked-image-url"
    Mockito.doAnswer {
          val callback = it.getArgument<(String) -> Unit>(0)
          callback(imageUrl)
        }
        .`when`(listJourneysViewModel)
        .uploadPicture(Mockito.any(Uri::class.java), Mockito.any())

    // Simulate image selection (mocked)
    composeTestRule.runOnUiThread {
      // Simulate image URI being set after image selection
      imageUri.let { uri ->
        // Use Mockito to simulate interaction after image selection
      }
    }

    // Scroll to the stars (Rate section) before interacting with them
    composeTestRule.onNodeWithTag("journeySave").performScrollTo()

    // Click on the 4th star and check if the state is updated to Filled
    composeTestRule.onNodeWithTag("OutlinedStar4").performClick()

    // Enter a date into the date field
    composeTestRule.onNodeWithTag("inputDate").assertIsDisplayed().performTextInput("12/12/2024")

    // Simulate clicking the Save button
    composeTestRule.onNodeWithTag("journeySave").assertHasClickAction().performClick()

    // Navigate to the Journey Record screen
    composeTestRule.onNodeWithTag("journeyListItem").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("JourneyRecordScreen").assertIsDisplayed()

    // Navigate to Edit Journey Screen
    composeTestRule.onNodeWithTag("editButton").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("EditJourneyScreen").assertIsDisplayed()
  }
}
