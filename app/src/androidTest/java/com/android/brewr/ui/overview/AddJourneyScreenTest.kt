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
