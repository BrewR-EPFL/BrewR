package com.android.brewr.ui.overview

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import com.android.brewr.model.coffee.Coffee
import com.android.brewr.model.coffee.Hours
import com.android.brewr.model.coffee.Review
import com.android.brewr.model.location.Location
import java.time.LocalDate
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CoffeeInformationScreenTest {
  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var uiDevice: UiDevice

  // Set up a mock Coffee object
  private val mockCoffee =
      Coffee(
          "1",
          coffeeShopName = "Café tranquille",
          Location(
              latitude = 48.87847905807652,
              longitude = 2.3562626423266946,
              address = "147 Rue du Faubourg Saint-Denis, 75010 Paris, France"),
          rating = 4.9,
          hours =
              listOf(
                  Hours(open = "8:00 AM", close = "5:00 PM"),
                  Hours(open = "8:00 AM", close = "5:00 PM"),
                  Hours(open = "8:00 AM", close = "5:00 PM"),
                  Hours(open = "8:00 AM", close = "5:00 PM"),
                  Hours(open = "8:00 AM", close = "5:00 PM"),
                  Hours(open = "8:00 AM", close = "5:00 PM"),
                  Hours(open = "8:00 AM", close = "5:00 PM")),
          reviews = listOf(Review("Pablo", "Best coffee in the 10th arrondissement of Paris", 5.0)),
          imagesUrls =
              listOf(
                  "https://firebasestorage.googleapis.com/v0/b/brewr-epfl.appspot.com/o/images%2F2023-09-29.jpg?alt=media&token=eaaa9dbf-f402-4d12-b5ac-7c5589231a35"))

  @Before
  fun setUp() {
    // Initialize UiDevice instance
    uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    // Set content to CoffeeInformationScreen
    composeTestRule.setContent { CoffeeInformationScreen(coffee = mockCoffee) }

    // Handle location permission if prompted
    runBlocking { grantLocationPermission() }
  }

  @Test
  fun displayAllComponentsValidCoffee() {
    composeTestRule.onNodeWithTag("coffeeInformationScreen").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("coffeeShopName")
        .assertIsDisplayed()
        .assertTextEquals(mockCoffee.coffeeShopName)
    composeTestRule
        .onNodeWithTag("coffeeShopAddress")
        .assertIsDisplayed()
        .assertTextEquals("Address: " + mockCoffee.location.address)
    composeTestRule
        .onNodeWithTag("coffeeShopHours")
        .assertIsDisplayed()
        .assertTextEquals(
            "Opening Hours: " +
                "${mockCoffee.hours[LocalDate.now().dayOfWeek.value - 1].open} - ${mockCoffee.hours[LocalDate.now().dayOfWeek.value - 1].close}")
    composeTestRule
        .onNodeWithTag("coffeeShopRating")
        .assertIsDisplayed()
        .assertTextEquals(("Rating: " + String.format("%.1f/5", mockCoffee.rating)))
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
