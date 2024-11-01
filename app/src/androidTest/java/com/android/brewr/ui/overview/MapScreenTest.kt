package com.android.brewr.ui.overview

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import com.android.brewr.model.journey.Location
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MapScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var uiDevice: UiDevice

  @Before
  fun setUp() {
    // Initialize UiDevice instance
    uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    // Sample list of locations for testing
    val sampleLocations =
        listOf(
            Location(latitude = 46.5228, longitude = 6.6285, name = "Lausanne 1"),
            Location(latitude = 46.5211, longitude = 6.6276, name = "Lausanne 2"))

    // Set content to ExploreScreen
    composeTestRule.setContent { MapScreen(listLocations = sampleLocations) }

    // Handle location permission if prompted
    runBlocking { grantLocationPermission() }
  }

  @Test
  fun mapIsDisplayedInExploreScreen() {
    // Verify that the map (tagged as "mapScreen") is displayed
    composeTestRule.onNodeWithTag("mapScreen").assertIsDisplayed()
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
