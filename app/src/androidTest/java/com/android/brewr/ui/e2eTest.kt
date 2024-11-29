package com.android.brewr.ui

import android.Manifest
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onFirst
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
import androidx.test.rule.GrantPermissionRule
import com.android.brewr.model.coffee.Coffee
import com.android.brewr.model.coffee.Hours
import com.android.brewr.model.coffee.Review
import com.android.brewr.model.journey.BrewingMethod
import com.android.brewr.model.journey.CoffeeOrigin
import com.android.brewr.model.journey.CoffeeRate
import com.android.brewr.model.journey.CoffeeTaste
import com.android.brewr.model.journey.Journey
import com.android.brewr.model.journey.JourneysRepository
import com.android.brewr.model.journey.ListJourneysViewModel
import com.android.brewr.model.map.Location
import com.android.brewr.model.user.UserRepository
import com.android.brewr.model.user.UserViewModel
import com.android.brewr.ui.navigation.NavigationActions
import com.android.brewr.ui.navigation.Route
import com.android.brewr.ui.navigation.Screen
import com.android.brewr.ui.navigation.Screen.EXPLORE
import com.android.brewr.ui.overview.AddJourneyScreen
import com.android.brewr.ui.overview.EditJourneyScreen
import com.android.brewr.ui.overview.ExploreScreen
import com.android.brewr.ui.overview.JourneyRecordScreen
import com.android.brewr.ui.overview.OverviewScreen
import com.android.brewr.ui.userProfile.UserMainProfileScreen
import com.google.firebase.Timestamp
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
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
  @get:Rule
  val fineLocationPermissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)
  @get:Rule
  val coarseLocationPermissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(Manifest.permission.ACCESS_COARSE_LOCATION)

  private lateinit var journeyRepositoryMock: JourneysRepository
  private lateinit var listJourneysViewModel: ListJourneysViewModel
  private lateinit var userRepositoryMock: UserRepository
  private lateinit var userViewModel: UserViewModel
  private lateinit var navigationActions: NavigationActions
  private lateinit var navController: NavHostController

  private val journey =
      Journey(
          uid = "journey1",
          imageUrl =
              "https://firebasestorage.googleapis.com/v0/b/brewr-epfl.appspot.com/o/images%2Fff3cdd66-87c7-40a9-af5e-52f98d8374dc?alt=media&token=6257d10d-e770-44c7-b038-ea8c8a3eedb2",
          description = "A wonderful coffee journey.",
          location =
              Location(
                  46.5183076,
                  6.6338096,
                  "Coffee page, Rue du Midi, Lausanne, District de Lausanne, Vaud, 1003, Schweiz/Suisse/Svizzera/Svizra"),
          coffeeOrigin = CoffeeOrigin.BRAZIL,
          brewingMethod = BrewingMethod.POUR_OVER,
          coffeeTaste = CoffeeTaste.NUTTY,
          coffeeRate = CoffeeRate.ONE,
          date = Timestamp.now())
  val sampleCoffees =
      listOf(
          Coffee(
              "1",
              "Coffee1",
              com.android.brewr.model.location.Location(
                  latitude = 46.5228, longitude = 6.6285, address = "Lausanne 1"),
              4.5,
              listOf(Hours("10", "20"), Hours("10", "20")),
              listOf(Review("Lei", "good", 5.0)),
              listOf("test.jpg")))

  @Before
  fun setUp() {
    // Initialize mocks and spies
    journeyRepositoryMock = mock(JourneysRepository::class.java)
    listJourneysViewModel = ListJourneysViewModel(journeyRepositoryMock)
    //      listJourneysViewModel = spy(ListJourneysViewModel(journeyRepositoryMock))
    userRepositoryMock = mock(UserRepository::class.java)
    userViewModel = spy(UserViewModel(userRepositoryMock))
    // Mock the behavior of `getJourneys` to simulate fetching journeys
    `when`(journeyRepositoryMock.getJourneys(org.mockito.kotlin.any(), org.mockito.kotlin.any()))
        .thenAnswer {
          val onSuccess = it.getArgument<(List<Journey>) -> Unit>(0) // onSuccess callback
          onSuccess(listOf(journey)) // Simulate return list of journeys
        }

    composeTestRule.setContent {
      navController = rememberNavController()
      navigationActions = NavigationActions(navController)

      NavHost(navController, Route.OVERVIEW) {
        navigation(
            startDestination = Screen.OVERVIEW,
            route = Route.OVERVIEW,
        ) {
          composable(Screen.OVERVIEW) { OverviewScreen(listJourneysViewModel, navigationActions) }
          composable(Screen.USERPROFILE) { UserMainProfileScreen(userViewModel, navigationActions) }
          composable(Screen.JOURNEY_RECORD) {
            JourneyRecordScreen(listJourneysViewModel, navigationActions)
          }
          composable(EXPLORE) { ExploreScreen(sampleCoffees) }
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
  }

  @Test
  fun endToEndGalleryFlowTest() {
    composeTestRule.onNodeWithTag("Gallery").assertIsDisplayed().performClick()
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

    composeTestRule.onNodeWithTag("inputCoffeeshopLocation").assertHasClickAction().performClick()
    composeTestRule.onNodeWithTag("inputCoffeeshopLocation").assertExists()

    composeTestRule.onNodeWithTag("inputCoffeeshopLocation").performClick().performTextInput("Star")

    runBlocking {
      repeat(50) { // 50 * 100ms = 5000ms = 5 seconds
        if (composeTestRule
            .onAllNodes(hasTestTag("locationSuggestionsDropdown"))
            .fetchSemanticsNodes()
            .isNotEmpty()) {
          return@runBlocking // Exit loop if the dropdown becomes visible
        }
        delay(1000)
      }
    }
    composeTestRule.onNodeWithTag("locationSuggestionsDropdown").assertIsDisplayed()

    // Simulate selecting the first location suggestion (if available)
    composeTestRule.onAllNodesWithTag("locationSuggestionsDropdown").onFirst().performClick()

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

    // Step 5: Edit the journey
    composeTestRule.onNodeWithTag("journeyListItem").performClick()
    composeTestRule.onNodeWithTag("journeyRecordScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("editButton").performClick()
    composeTestRule.onNodeWithTag("editJourneyScreen").assertIsDisplayed()
    composeTestRule
    composeTestRule
        .onNodeWithTag("inputJourneyDescription")
        .assertIsDisplayed()
        .performTextInput("Amazing Coffee Experience Edited")
    composeTestRule
        .onNodeWithTag("coffeeShopCheckText", useUnmergedTree = true)
        .assertTextEquals("At a coffee shop")
    composeTestRule.onNodeWithTag("coffeeShopCheckRow").assertHasClickAction().performClick()
    composeTestRule
        .onNodeWithTag("coffeeShopCheckText", useUnmergedTree = true)
        .assertTextEquals("At home")
    composeTestRule.onNodeWithTag("journeySave").assertHasClickAction().performClick()
    composeTestRule.runOnIdle { navController.popBackStack() }
  }

  @Test
  fun endToEndExploreFlowTest() {
    // go the the explore screen
    composeTestRule.onNodeWithTag("Explore").assertIsDisplayed().performClick()
    composeTestRule.runOnIdle { navigationActions.navigateTo(EXPLORE) }
    composeTestRule.onNodeWithTag("mapScreen").assertIsDisplayed()

    // go to menu screen
    composeTestRule.onNodeWithTag("menuButton").assertIsDisplayed().performClick()

    // check the bottomSheet and coffee shop information existence
    composeTestRule.onNodeWithTag("bottomSheet").assertIsDisplayed()
    composeTestRule.onNodeWithTag("coffeeImage").assertIsDisplayed()
  }
}
