package com.android.brewr.ui.overview


import android.net.Uri
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.brewr.model.journey.BrewingMethod
import com.android.brewr.model.journey.CoffeeOrigin
import com.android.brewr.model.journey.CoffeeRate
import com.android.brewr.model.journey.CoffeeTaste
import com.android.brewr.model.journey.ListJourneysViewModel
import com.android.brewr.ui.navigation.NavigationActions
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*

@RunWith(AndroidJUnit4::class)
class AddJourneyScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // Create a mocked NavigationActions
    private val navigationActions = mock(NavigationActions::class.java)

    // Create a spy on the ListJourneysViewModel
    private val listJourneysViewModel = spy(ListJourneysViewModel(mock()))



    @Test
    fun testAddJourneyScreenDisplaysAndInteractsCorrectly() {
        composeTestRule.setContent {
            AddJourneyScreen(
                listJourneysViewModel = listJourneysViewModel,
                navigationActions = navigationActions
            )
        }

        // Check if the title "Your Journey" is displayed
        composeTestRule.onNodeWithTag("YourJourneyTitle").assertIsDisplayed()

        // Test Add Photo functionality (simulate photo click)
        composeTestRule.onNodeWithTag("addPhotoBox").assertIsDisplayed().assertHasClickAction()



        // Check if the description text field is displayed and type text into it
        composeTestRule.onNodeWithTag("inputJourneyDescription").assertIsDisplayed()
            .performTextInput("Amazing Coffee Experience")

        // Test the coffee shop checkbox interaction
        composeTestRule.onNodeWithTag("coffeeShopCheckRow").assertHasClickAction().performClick()

        // After clicking, the Coffee Shop Name field should appear
        composeTestRule.onNodeWithTag("coffeeShopNameField").assertExists()
            .performTextInput("Starbucks")

        // Test Coffee Origin dropdown (click and select an option)
        composeTestRule.onNodeWithTag("inputCoffeeOrigin").assertIsDisplayed().performClick()
        composeTestRule.onNodeWithTag("dropdownMenuCoffeeOrigin").assertExists()

        // Simulate selecting an origin from the dropdown
        composeTestRule.onNodeWithText(CoffeeOrigin.BRAZIL.name).performClick()

        // Test brewing method button selection
        composeTestRule.onNodeWithTag("Button:${BrewingMethod.POUR_OVER.name}")
            .assertIsDisplayed().performClick()

        // Test taste button selection
        composeTestRule.onNodeWithTag("Button:${CoffeeTaste.BITTER.name}")
            .assertIsDisplayed().performClick()

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

        // Enter a date into the date field
        composeTestRule.onNodeWithTag("inputDate").assertIsDisplayed()
            .performTextInput("12/12/2024")

        // Simulate clicking the Save button
        composeTestRule.onNodeWithTag("journeySave").assertHasClickAction().performClick()


    }

    @Test
    fun doesNotSubmitWithInvalidDate() {
        composeTestRule.setContent {
            AddJourneyScreen(
                listJourneysViewModel = listJourneysViewModel,
                navigationActions = navigationActions
            )
        }

        // Clear any existing input in the date field and enter an invalid date
        composeTestRule.onNodeWithTag("inputDate").performTextClearance()
        composeTestRule.onNodeWithTag("inputDate").performTextInput("notadate")

        // Click the save button
        composeTestRule.onNodeWithTag("journeySave").performClick()

        // Verify that the function to add the journey was NOT called due to invalid date
        verify(listJourneysViewModel, never()).addJourney(any())
    }
}
