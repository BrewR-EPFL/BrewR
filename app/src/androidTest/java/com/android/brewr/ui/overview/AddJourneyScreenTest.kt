package com.android.brewr.ui.overview

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.brewr.model.journey.BrewingMethod
import com.android.brewr.model.journey.CoffeeOrigin
import com.android.brewr.model.journey.CoffeeTaste
import com.android.brewr.model.journey.JourneysRepository
import com.android.brewr.model.journey.ListJourneysViewModel
import com.android.brewr.ui.navigation.NavigationActions
import com.android.brewr.ui.navigation.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.spy
import org.mockito.Mockito.`when`
import org.mockito.kotlin.verify

@RunWith(AndroidJUnit4::class)
class AddJourneyScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var repositoryMock: JourneysRepository
  private lateinit var listJourneysViewModel: ListJourneysViewModel
  private lateinit var navigationActions: NavigationActions

  @Before
  fun setUp() {
    repositoryMock = mock(JourneysRepository::class.java)
    listJourneysViewModel = spy(ListJourneysViewModel(repositoryMock))
    navigationActions = mock(NavigationActions::class.java)

    `when`(navigationActions.currentRoute()).thenReturn(Screen.ADD_JOURNEY)
  }

  @Test
  fun testAddJourneyScreenDisplaysAndInteractsCorrectly() {
    composeTestRule.setContent {
      AddJourneyScreen(
          listJourneysViewModel = listJourneysViewModel, navigationActions = navigationActions)
    }

    // Check if the title "Your Journey" is displayed
    composeTestRule.onNodeWithTag("YourJourneyTitle").assertIsDisplayed()

    // Test Add Photo functionality (simulate photo click)
    composeTestRule.onNodeWithTag("addImageBox").assertIsDisplayed().assertHasClickAction()

    // Check if the description text field is displayed and type text into it
    composeTestRule
        .onNodeWithTag("inputJourneyDescription")
        .assertIsDisplayed()
        .performTextInput("Amazing Coffee Experience")

    // Check if the location input field is displayed
    composeTestRule.onNodeWithTag("coffeeShopCheckRow").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("coffeeShopCheckboxIcon", useUnmergedTree = true)
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("coffeeShopCheckText", useUnmergedTree = true)
        .assertTextEquals("At a coffee shop")
    // Check if the Coffee Shop Name input field displays
    composeTestRule.onNodeWithTag("inputCoffeeshopLocation").assertIsDisplayed()

    // Test the coffee shop checkbox interaction
    composeTestRule.onNodeWithTag("coffeeShopCheckRow").assertHasClickAction().performClick()
    // Assert that the text "At home" is displayed
    composeTestRule
        .onNodeWithTag("coffeeShopCheckText", useUnmergedTree = true)
        .assertTextEquals("At home")
    // Check if the Coffee Shop Name input field not display
    composeTestRule.onNodeWithTag("inputCoffeeshopLocation").assertIsNotDisplayed()

    // Test the coffee shop checkbox interaction
    composeTestRule.onNodeWithTag("coffeeShopCheckRow").assertHasClickAction().performClick()

    // After clicking, the coffee shop location input field should appear
    composeTestRule.onNodeWithTag("inputCoffeeshopLocation").assertIsDisplayed()

    // Interact with the dropdown for coffee shop location suggestions
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

    // Scroll to the stars (Rate section) before interacting with them
    composeTestRule.onNodeWithTag("journeySave").performScrollTo()

    // Check if all 5 stars are displayed with their respective tags
    composeTestRule.onNodeWithTag("OutlinedStar1").assertIsDisplayed()
    composeTestRule.onNodeWithTag("OutlinedStar2").assertIsDisplayed()
    composeTestRule.onNodeWithTag("OutlinedStar3").assertIsDisplayed()
    composeTestRule.onNodeWithTag("OutlinedStar4").assertIsDisplayed()
    composeTestRule.onNodeWithTag("OutlinedStar5").assertIsDisplayed()

    // Click on the 4th star and check if the state is updated to Filled
    composeTestRule.onNodeWithTag("OutlinedStar4").performClick()

    // After clicking the 4th star, it should now be filled
    composeTestRule.onNodeWithTag("FilledStar4").assertIsDisplayed()

    // Ensure the 5th star remains outlined
    composeTestRule.onNodeWithTag("OutlinedStar5").assertIsDisplayed()

    // Click on the 5th star to select it
    composeTestRule.onNodeWithTag("OutlinedStar5").performClick()

    // After clicking, all 5 stars should now be filled
    composeTestRule.onNodeWithTag("FilledStar5").assertIsDisplayed()

    // Assert that the filled star tags have been updated for all stars
    composeTestRule.onNodeWithTag("FilledStar1").assertIsDisplayed()
    composeTestRule.onNodeWithTag("FilledStar2").assertIsDisplayed()
    composeTestRule.onNodeWithTag("FilledStar3").assertIsDisplayed()
    composeTestRule.onNodeWithTag("FilledStar4").assertIsDisplayed()
    composeTestRule.onNodeWithTag("FilledStar5").assertIsDisplayed()

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
  fun doesNotDisplayImagePreviewWithoutImage() {
    composeTestRule.setContent {
      AddJourneyScreen(
          listJourneysViewModel = listJourneysViewModel, navigationActions = navigationActions)
    }

    composeTestRule.onNodeWithTag("addImageBox").assertIsDisplayed()
    composeTestRule.onNodeWithTag("selectedImagePreview").assertIsNotDisplayed()
  }

  @Test
  fun navigatesBackToOverviewOnBackButtonClick() {
    composeTestRule.setContent {
      AddJourneyScreen(
          listJourneysViewModel = listJourneysViewModel, navigationActions = navigationActions)
    }

    composeTestRule.onNodeWithTag("backButton").performClick()
    verify(navigationActions).goBack()
  }
}
