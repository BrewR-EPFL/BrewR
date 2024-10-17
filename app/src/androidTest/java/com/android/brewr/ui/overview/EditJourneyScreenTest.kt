package com.android.brewr.ui.overview

import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
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
import com.android.brewr.ui.navigation.NavigationActions
import com.android.brewr.ui.navigation.Screen
import com.google.firebase.Timestamp
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.spy
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.never
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
          coffeeShopName = "Starbucks",
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

    // Test the coffee shop checkbox interaction
    composeTestRule.onNodeWithTag("coffeeShopCheckRow").assertHasClickAction().performClick()

    // Check if the Coffee Shop Name field displays the correct coffee shop name
    composeTestRule
        .onNodeWithTag("coffeeShopNameField")
        .assertIsDisplayed()
        .assert(hasText("Starbucks"))

    // After clicking, the Coffee Shop Name field should appear
    composeTestRule
        .onNodeWithTag("coffeeShopNameField")
        .assertExists()
        .performTextInput("Local Brew")

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
        .onNodeWithTag("filledButton:${BrewingMethod.POUR_OVER.name}")
        .performScrollTo()
        .assertIsDisplayed()

    // Test brewing method button selection
    composeTestRule
        .onNodeWithTag("outlinedButton:${BrewingMethod.FRENCH_PRESS.name}")
        .performScrollTo()
        .assertIsDisplayed()
        .performClick()

    // Test taste method button selection
    composeTestRule
        .onNodeWithTag("filledButton:${CoffeeTaste.NUTTY.name}")
        .performScrollTo()
        .assertIsDisplayed()

    // Test taste button selection
    composeTestRule
        .onNodeWithTag("outlinedButton:${CoffeeTaste.SPICY.name}")
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

    // Enter a date into the date field
    composeTestRule.onNodeWithTag("inputDate").assertIsDisplayed().performTextInput("15/10/2024")

    // Simulate clicking the Save button
    composeTestRule.onNodeWithTag("journeySave").assertHasClickAction().performClick()
  }

  @Test
  fun doesNotSubmitWithInvalidDate() {
    listJourneysViewModel.selectJourney(journey1)
    composeTestRule.setContent {
      EditJourneyScreen(
          listJourneysViewModel = listJourneysViewModel, navigationActions = navigationActions)
    }

    // Clear any existing input in the date field and enter an invalid date
    composeTestRule.onNodeWithTag("inputDate").performTextClearance()
    composeTestRule.onNodeWithTag("inputDate").performTextInput("notadate")

    // Click the save button
    composeTestRule.onNodeWithTag("journeySave").performClick()

    // Verify that the function to add the journey was NOT called due to invalid date
    verify(repositoryMock, never()).updateJourney(any(), any(), any())
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
    composeTestRule.onNodeWithTag("coffeeShopNameField").performTextClearance()
    composeTestRule.onNodeWithTag("coffeeShopNameField").performTextInput("Pablo's Coffee")
    // Coffee Origin
    composeTestRule
        .onNodeWithTag("outlinedButton:${BrewingMethod.FRENCH_PRESS.name}")
        .performScrollTo()
        .performClick()
    // Brewing Method
    composeTestRule
        .onNodeWithTag("outlinedButton:${BrewingMethod.COLD_BREW.name}")
        .performScrollTo()
        .performClick()
    // Coffee Taste
    composeTestRule
        .onNodeWithTag("outlinedButton:${CoffeeTaste.FRUITY.name}")
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
