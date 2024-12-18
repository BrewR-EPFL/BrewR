package com.android.brewr.ui.explore

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import com.android.brewr.model.coffee.CoffeeShop
import com.android.brewr.model.coffee.CoffeesViewModel
import com.android.brewr.model.coffee.Hours
import com.android.brewr.model.coffee.Review
import com.android.brewr.model.map.Location
import com.android.brewr.ui.navigation.NavigationActions
import java.time.LocalDate
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.spy
import org.mockito.kotlin.verify

@RunWith(AndroidJUnit4::class)
class CoffeeShopInformationScreenTest {
  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var uiDevice: UiDevice
  private lateinit var navigationActions: NavigationActions
  private lateinit var coffeesViewModel: CoffeesViewModel

  // Set up a mock Coffee object
  private val mockCoffeeShop =
      CoffeeShop(
          "1",
          coffeeShopName = "Café tranquille",
          Location(
              latitude = 48.87847905807652,
              longitude = 2.3562626423266946,
              name = "147 Rue du Faubourg Saint-Denis, 75010 Paris, France"),
          rating = 4.9,
          hours =
              listOf(
                  Hours("Monday", open = "8:00 AM", close = "5:00 PM"),
                  Hours("Tuesday", open = "8:00 AM", close = "5:00 PM"),
                  Hours("Wednesday", open = "8:00 AM", close = "5:00 PM"),
                  Hours("Thursday", open = "8:00 AM", close = "5:00 PM"),
                  Hours("Friday", open = "8:00 AM", close = "5:00 PM"),
                  Hours("Saturday", open = "8:00 AM", close = "5:00 PM"),
                  Hours("Sunday", open = "8:00 AM", close = "5:00 PM")),
          reviews =
              listOf(
                  Review("Pablo", "Best coffee in the 10th arrondissement of Paris", 5.0),
                  Review("Thomas", "The staff is super friendly. Love their cappuccino!", 4.9),
                  Review("Claire", "Great spot to catch up with friends over a latte.", 4.8),
                  Review("Nicolas", "Delicious coffee, but seating is a bit limited.", 4.3),
                  Review("Alice", "Quiet and cozy, perfect for working in the morning.", 4.5),
                  Review("Camille", "Would come back just for the flat white!", 4.6)),
          imagesUrls =
              listOf(
                  "https://firebasestorage.googleapis.com/v0/b/brewr-epfl.appspot.com/o/images%2F2023-09-29.jpg?alt=media&token=eaaa9dbf-f402-4d12-b5ac-7c5589231a35"))

  @Before
  fun setUp() {
    // Initialize UiDevice instance
    uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    // Set content to CoffeeInformationScreen
    navigationActions = mock(NavigationActions::class.java)
    coffeesViewModel = spy(CoffeesViewModel::class.java)

    composeTestRule.setContent {
      coffeesViewModel.addCoffees(listOf((mockCoffeeShop)))
      coffeesViewModel.selectCoffee(mockCoffeeShop)
      CoffeeInformationScreen(coffeesViewModel, onBack = { navigationActions.goBack() })
    }

    // Handle location permission if prompted
    runBlocking { grantLocationPermission() }
  }

  @Test
  fun displayAllComponentsValidCoffee() {
    composeTestRule.onNodeWithTag("coffeeImage").assertExists()
    composeTestRule
        .onNodeWithTag("coffeeShopName")
        .assertIsDisplayed()
        .assertTextEquals(mockCoffeeShop.coffeeShopName)
    composeTestRule
        .onNodeWithTag("coffeeShopAddress")
        .assertIsDisplayed()
        .assertTextEquals(mockCoffeeShop.location.name)
    composeTestRule
        .onNodeWithTag(
            "coffeeShopHour${LocalDate.now().dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }}")
        .assertIsDisplayed()
        .assertTextEquals(
            "${mockCoffeeShop.hours[LocalDate.now().dayOfWeek.value - 1].day}: ${mockCoffeeShop.hours[LocalDate.now().dayOfWeek.value - 1].open} - ${mockCoffeeShop.hours[LocalDate.now().dayOfWeek.value - 1].close}")
    composeTestRule.onNodeWithTag("buttonBest").assertExists()
    composeTestRule.onNodeWithTag("buttonWorst").assertExists()
  }

  @Test
  fun backButtonClicked() {
    // Perform a click on the back button
    composeTestRule.onNodeWithTag("backButton").assertExists().performClick()

    // Verify that the navigation back to the "Explore Screen" is triggered
    verify(navigationActions).goBack()
  }

  @Test
  fun reviewsButtonClicked() {
    // Perform a click on the back button
    composeTestRule.onNodeWithTag("buttonBest").assertExists().performClick()
    mockCoffeeShop.reviews?.forEach { review ->
      if (composeTestRule.onNodeWithTag("button${review.authorName}").isDisplayed()) {
        composeTestRule
            .onNodeWithTag("button${review.authorName}")
            .assertTextEquals(
                "- ${review.authorName}: \"${review.review}\" (${
                          String.format(
                              "%.1f/5",
                              review.rating
                          )
                      })")
      }
    }
    composeTestRule.onNodeWithTag("buttonWorst").assertExists().performClick()
    mockCoffeeShop.reviews?.forEach { review ->
      if (composeTestRule.onNodeWithTag("button${review.authorName}").isDisplayed()) {
        composeTestRule
            .onNodeWithTag("button${review.authorName}")
            .assertTextEquals(
                "- ${review.authorName}: \"${review.review}\" (${
                          String.format(
                              "%.1f/5",
                              review.rating
                          )
                      })")
      }
    }
  }

  private fun grantLocationPermission() {
    // Grant location permission automatically for the test
    repeat(10) { // Try up to 10 times with a delay
      val allowButton = uiDevice.findObject(UiSelector().text("While using the app"))
      if (allowButton.exists()) {
        allowButton.click()
        return
      }
      // Short delay between checks
      Thread.sleep(500)
    }
  }
}
