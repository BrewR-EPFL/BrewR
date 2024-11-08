package com.android.brewr.ui.overview

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.android.brewr.model.journey.BrewingMethod
import com.android.brewr.model.journey.CoffeeOrigin
import com.android.brewr.model.journey.CoffeeRate
import com.android.brewr.model.journey.CoffeeTaste
import com.android.brewr.model.journey.Journey
import com.android.brewr.model.journey.JourneysRepository
import com.android.brewr.model.journey.ListJourneysViewModel
import com.android.brewr.ui.navigation.NavigationActions
import com.android.brewr.ui.navigation.Route
import com.google.firebase.Timestamp
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.spy
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.verify

class OverviewScreenTest {

  private lateinit var navigationActions: NavigationActions
  private lateinit var journeysRepository: JourneysRepository
  private lateinit var listJourneysViewModel: ListJourneysViewModel
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

  // Setup Compose Test Rule
  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    journeysRepository = mock(JourneysRepository::class.java)
    listJourneysViewModel = spy(ListJourneysViewModel(journeysRepository))

    // Start the OverviewScreen composable for testing
    `when`(navigationActions.currentRoute()).thenReturn(Route.OVERVIEW)
    // composeTestRule.setContent { OverviewScreen(listJourneysViewModel,navigationActions) }
  }

  @Test
  fun overviewScreen_displaysTitleAndButtons() {
    composeTestRule.setContent { OverviewScreen(listJourneysViewModel, navigationActions) }
    // Assert that the app title is displayed
    composeTestRule.onNodeWithTag("appTitle").assertIsDisplayed()
    composeTestRule.onNodeWithText("BrewR").assertExists().assertIsDisplayed()
    // Assert that the 'Add' and 'Account' buttons exist
    composeTestRule.onNodeWithTag("addButton").assertIsDisplayed().assertHasClickAction()
    composeTestRule.onNodeWithTag("accountButton").assertIsDisplayed().assertHasClickAction()
  }

  @Test
  fun exploreScreen_displayCorrectly() {
    composeTestRule.setContent { OverviewScreen(listJourneysViewModel, navigationActions) }

    composeTestRule.onNodeWithTag("Explore").performClick()
    composeTestRule.onNodeWithTag("menuButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("menuButton").performClick()
  }

  @Test
  fun overviewScreen_clickSubNavigationButtons() {
    composeTestRule.setContent { OverviewScreen(listJourneysViewModel, navigationActions) }

    // Assert that the sub-navigation buttons are displayed
    composeTestRule.onNodeWithTag("Gallery").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Explore").assertIsDisplayed()

    // Simulate a click on the "Gallery" button
    composeTestRule.onNodeWithTag("Gallery").assertHasClickAction().performClick()

    // Simulate a click on the "Explore" button
    composeTestRule.onNodeWithTag("Explore").assertHasClickAction().performClick()
  }

  @Test
  fun overviewScreen_clickAddButton() {
    composeTestRule.setContent { OverviewScreen(listJourneysViewModel, navigationActions) }
    composeTestRule.onNodeWithTag("addButton").performClick()
    // check navigation
  }

  @Test
  fun overviewScreen_clickAccountButton() {
    composeTestRule.setContent { OverviewScreen(listJourneysViewModel, navigationActions) }
    composeTestRule.onNodeWithTag("accountButton").performClick()
    // check navigation
  }

  @Test
  fun overviewScreen_displaysJourneys() {
    // Define the behavior of getJourneys in the mock repository
    `when`(journeysRepository.getJourneys(any(), any())).thenAnswer {
      val onSuccess = it.getArgument<(List<Journey>) -> Unit>(0) // onSuccess callback
      onSuccess(listOf(journey)) // Simulate return list of journeys
    }
    composeTestRule.setContent { OverviewScreen(listJourneysViewModel, navigationActions) }
    listJourneysViewModel.getJourneys()
    // Wait for UI state to settle
    composeTestRule.waitForIdle() // or mainClock.advanceUntilIdle()

    composeTestRule.onNodeWithTag("journeyListItem").assertIsDisplayed()
    // Perform a click on the first item in the list
    composeTestRule.onAllNodesWithTag("journeyListItem")[0].performClick()
    verify(listJourneysViewModel).selectJourney(journey)
  }

  @Test
  fun overviewScreen_displaysNoJourneysMessage_whenJourneysListIsEmpty() {
    // Define the behavior of getJourneys in the mock repository
    `when`(journeysRepository.getJourneys(any(), any())).thenAnswer {
      val onSuccess = it.getArgument<(List<Journey>) -> Unit>(0) // onSuccess callback
      onSuccess(emptyList()) // Simulate return list of journeys
    }
    composeTestRule.setContent { OverviewScreen(listJourneysViewModel, navigationActions) }
    listJourneysViewModel.getJourneys()
    // Then
    composeTestRule
        .onNodeWithTag("emptyJourneyPrompt")
        .assertIsDisplayed()
        .assertTextEquals("You have no Journey yet.")
  }
}
