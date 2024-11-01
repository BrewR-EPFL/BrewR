package com.android.brewr.ui

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.brewr.model.journey.BrewingMethod
import com.android.brewr.model.journey.CoffeeOrigin
import com.android.brewr.model.journey.CoffeeRate
import com.android.brewr.model.journey.CoffeeTaste
import com.android.brewr.model.journey.Journey
import com.android.brewr.model.journey.JourneysRepository
import com.android.brewr.model.journey.ListJourneysViewModel
import com.android.brewr.ui.navigation.NavigationActions
import com.android.brewr.ui.navigation.Route
import com.android.brewr.ui.navigation.Screen
import com.android.brewr.ui.overview.AddJourneyScreen
import com.android.brewr.ui.overview.EditJourneyScreen
import com.android.brewr.ui.overview.JourneyRecordScreen
import com.android.brewr.ui.overview.OverviewScreen
import com.android.brewr.ui.userProfile.UserMainProfileScreen
import com.google.firebase.Timestamp
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.spy
import org.mockito.Mockito.`when`

@RunWith(AndroidJUnit4::class)
class E2ETest {
  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var repositoryMock: JourneysRepository
  private lateinit var listJourneysViewModel: ListJourneysViewModel
  private lateinit var navigationActions: NavigationActions
  private lateinit var navController: NavHostController

  private val journey =
      Journey(
          uid = "journey1",
          imageUrl =
              "https://firebasestorage.googleapis.com/v0/b/brewr-epfl.appspot.com/o/images%2Fff3cdd66-87c7-40a9-af5e-52f98d8374dc?alt=media&token=6257d10d-e770-44c7-b038-ea8c8a3eedb2",
          description = "A wonderful coffee journey.",
          coffeeShopName = "Starbucks",
          coffeeOrigin = CoffeeOrigin.BRAZIL,
          brewingMethod = BrewingMethod.POUR_OVER,
          coffeeTaste = CoffeeTaste.NUTTY,
          coffeeRate = CoffeeRate.ONE,
          date = Timestamp.now())

  @Before
  fun setUp() {
    // Initialize mocks and spies
    repositoryMock = mock(JourneysRepository::class.java)
    listJourneysViewModel = spy(ListJourneysViewModel(repositoryMock))
    // Mock the behavior of `getJourneys` to simulate fetching journeys
    `when`(repositoryMock.getJourneys(org.mockito.kotlin.any(), org.mockito.kotlin.any()))
        .thenAnswer {
          val onSuccess = it.getArgument<(List<Journey>) -> Unit>(0) // onSuccess callback
          onSuccess(listOf(journey)) // Simulate return list of journeys
        }
  }

  @Test
  fun endToEndFlowTest() {
    // Set up the composable content for the test
    composeTestRule.setContent {
      navController = rememberNavController()
      navigationActions = NavigationActions(navController)

      NavHost(navController, Route.OVERVIEW) {
        navigation(
            startDestination = Screen.OVERVIEW,
            route = Route.OVERVIEW,
        ) {
          composable(Screen.OVERVIEW) { OverviewScreen(listJourneysViewModel, navigationActions) }
          composable(Screen.USERPROFILE) { UserMainProfileScreen(navigationActions) }
          composable(Screen.JOURNEY_RECORD) {
            JourneyRecordScreen(listJourneysViewModel, navigationActions)
          }
        }
        navigation(
            startDestination = Screen.ADD_JOURNEY,
            route = Route.ADD_JOURNEY,
        ) {
          composable(Screen.ADD_JOURNEY) {
            AddJourneyScreen(listJourneysViewModel, navigationActions)
          }
          composable(Screen.EDIT_JOURNEY) {
            EditJourneyScreen(listJourneysViewModel, navigationActions)
          }
        }
      }
    }
    // Fetch journeys to ensure initial data is available
    listJourneysViewModel.getJourneys()

    // Step1 : Click on a Journey to see the detail
    composeTestRule.onNodeWithTag("journeyListItem").performClick()
    composeTestRule.onNodeWithTag("journeyRecordScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("backButton").performClick()
    composeTestRule.onNodeWithTag("overviewScreen").assertIsDisplayed()

    // Step2 : Navigate to Add Journey screen
    composeTestRule.onNodeWithTag("addButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("addButton").performClick()

    // Step3 : Add a Journey
    composeTestRule.onNodeWithTag("addJourneyScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("addImageBox").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("inputJourneyDescription")
        .assertIsDisplayed()
        .performTextInput("Amazing Coffee Experience")
    composeTestRule.onNodeWithTag("coffeeShopCheckRow").assertHasClickAction().performClick()
    composeTestRule
        .onNodeWithTag("coffeeShopNameField")
        .assertExists()
        .performTextInput("Starbucks")
    composeTestRule.onNodeWithTag("inputCoffeeOrigin").assertIsDisplayed().performClick()
    composeTestRule.onNodeWithTag("dropdownMenuCoffeeOrigin").assertExists()
    composeTestRule.onNodeWithText(CoffeeOrigin.BRAZIL.name).performClick()
    composeTestRule
        .onNodeWithTag("Button:${BrewingMethod.POUR_OVER.name}")
        .performScrollTo()
        .assertIsDisplayed()
        .performClick()
    composeTestRule
        .onNodeWithTag("Button:${CoffeeTaste.BITTER.name}")
        .performScrollTo()
        .assertIsDisplayed()
        .performClick()
    composeTestRule.onNodeWithTag("journeySave").performScrollTo()
    composeTestRule.onNodeWithTag("OutlinedStar4").performClick()
    composeTestRule.onNodeWithTag("journeySave").assertHasClickAction().performClick()
    composeTestRule.runOnIdle { navController.popBackStack() }

    // Step 4: Back to Overview
    composeTestRule.onNodeWithTag("overviewScreen").assertIsDisplayed()
  }
}
