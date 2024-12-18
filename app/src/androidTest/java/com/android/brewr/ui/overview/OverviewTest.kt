package com.android.brewr.ui.overview

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.core.content.ContextCompat
import androidx.test.rule.GrantPermissionRule
import com.android.brewr.model.coffee.CoffeeShop
import com.android.brewr.model.coffee.CoffeesViewModel
import com.android.brewr.model.journey.BrewingMethod
import com.android.brewr.model.journey.CoffeeOrigin
import com.android.brewr.model.journey.CoffeeRate
import com.android.brewr.model.journey.CoffeeTaste
import com.android.brewr.model.journey.Journey
import com.android.brewr.model.journey.JourneysRepository
import com.android.brewr.model.journey.ListJourneysViewModel
import com.android.brewr.model.map.Location
import com.android.brewr.ui.navigation.NavigationActions
import com.android.brewr.ui.navigation.Route
import com.google.firebase.Timestamp
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any

class OverviewScreenTest {

  private lateinit var navigationActions: NavigationActions
  private lateinit var journeysRepository: JourneysRepository
  private lateinit var listJourneysViewModel: ListJourneysViewModel
  private lateinit var coffeesViewModel: CoffeesViewModel
  @Mock lateinit var mockContext: Context
  private val journey =
      Journey(
          uid = "journey1",
          imageUrl =
              "https://firebasestorage.googleapis.com/v0/b/brewr-epfl.appspot.com/o/images%2Fff3cdd66-87c7-40a9-af5e-52f98d8374dc?alt=media&token=6257d10d-e770-44c7-b038-ea8c8a3eedb2",
          description = "A wonderful coffee journey.",
          coffeeShop =
              CoffeeShop(
                  "1",
                  "Coffee page",
                  Location(
                      latitude = 46.5183076,
                      longitude = 6.6338096,
                      name =
                          "Coffee page, Rue du Midi, Lausanne, District de Lausanne, Vaud, 1003, Schweiz/Suisse/Svizzera/Svizra"),
                  4.5,
                  listOf(),
                  listOf(),
                  listOf()),
          coffeeOrigin = CoffeeOrigin.BRAZIL,
          brewingMethod = BrewingMethod.POUR_OVER,
          coffeeTaste = CoffeeTaste.NUTTY,
          coffeeRate = CoffeeRate.ONE,
          date = Timestamp.now())

  // Setup Compose Test Rule
  @get:Rule val composeTestRule = createComposeRule()
  @get:Rule
  val fineLocationPermissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)
  @get:Rule
  val coarseLocationPermissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(Manifest.permission.ACCESS_COARSE_LOCATION)

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)
    navigationActions = mock(NavigationActions::class.java)
    journeysRepository = mock(JourneysRepository::class.java)
    listJourneysViewModel = spy(ListJourneysViewModel(journeysRepository))
    coffeesViewModel = spy(CoffeesViewModel::class.java)

    // Start the OverviewScreen composable for testing
    `when`(navigationActions.currentRoute()).thenReturn(Route.OVERVIEW)
    // composeTestRule.setContent { OverviewScreen(listJourneysViewModel,navigationActions) }

  }

  @Test
  fun testPermissionGranted() {
    // Mock ContextCompat.checkSelfPermission to return PERMISSION_GRANTED
    `when`(ContextCompat.checkSelfPermission(mockContext, Manifest.permission.ACCESS_FINE_LOCATION))
        .thenReturn(PackageManager.PERMISSION_GRANTED)

    // Mock ContextCompat.checkSelfPermission to return PERMISSION_GRANTED
    `when`(
            ContextCompat.checkSelfPermission(
                mockContext, Manifest.permission.ACCESS_COARSE_LOCATION))
        .thenReturn(PackageManager.PERMISSION_GRANTED)

    // Set up and run the Compose UI test
    composeTestRule.setContent {
      var permissionGranted by remember { mutableStateOf(false) }

      // Launch permission check as in your real code
      LaunchedEffect(Unit) {
        permissionGranted =
            ContextCompat.checkSelfPermission(
                mockContext, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    mockContext, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED
      }

      // Display content based on permissionGranted state
      if (permissionGranted) {
        Text("Permission Granted")
      } else {
        Text("Permission Denied")
      }
    }

    // Verify that "Permission Granted" is displayed since permissions are mocked as granted
    composeTestRule.onNodeWithText("Permission Granted").assertIsDisplayed()
  }

  @Test
  fun overviewScreen_displaysTitleAndButtons() {
    composeTestRule.setContent {
      OverviewScreen(listJourneysViewModel, coffeesViewModel, navigationActions)
    }
    // Assert that the app title is displayed
    composeTestRule.onNodeWithTag("appTitle").assertIsDisplayed()
    composeTestRule.onNodeWithText("BrewR").assertExists().assertIsDisplayed()
    // Assert that the 'Add' and 'Account' buttons exist
    composeTestRule.onNodeWithTag("addButton").assertIsDisplayed().assertHasClickAction()
    composeTestRule.onNodeWithTag("accountButton").assertIsDisplayed().assertHasClickAction()
  }

  @Test
  fun exploreScreen_displayCorrectly() {
    composeTestRule.setContent {
      OverviewScreen(listJourneysViewModel, coffeesViewModel, navigationActions)
    }

    composeTestRule.onNodeWithTag("Explore").performClick()
    composeTestRule.onNodeWithTag("menuButton").assertIsDisplayed().performClick()
  }

  @Test
  fun overviewScreen_clickSubNavigationButtons() {
    composeTestRule.setContent {
      OverviewScreen(listJourneysViewModel, coffeesViewModel, navigationActions)
    }

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
    composeTestRule.setContent {
      OverviewScreen(listJourneysViewModel, coffeesViewModel, navigationActions)
    }
    composeTestRule.onNodeWithTag("addButton").performClick()
    // check navigation
  }

  @Test
  fun overviewScreen_clickAccountButton() {
    composeTestRule.setContent {
      OverviewScreen(listJourneysViewModel, coffeesViewModel, navigationActions)
    }
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
    composeTestRule.setContent {
      OverviewScreen(listJourneysViewModel, coffeesViewModel, navigationActions)
    }
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
    composeTestRule.setContent {
      OverviewScreen(listJourneysViewModel, coffeesViewModel, navigationActions)
    }
    listJourneysViewModel.getJourneys()
    // Then
    composeTestRule
        .onNodeWithTag("emptyJourneyPrompt")
        .assertIsDisplayed()
        .assertTextEquals("You have no Journey yet.")
  }
}
