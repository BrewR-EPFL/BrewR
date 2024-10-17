package com.android.brewr.ui.overview

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
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

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    listJourneysViewModel = ListJourneysViewModel(mock(JourneysRepository::class.java))

    `when`(navigationActions.currentRoute()).thenReturn(Screen.JOURNEY_RECORD)
  }

  @Test
  fun displayAllComponents() {
    // Set up a mock Journey object
    val mockJourney =
        Journey(
            uid = "Ksd22S9M4pD4JswrHefa",
            imageUrl =
                "https://firebasestorage.googleapis.com/v0/b/brewr-epfl.appspot.com/o/images%2F448195f9-c8bc-4bdc-a8da-c7691c053b16?alt=media&token=bcc21fec-04d4-4dda-8972-be949c29bd23",
            description = "Matcha Latte looks like android",
            coffeeShopName = "Mock Coffee Shop",
            coffeeOrigin = CoffeeOrigin.BRAZIL,
            brewingMethod = BrewingMethod.ESPRESSO_MACHINE,
            coffeeTaste = CoffeeTaste.SWEET,
            coffeeRate = CoffeeRate.FIVE,
            date = Timestamp.now(),
            location = "Home")

    // Set up the UI with the mock journey selected
    composeTestRule.setContent {
      listJourneysViewModel.selectJourney(mockJourney)
      JourneyRecordScreen(
          listJourneysViewModel = listJourneysViewModel, navigationActions = navigationActions)
    }

    // Check that all journey details are displayed
    composeTestRule.onNodeWithTag("journeyRecordScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("coffeeShopName").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("coffeeShopName")
        .assertTextEquals("Coffee Shop: Mock Coffee Shop")
    composeTestRule
        .onNodeWithTag("description")
        .assertTextEquals("Description: Matcha Latte looks like android")
    composeTestRule.onNodeWithTag("origin").assertTextEquals("Origin: BRAZIL")
    composeTestRule
        .onNodeWithTag("brewingMethod")
        .assertTextEquals("Brewing Method: ESPRESSO_MACHINE")
    composeTestRule.onNodeWithTag("taste").assertTextEquals("Taste: SWEET")
    composeTestRule.onNodeWithTag("rating").assertTextEquals("Rating: FIVE")
    composeTestRule.onNodeWithTag("location").assertTextEquals("Location: Home")
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
