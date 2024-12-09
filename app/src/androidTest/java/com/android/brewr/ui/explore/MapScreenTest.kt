package com.android.brewr.ui.explore

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import com.android.brewr.model.coffee.Coffee
import com.android.brewr.model.coffee.Hours
import com.android.brewr.model.coffee.Review
import com.android.brewr.model.journey.BrewingMethod
import com.android.brewr.model.journey.CoffeeOrigin
import com.android.brewr.model.journey.CoffeeRate
import com.android.brewr.model.journey.CoffeeTaste
import com.android.brewr.model.journey.Journey
import com.android.brewr.model.journey.JourneysRepository
import com.android.brewr.model.journey.ListJourneysViewModel
import com.android.brewr.model.map.Location
import com.google.firebase.Timestamp
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock

@RunWith(AndroidJUnit4::class)
class MapScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var uiDevice: UiDevice
  private lateinit var listJourneysViewModel: ListJourneysViewModel
  private lateinit var sampleCoffees: List<Coffee>
  private lateinit var sampleJourneys: List<Journey>

  @Before
  fun setUp() {
    // Initialize UiDevice instance
    uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    listJourneysViewModel = ListJourneysViewModel(mock(JourneysRepository::class.java))

    // Sample journeys and coffees for testing
    sampleJourneys =
        listOf(
            Journey(
                "1",
                "",
                "Display as a saved on the map",
                Location(
                    latitude = 37.4305087,
                    longitude = -122.0854755,
                    name =
                        "Shoreline Golf Links, 2940, North Shoreline Boulevard, Mountain View, Santa Clara County, California, 94043, United States"),
                CoffeeOrigin.DEFAULT,
                BrewingMethod.DEFAULT,
                CoffeeTaste.DEFAULT,
                CoffeeRate.DEFAULT,
                Timestamp.now()),
            Journey(
                "2",
                "",
                "Home Journey",
                Location(),
                CoffeeOrigin.DEFAULT,
                BrewingMethod.DEFAULT,
                CoffeeTaste.DEFAULT,
                CoffeeRate.DEFAULT,
                Timestamp.now()))
    sampleJourneys.forEach { listJourneysViewModel.addJourney(it) }

    sampleCoffees =
        listOf(
            Coffee(
                "1",
                "Starbucks",
                Location(latitude = 46.5228, longitude = 6.6285, name = "Lausanne 1"),
                4.5,
                listOf(Hours("Monday", "10", "20"), Hours("Tuesday", "10", "20")),
                listOf(Review("Lei", "good", 5.0)),
                listOf("test.jpg")),
            Coffee(
                "2",
                "Shoreline Golf Links",
                Location(
                    latitude = 37.4305087, longitude = -122.0854755, name = "Shoreline Golf Links"),
                4.5,
                listOf(Hours("Monday", "10", "20"), Hours("Tuesday", "10", "20")),
                listOf(Review("Pablo", "Awesome", 5.0)),
                listOf("test.jpg")),
            Coffee(
                "3",
                "McDonald's",
                Location(latitude = 46.5228, longitude = 6.7285, name = "Lausanne 1"),
                4.5,
                listOf(Hours("Monday", "10", "20"), Hours("Tuesday", "10", "20")),
                listOf(Review("Lei", "good", 5.0)),
                listOf("test.jpg")),
            Coffee(
                "4",
                "default",
                Location(latitude = 46.5228, longitude = 6.7285, name = "Lausanne 1"),
                4.5,
                listOf(Hours("Monday", "10", "20"), Hours("Tuesday", "10", "20")),
                listOf(Review("Lei", "good", 5.0)),
                listOf("test.jpg")))
    // Handle location permission if prompted
    runBlocking { grantLocationPermission() }
  }

  @Test
  fun mapScreen_IsDisplayedProperly() {
    sampleJourneys.forEach { listJourneysViewModel.addJourney(it) }
    composeTestRule.setContent { MapScreen(sampleCoffees, listJourneysViewModel) }
    composeTestRule.onNodeWithTag("mapScreen").assertIsDisplayed()
  }

  @Test
  fun userLocationMarker_IsDisplayedWhenAvailable() {
    composeTestRule.setContent { MapScreen(sampleCoffees, listJourneysViewModel) }
    composeTestRule.onNodeWithTag("mapScreen").assertIsDisplayed()
  }

  @Test
  fun customIcons_AreAssignedToMarkers() {
    composeTestRule.setContent {
      sampleCoffees.forEach { coffee ->
        val icon = getMarkerIcon(coffee, listJourneysViewModel)
        assertNotNull(icon)
      }
    }
  }

  @Test
  fun mapScreen_DisplaysProperlyWithNoData() {
    composeTestRule.setContent { MapScreen(emptyList(), listJourneysViewModel) }
    composeTestRule.onNodeWithTag("mapScreen").assertIsDisplayed()
  }

  @Test
  fun locationPermission_IsHandledCorrectly() {
    composeTestRule.setContent { MapScreen(sampleCoffees, listJourneysViewModel) }
    val allowButton = uiDevice.findObject(UiSelector().text("While using the app"))
    if (allowButton.exists()) {
      allowButton.click()
    }
    composeTestRule.onNodeWithTag("mapScreen").assertIsDisplayed()
  }

  private fun grantLocationPermission() {
    repeat(10) { // Try up to 10 times with a delay
      val allowButton = uiDevice.findObject(UiSelector().text("While using the app"))
      if (allowButton.exists()) {
        allowButton.click()
        return
      }
      Thread.sleep(500)
    }
  }
}
