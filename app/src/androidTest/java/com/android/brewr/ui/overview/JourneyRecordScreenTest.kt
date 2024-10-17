package com.android.brewr.ui.overview

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.brewr.model.journey.*
import com.android.brewr.ui.navigation.NavigationActions
import com.android.brewr.ui.navigation.Screen
import com.google.firebase.Timestamp
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.never
import org.mockito.kotlin.verify

@RunWith(AndroidJUnit4::class)
class JourneyRecordScreenTest {

  private lateinit var navigationActions: NavigationActions
  private lateinit var listJourneysViewModel: ListJourneysViewModel

  @get:Rule val composeTestRule = createComposeRule()
  // Set up a mock Journey object
  private val mockJourney =
      Journey(
          uid = "Ksd22S9M4pD4JswrHefa",
          imageUrl =
              "https://firebasestorage.googleapis.com/v0/b/brewr-epfl.appspot.com/o/images%2F448195f9-c8bc-4bdc-a8da-c7691c053b16?alt=media&token=bcc21fec-04d4-4dda-8972-be949c29bd23",
          description = "Matcha Latte looks like android",
          coffeeShopName = "Coffee Shop: Mock Coffee Shop",
          coffeeOrigin = CoffeeOrigin.BRAZIL,
          brewingMethod = BrewingMethod.ESPRESSO_MACHINE,
          coffeeTaste = CoffeeTaste.SWEET,
          coffeeRate = CoffeeRate.THREE,
          date = Timestamp.now(),
          location = "Home")

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    listJourneysViewModel = ListJourneysViewModel(mock(JourneysRepository::class.java))

    `when`(navigationActions.currentRoute()).thenReturn(Screen.JOURNEY_RECORD)
  }

  @Test
  fun displayAllComponents() {
    // Set up the UI with the mock journey selected
    composeTestRule.setContent {
      listJourneysViewModel.selectJourney(mockJourney)
      JourneyRecordScreen(
          listJourneysViewModel = listJourneysViewModel, navigationActions = navigationActions)
    }
    // Verify if the back button is clickable
    composeTestRule.onNodeWithTag("backButton").assertIsDisplayed().performClick()
    // Check that all journey details are displayed
    composeTestRule.onNodeWithTag("deleteButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("journeyRecordScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("coffeeShopName").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("coffeeShopName")
        .assertTextEquals("Coffee Shop: Mock Coffee Shop")
    composeTestRule
        .onNodeWithTag("journeyDescription")
        .assertTextEquals("Matcha Latte looks like android")
    composeTestRule.onNodeWithTag("CoffeeOrigin").assertTextEquals("BRAZIL")
    composeTestRule.onNodeWithTag("brewingMethod").assertTextEquals("ESPRESSO MACHINE")
    composeTestRule.onNodeWithTag("coffeeTaste").assertTextEquals("SWEET")
    // Verify if the Coffee Rating shows 5 filled stars
    composeTestRule.onNodeWithTag("rateRow").performScrollTo().apply {
      onChildren().filterToOne(hasTestTag("FilledStar1")).assertIsDisplayed()
      onChildren().filterToOne(hasTestTag("FilledStar2")).assertIsDisplayed()
      onChildren().filterToOne(hasTestTag("FilledStar3")).assertIsDisplayed()
      onChildren().filterToOne(hasTestTag("OutlinedStar4")).assertIsDisplayed() // Outlined Star
      onChildren().filterToOne(hasTestTag("OutlinedStar5")).assertIsDisplayed() // Outlined Star
    }
    composeTestRule.onNodeWithTag("date").assertExists()
    composeTestRule.onNodeWithTag("editButton").assertIsDisplayed()
  }

  @Test
  fun editButtonClicked() {
    composeTestRule.setContent {
      listJourneysViewModel.selectJourney(mockJourney)
      JourneyRecordScreen(
          listJourneysViewModel = listJourneysViewModel, navigationActions = navigationActions)
    }
    // Perform a click on the edit button
    composeTestRule.onNodeWithTag("editButton").performClick()

    // Verify that the navigation to the "Edit Screen" is triggered
    verify(navigationActions).navigateTo(Screen.EDIT_JOURNEY)
  }

  @Test
  fun deleteButtonClickedYes() {
    composeTestRule.setContent {
      listJourneysViewModel.selectJourney(mockJourney)
      JourneyRecordScreen(
          listJourneysViewModel = listJourneysViewModel, navigationActions = navigationActions)
    }
    // Perform a click on the edit button
    composeTestRule.onNodeWithTag("deleteButton").performClick()

    // Verify the AlertDialog is displayed
    composeTestRule.onNodeWithTag("Alter dialog").assertIsDisplayed()
    composeTestRule.onNodeWithText("Delete Journey").assertIsDisplayed()
    // Test confirming delete
    composeTestRule.onNodeWithTag("button Yes").performClick()
  }

  @Test
  fun deleteButtonClickedNo() {
    composeTestRule.setContent {
      listJourneysViewModel.selectJourney(mockJourney)
      JourneyRecordScreen(
          listJourneysViewModel = listJourneysViewModel, navigationActions = navigationActions)
    }
    // Perform a click on the edit button
    composeTestRule.onNodeWithTag("deleteButton").performClick()

    // Verify the AlertDialog is displayed
    composeTestRule.onNodeWithTag("Alter dialog").assertIsDisplayed()
    composeTestRule.onNodeWithText("Delete Journey").assertIsDisplayed()
    // Test confirming delete
    composeTestRule.onNodeWithTag("button No").performClick()
  }

  @Test
  fun doesNotNavigateWithInvalidJourney() {
    // Setting an invalid journey
    val invalidJourney =
        Journey(
            uid = "",
            imageUrl = "",
            description = "",
            coffeeShopName = "",
            coffeeOrigin = CoffeeOrigin.BRAZIL,
            brewingMethod = BrewingMethod.ESPRESSO_MACHINE,
            coffeeTaste = CoffeeTaste.SWEET,
            coffeeRate = CoffeeRate.FIVE,
            date = Timestamp.now(),
            location = "")

    composeTestRule.setContent {
      listJourneysViewModel.selectJourney(invalidJourney)
      JourneyRecordScreen(
          listJourneysViewModel = listJourneysViewModel, navigationActions = navigationActions)
    }

    composeTestRule.onNodeWithTag("journeyRecordScreen").assertIsDisplayed()

    // Verify no navigation action is taken with invalid data
    verify(navigationActions, never()).navigateTo("Journey Screen")
  }
}
