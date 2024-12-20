package com.android.brewr.ui

import android.Manifest
import androidx.compose.ui.geometry.Offset
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
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipe
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.android.brewr.model.coffee.CoffeeShop
import com.android.brewr.model.coffee.CoffeesViewModel
import com.android.brewr.model.coffee.FavoriteCoffeeShopsViewModel
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
import com.android.brewr.ui.explore.CoffeeInformationScreen
import com.android.brewr.ui.explore.ExploreScreen
import com.android.brewr.ui.navigation.NavigationActions
import com.android.brewr.ui.navigation.Route
import com.android.brewr.ui.navigation.Screen
import com.android.brewr.ui.navigation.Screen.EXPLORE
import com.android.brewr.ui.overview.AddJourneyScreen
import com.android.brewr.ui.overview.EditJourneyScreen
import com.android.brewr.ui.overview.JourneyRecordScreen
import com.android.brewr.ui.overview.OverviewScreen
import com.android.brewr.ui.userProfile.UserMainProfileScreen
import com.android.brewr.ui.userProfile.UserPrivateListScreen
import com.google.firebase.Timestamp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.spy
import org.mockito.Mockito.`when`
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class E2ETest {
  @get:Rule val composeTestRule = createComposeRule()
  @get:Rule
  val fineLocationPermissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)
  @get:Rule
  val coarseLocationPermissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(Manifest.permission.ACCESS_COARSE_LOCATION)
  private val favoriteCoffeeShopsViewModel: FavoriteCoffeeShopsViewModel = mock()

  private lateinit var journeyRepositoryMock: JourneysRepository
  private lateinit var listJourneysViewModel: ListJourneysViewModel
  private lateinit var userRepositoryMock: UserRepository
  private lateinit var userViewModel: UserViewModel
  private lateinit var navigationActions: NavigationActions
  private lateinit var navController: NavHostController
  private lateinit var coffeesViewModel: CoffeesViewModel
  private lateinit var privateCoffeesViewModel: CoffeesViewModel

  private val journey =
      Journey(
          uid = "journey1",
          imageUrl =
              "https://firebasestorage.googleapis.com/v0/b/brewr-epfl.appspot.com/o/images%2Fff3cdd66-87c7-40a9-af5e-52f98d8374dc?alt=media&token=6257d10d-e770-44c7-b038-ea8c8a3eedb2",
          description = "A wonderful coffee journey.",
          coffeeShop =
              CoffeeShop(
                  "2",
                  "Coffee page",
                  Location(
                      46.5183076,
                      6.6338096,
                      "Coffee page, Rue du Midi, Lausanne, District de Lausanne, Vaud, 1003, Schweiz/Suisse/Svizzera/Svizra"),
                  4.5,
                  listOf(Hours("Monday", "10", "20"), Hours("Tuesday", "10", "20")),
                  listOf(Review("Lei", "good", 5.0)),
                  listOf("test.jpg")),
          coffeeOrigin = CoffeeOrigin.BRAZIL,
          brewingMethod = BrewingMethod.POUR_OVER,
          coffeeTaste = CoffeeTaste.NUTTY,
          coffeeRate = CoffeeRate.ONE,
          date = Timestamp.now())
  private val sampleCoffeeShops =
      listOf(
          CoffeeShop(
              "1",
              "Coffee1",
              Location(latitude = 46.5228, longitude = 6.6285, name = "Lausanne 1"),
              4.5,
              listOf(
                  Hours("Monday", open = "8:00 AM", close = "5:00 PM"),
                  Hours("Tuesday", open = "8:00 AM", close = "5:00 PM"),
                  Hours("Wednesday", open = "8:00 AM", close = "5:00 PM"),
                  Hours("Thursday", open = "8:00 AM", close = "5:00 PM"),
                  Hours("Friday", open = "8:00 AM", close = "5:00 PM"),
                  Hours("Saturday", open = "8:00 AM", close = "5:00 PM"),
                  Hours("Sunday", open = "8:00 AM", close = "5:00 PM")),
              listOf(Review("Lei", "good", 5.0)),
              listOf("test.jpg")),
          CoffeeShop(
              "2",
              "Coffee2",
              Location(latitude = 47.5228, longitude = 6.8385, name = "Lausanne 2"),
              5.0,
              listOf(Hours("Monday", "10", "20"), Hours("Tuesday", "10", "20")),
              listOf(Review("Jaeyi", "perfect", 5.0)),
              listOf(
                  "https://th.bing.com/th/id/OIP.gNiGdodNdn2Bck61_x18dAHaFi?rs=1&pid=ImgDetMain")))
  private val favoriteCoffeesFlow = MutableStateFlow(sampleCoffeeShops)

  @Before
  fun setUp() {
    // Initialize mocks and spies
    journeyRepositoryMock = mock(JourneysRepository::class.java)
    listJourneysViewModel = spy(ListJourneysViewModel(journeyRepositoryMock))
    userRepositoryMock = mock(UserRepository::class.java)
    userViewModel = spy(UserViewModel(userRepositoryMock))
    coffeesViewModel = spy(CoffeesViewModel::class.java)
    coffeesViewModel.addCoffees(sampleCoffeeShops)
    privateCoffeesViewModel = spy(CoffeesViewModel::class.java)

    // Mock the behavior of `getJourneys` to simulate fetching journeys
    `when`(journeyRepositoryMock.getJourneys(org.mockito.kotlin.any(), org.mockito.kotlin.any()))
        .thenAnswer {
          val onSuccess = it.getArgument<(List<Journey>) -> Unit>(0) // onSuccess callback
          onSuccess(listOf(journey)) // Simulate return list of journeys
        }
    whenever(favoriteCoffeeShopsViewModel.favoriteCoffees).thenReturn(favoriteCoffeesFlow)
    composeTestRule.setContent {
      navController = rememberNavController()
      navigationActions = NavigationActions(navController)

      NavHost(navController, Route.OVERVIEW) {
        navigation(
            startDestination = Screen.OVERVIEW,
            route = Route.OVERVIEW,
        ) {
          composable(Screen.OVERVIEW) {
            OverviewScreen(listJourneysViewModel, coffeesViewModel, navigationActions)
          }
          composable(Screen.USERPROFILE) { UserMainProfileScreen(userViewModel, navigationActions) }
          composable(Screen.JOURNEY_RECORD) {
            JourneyRecordScreen(listJourneysViewModel, navigationActions)
          }
          composable(EXPLORE) {
            ExploreScreen(
                coffeesViewModel,
                listJourneysViewModel,
                sampleCoffeeShops.sortedByDescending { it.rating })
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
        navigation(
            startDestination = Screen.USERPROFILE,
            route = Route.USER_PROFILE,
        ) {
          composable(Screen.USER_PRIVATE_LIST) {
            UserPrivateListScreen(
                navigationActions, privateCoffeesViewModel, favoriteCoffeeShopsViewModel)
          }
          composable(Screen.USER_PRIVATE_LIST_INFOS) {
            CoffeeInformationScreen(
                privateCoffeesViewModel, onBack = { navigationActions.goBack() })
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
  }

  @Test
  fun endToEndExploreFlowTest() {
    // go the the explore screen
    composeTestRule.onNodeWithTag("Explore").assertIsDisplayed().performClick()
    composeTestRule.runOnIdle { navigationActions.navigateTo(EXPLORE) }
    composeTestRule.onNodeWithTag("mapScreen").assertIsDisplayed()

    // go to menu screen
    composeTestRule.onNodeWithTag("menuButton").assertIsDisplayed().performClick()

    // Verify the bottom sheet is displayed
    composeTestRule.onNodeWithTag("exploreBottomSheet").assertIsDisplayed()

    // Verify the nearby list title is displayed
    composeTestRule
        .onNodeWithTag("listTitle")
        .assertIsDisplayed()
        .assertTextEquals("Nearby Coffee Shops")

    // Verify the dropdown menu functionality
    composeTestRule.onNodeWithTag("toggleDropdownMenu").assertIsDisplayed().performClick()
    composeTestRule.onNodeWithTag("dropdownMenu").assertIsDisplayed()

    // check the bottomSheet and coffee shop information existence
    composeTestRule.onNodeWithTag("bottomSheet").assertIsDisplayed().performTouchInput {
      swipe(center, Offset(center.x, center.y - 800)) // scroll down
    }
  }

  @Test
  fun endToEndExploreScreenDropdownMenuFlowTest() {
    // Go to the Explore screen
    composeTestRule.onNodeWithTag("Explore").assertIsDisplayed().performClick()
    composeTestRule.runOnIdle { navigationActions.navigateTo(EXPLORE) }
    composeTestRule.onNodeWithTag("mapScreen").assertIsDisplayed()

    // Clear the coffee list to simulate an empty state
    composeTestRule.runOnIdle { coffeesViewModel.clearCoffees() }
    // Open the bottom sheet
    composeTestRule.onNodeWithTag("menuButton").assertIsDisplayed().performClick()
    composeTestRule.onNodeWithTag("exploreBottomSheet").assertIsDisplayed()

    // Verify the dropdown menu opens
    composeTestRule.onNodeWithTag("toggleDropdownMenu").assertIsDisplayed().performClick()
    composeTestRule.onNodeWithTag("dropdownMenu").assertIsDisplayed()

    // Select "Curated" from the dropdown menu
    composeTestRule.onNodeWithText("Curated").assertExists().performClick()
    composeTestRule.onNodeWithTag("listTitle").assertTextEquals("Curated Coffee Shops")

    // Select "Opened" from the dropdown menu
    composeTestRule.onNodeWithTag("toggleDropdownMenu").assertIsDisplayed().performClick()
    composeTestRule.onNodeWithText("Opened").assertExists().performClick()
    composeTestRule.onNodeWithTag("listTitle").assertTextEquals("Opened Coffee Shops")

    // Assert the "closed" message is displayed if the list is empty
    composeTestRule.onNodeWithTag("noOpenCoffeeShopsMessage").assertExists()
  }

  @Test
  fun userProfileScreenE2EFlowTest() {
    // Step 1: Start from the Overview screen and navigate to the User Profile screen
    composeTestRule.onNodeWithTag("accountButton").assertIsDisplayed().performClick()

    // Verify User Profile screen is displayed
    composeTestRule.onNodeWithTag("UserProfileScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Username").assertIsDisplayed()
    composeTestRule.onNodeWithTag("User Email").assertIsDisplayed()

    // Step 2: Click on the "Favorite" button to navigate to the User Private List
    composeTestRule.onNodeWithTag("Favorite button").assertIsDisplayed().performClick()

    // Verify User Private List screen is displayed
    composeTestRule.onNodeWithTag("UserPrivateListScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("topBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("privateList").assertIsDisplayed()

    // Check all elements in User Private List are displayed
    composeTestRule.onNodeWithTag("coffeeImage:1").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("coffeeShopName:1").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("coffeeShopAddress:1").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("coffeeShopHours:1").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("coffeeShopRating:1").performScrollTo().assertIsDisplayed()

    // Step 3: Click on the clickable image to navigate to the detailed info screen
    composeTestRule.onNodeWithTag("coffeeImage:1").assertIsDisplayed().performClick()

    // Verify the Coffee Information screen is displayed
    composeTestRule.onNodeWithTag("coffeeInformationScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("coffeeShopName").assertIsDisplayed()
    composeTestRule.onNodeWithTag("coffeeShopAddress").assertIsDisplayed()
    composeTestRule.onNodeWithTag("coffeeShopRating").assertIsDisplayed()

    // Check Operating Hours are displayed
    composeTestRule.onNodeWithTag("coffeeShopHourMonday").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("coffeeShopHourTuesday").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("coffeeShopHourWednesday").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("coffeeShopHourThursday").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("coffeeShopHourFriday").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("coffeeShopHourSaturday").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("coffeeShopHourSunday").performScrollTo().assertIsDisplayed()
  }
}
