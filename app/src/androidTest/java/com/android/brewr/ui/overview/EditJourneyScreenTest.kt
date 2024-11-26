package com.android.brewr.ui.overview

import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.brewr.model.journey.BrewingMethod
import com.android.brewr.model.journey.CoffeeOrigin
import com.android.brewr.model.journey.CoffeeRate
import com.android.brewr.model.journey.CoffeeTaste
import com.android.brewr.model.journey.Journey
import com.android.brewr.model.journey.JourneysRepository
import com.android.brewr.model.journey.ListJourneysViewModel
import com.android.brewr.model.map.Location
import com.android.brewr.ui.navigation.NavigationActions
import com.android.brewr.ui.navigation.Screen
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
import org.mockito.kotlin.any
import org.mockito.kotlin.verify

@RunWith(AndroidJUnit4::class)
class EditJourneyScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var repositoryMock: JourneysRepository
  private lateinit var listJourneysViewModel: ListJourneysViewModel
  private lateinit var navigationActions: NavigationActions

  private val journey1 =
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
  private val journey2 =
      Journey(
          uid = "journey2",
          imageUrl =
              "https://firebasestorage.googleapis.com/v0/b/brewr-epfl.appspot.com/o/images%2Fff3cdd66-87c7-40a9-af5e-52f98d8374dc?alt=media&token=6257d10d-e770-44c7-b038-ea8c8a3eedb2",
          description = "A wonderful coffee journey.",
          location = Location(),
          coffeeOrigin = CoffeeOrigin.BRAZIL,
          brewingMethod = BrewingMethod.POUR_OVER,
          coffeeTaste = CoffeeTaste.NUTTY,
          coffeeRate = CoffeeRate.ONE,
          date = Timestamp.now())

  @Before
  fun setUp() {
    repositoryMock = mock(JourneysRepository::class.java)
    listJourneysViewModel = spy(ListJourneysViewModel(repositoryMock))
    navigationActions = mock(NavigationActions::class.java)

    `when`(navigationActions.currentRoute()).thenReturn(Screen.EDIT_JOURNEY)
  }

  @Test
  fun testEditJourneyScreenDisplaysAndInteractsCorrectly() {
    listJourneysViewModel.selectJourney(journey1)
    composeTestRule.setContent {
      EditJourneyScreen(
          listJourneysViewModel = listJourneysViewModel, navigationActions = navigationActions)
    }
    // Check if the Scaffold with the tag "editJourneyScreen" is displayed
    composeTestRule
        .onNodeWithTag("editJourneyScreen")
        .assertExists() // Ensures that the Scaffold exists in the composition
        .assertIsDisplayed() // Ensures that the Scaffold is visible on the screen

    // Check if the back button is displayed
    composeTestRule.onNodeWithTag("backButton").assertIsDisplayed()

    // Check if the title "Your Journey" is displayed
    composeTestRule.onNodeWithTag("YourJourneyTitle").assertIsDisplayed()

    // Test Edit Photo functionality (simulate photo click)
    composeTestRule.onNodeWithTag("editImageBox").assertIsDisplayed().assertHasClickAction()

    // Check if the description text field displays the correct description
    composeTestRule
        .onNodeWithTag("inputJourneyDescription")
        .assertIsDisplayed()
        .assert(hasText("A wonderful coffee journey."))

    // Clear the existing text and enter a new description
    composeTestRule
        .onNodeWithTag("inputJourneyDescription")
        .assertIsDisplayed()
        .performTextInput("Updated Coffee Experience")

    // Test if the text changes to "At a coffee shop"
    composeTestRule
        .onNodeWithTag("coffeeShopCheckText", useUnmergedTree = true)
        .assertTextEquals("At a coffee shop")
    // Check if the Coffee Shop Name field not displays
    composeTestRule.onNodeWithTag("inputCoffeeshopLocation").assertIsNotDisplayed()
    // Test the coffee shop checkbox interaction
    composeTestRule.onNodeWithTag("coffeeShopCheckRow").assertHasClickAction().performClick()
    // Test if the text changes to "At home"
    composeTestRule
        .onNodeWithTag("coffeeShopCheckText", useUnmergedTree = true)
        .assertTextEquals("At home")
    // Check if the Coffee Shop Name field not displays
    composeTestRule.onNodeWithTag("inputCoffeeshopLocation").assertIsNotDisplayed()
    // Test the coffee shop checkbox interaction
    composeTestRule.onNodeWithTag("coffeeShopCheckRow").assertHasClickAction().performClick()
    // Check if the Coffee Shop Name input field displays
    composeTestRule.onNodeWithTag("inputCoffeeshopLocation").assertIsDisplayed()

    // Update the coffee shop location
    composeTestRule
        .onNodeWithTag("inputCoffeeshopLocation")
        .performClick()
        .performTextInput("Starbucks Lausanne")

    runBlocking {
      repeat(50) { // 50 * 100ms = 5000ms = 5 seconds
        if (composeTestRule
            .onAllNodes(hasTestTag("locationSuggestionsDropdown"))
            .fetchSemanticsNodes()
            .isNotEmpty()) {
          return@runBlocking // Exit loop if the dropdown becomes visible
        }
        delay(100)
      }
    }
    composeTestRule.onNodeWithTag("locationSuggestionsDropdown").assertIsDisplayed()

    // Simulate selecting the first location suggestion (if available)
    composeTestRule.onAllNodesWithTag("locationSuggestionsDropdown").onFirst().performClick()
    // Test Coffee Origin before change match the journey's origin
    composeTestRule
        .onNodeWithTag("inputCoffeeOrigin")
        .assertIsDisplayed()
        .assert(hasText(CoffeeOrigin.BRAZIL.name))

    // Test Coffee Origin dropdown (click and select an option)
    composeTestRule.onNodeWithTag("inputCoffeeOrigin").assertIsDisplayed().performClick()
    composeTestRule.onNodeWithTag("dropdownMenuCoffeeOrigin").assertExists()

    // Simulate selecting an origin from the dropdown
    composeTestRule.onNodeWithText(CoffeeOrigin.COLOMBIA.name).performClick()

    // Test brewing method button selection
    composeTestRule
        .onNodeWithTag("Button:${BrewingMethod.POUR_OVER.name}")
        .performScrollTo()
        .assertIsDisplayed()

    // Test brewing method button selection
    composeTestRule
        .onNodeWithTag("Button:${BrewingMethod.FRENCH_PRESS.name}")
        .performScrollTo()
        .assertIsDisplayed()
        .performClick()

    // Test taste method button selection
    composeTestRule
        .onNodeWithTag("Button:${CoffeeTaste.NUTTY.name}")
        .performScrollTo()
        .assertIsDisplayed()

    // Test taste button selection
    composeTestRule
        .onNodeWithTag("Button:${CoffeeTaste.SPICY.name}")
        .performScrollTo()
        .assertIsDisplayed()
        .performClick()

    // Scroll to the stars (Rate section) before interacting with them
    composeTestRule.onNodeWithTag("journeySave").performScrollTo()

    // Check if all 5 stars are displayed with their respective tags
    composeTestRule.onNodeWithTag("FilledStar1").assertIsDisplayed()
    composeTestRule.onNodeWithTag("OutlinedStar2").assertIsDisplayed()
    composeTestRule.onNodeWithTag("OutlinedStar3").assertIsDisplayed()
    composeTestRule.onNodeWithTag("OutlinedStar4").assertIsDisplayed()
    composeTestRule.onNodeWithTag("OutlinedStar5").assertIsDisplayed()

    // Click on the 3rd star and check if the state is updated to Filled
    composeTestRule.onNodeWithTag("OutlinedStar3").performClick()

    // After clicking the 3rd star, it should now be filled
    composeTestRule.onNodeWithTag("FilledStar3").assertIsDisplayed()

    // Ensure the 4th and 5th stars remain outlined
    composeTestRule.onNodeWithTag("OutlinedStar4").assertIsDisplayed()
    composeTestRule.onNodeWithTag("OutlinedStar5").assertIsDisplayed()

    // Open the DatePickerDialog
    composeTestRule.onNodeWithTag("dateButton").assertIsDisplayed().performClick()

    // Verify the DatePickerDialog is shown
    composeTestRule.onNodeWithTag("datePickerDialog").assertIsDisplayed()

    // Confirm the selection
    composeTestRule.onNodeWithText("Cancel").performClick()

    // Simulate clicking the Save button
    composeTestRule.onNodeWithTag("journeySave").assertHasClickAction().performClick()
  }

  @Test
  fun submitWithEverythingNewExceptPhoto() {
    listJourneysViewModel.selectJourney(journey1)
    composeTestRule.setContent {
      EditJourneyScreen(
          listJourneysViewModel = listJourneysViewModel, navigationActions = navigationActions)
    }

    // Description
    composeTestRule.onNodeWithTag("inputJourneyDescription").performTextClearance()
    composeTestRule
        .onNodeWithTag("inputJourneyDescription")
        .performTextInput("Another wonderful coffee journey.")
    // Coffee Shop Name
    composeTestRule.onNodeWithTag("coffeeShopCheckRow").performClick()
    /**
     * composeTestRule.onNodeWithTag("inputCoffeeshopLocation").performTextClearance()
     * composeTestRule .onNodeWithTag("inputCoffeeshopLocation") .performClick()
     * .performTextInput("Starbucks Lausanne")
     *
     * runBlocking { repeat(50) { // 50 * 100ms = 5000ms = 5 seconds if (composeTestRule
     * .onAllNodes(hasTestTag("locationSuggestionsDropdown")) .fetchSemanticsNodes() .isNotEmpty())
     * { return@runBlocking // Exit loop if the dropdown becomes visible } delay(100) } }
     * composeTestRule.onNodeWithTag("locationSuggestionsDropdown").assertIsDisplayed()
     *
     * // Simulate selecting the first location suggestion (if available)
     * composeTestRule.onAllNodesWithTag("locationSuggestionsDropdown").onFirst().performClick()
     */
    // Coffee Origin
    composeTestRule
        .onNodeWithTag("Button:${BrewingMethod.FRENCH_PRESS.name}")
        .performScrollTo()
        .performClick()
    // Brewing Method
    composeTestRule
        .onNodeWithTag("Button:${BrewingMethod.COLD_BREW.name}")
        .performScrollTo()
        .performClick()
    // Coffee Taste
    composeTestRule
        .onNodeWithTag("Button:${CoffeeTaste.FRUITY.name}")
        .performScrollTo()
        .performClick()
    // Coffee Rate
    composeTestRule.onNodeWithTag("OutlinedStar5").performScrollTo().performClick()

    composeTestRule.waitForIdle()

    // Click the save button
    composeTestRule.onNodeWithTag("journeySave").performScrollTo().performClick()

    composeTestRule.waitForIdle()

    verify(repositoryMock).updateJourney(any(), any(), any())
    verify(navigationActions).goBack()
  }

  @Test
  fun testEditJourneyScreenWithLocationAthomeDisplaysCorrectly() {
    listJourneysViewModel.selectJourney(journey2)
    composeTestRule.setContent {
      EditJourneyScreen(
          listJourneysViewModel = listJourneysViewModel, navigationActions = navigationActions)
    }
    // Check if the Coffees hop checkbox is displayed
    composeTestRule.onNodeWithTag("coffeeShopCheckRow").assertIsDisplayed()
    // Assert that the text "At home" is displayed
    composeTestRule
        .onNodeWithTag("coffeeShopCheckText", useUnmergedTree = true)
        .assertTextEquals("At home")
    // Check if the Coffee Shop search field is not displayed
    composeTestRule
        .onNodeWithTag("inputCoffeeshopLocation", useUnmergedTree = true)
        .assertIsNotDisplayed()

    // Test the coffee shop checkbox interaction
    composeTestRule.onNodeWithTag("coffeeShopCheckRow").assertHasClickAction().performClick()
    // Test if the text changes to "At a coffee shop"
    composeTestRule
        .onNodeWithTag("coffeeShopCheckText", useUnmergedTree = true)
        .assertTextEquals("At a coffee shop")
    // Check if the Coffee Shop Name input field displays
    composeTestRule.onNodeWithTag("inputCoffeeshopLocation").assertIsDisplayed()

    // Update the coffee shop location
    composeTestRule
        .onNodeWithTag("inputCoffeeshopLocation")
        .performClick()
        .performTextInput("Starbucks Lausanne")

    runBlocking {
      repeat(50) { // 50 * 100ms = 5000ms = 5 seconds
        if (composeTestRule
            .onAllNodes(hasTestTag("locationSuggestionsDropdown"))
            .fetchSemanticsNodes()
            .isNotEmpty()) {
          return@runBlocking // Exit loop if the dropdown becomes visible
        }
        delay(100)
      }
    }
    composeTestRule.onNodeWithTag("locationSuggestionsDropdown").assertIsDisplayed()

    // Simulate selecting the first location suggestion (if available)
    composeTestRule.onAllNodesWithTag("locationSuggestionsDropdown").onFirst().performClick()
    // Test Coffee Origin before change match the journey's origin
    composeTestRule
        .onNodeWithTag("inputCoffeeOrigin")
        .assertIsDisplayed()
        .assert(hasText(CoffeeOrigin.BRAZIL.name))

    // Simulate clicking the Save button
    composeTestRule.onNodeWithTag("journeySave").assertHasClickAction().performClick()
  }

  @Test
  fun navigatesBackToOverviewOnBackButtonClick() {
    listJourneysViewModel.selectJourney(journey1)
    composeTestRule.setContent {
      AddJourneyScreen(
          listJourneysViewModel = listJourneysViewModel, navigationActions = navigationActions)
    }

    composeTestRule.onNodeWithTag("backButton").performClick()
    verify(navigationActions).goBack()
  }
}

private fun Unit.performTextInput(s: String) {}
