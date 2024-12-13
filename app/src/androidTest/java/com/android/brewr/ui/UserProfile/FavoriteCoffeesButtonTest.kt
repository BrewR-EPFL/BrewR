package com.android.brewr.ui.UserProfile

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.android.brewr.model.coffee.Coffee
import com.android.brewr.model.coffee.FavoriteCoffeesRepository
import com.android.brewr.model.coffee.FavoriteCoffeesViewModel
import com.android.brewr.model.location.Location
import com.android.brewr.ui.explore.FavoriteCoffeesButton
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock

// Test Rule to set up the Compose environment
class FavoriteCoffeesButtonTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var mockViewModel: FavoriteCoffeesViewModel
  private lateinit var mockRepository: FavoriteCoffeesRepository
  // Create a sample Coffee object
  val coffee =
      Coffee(
          id = "1",
          coffeeShopName = "Sample Coffee Shop",
          location = Location(40.7128, 74.0060, "123 Main St"),
          rating = 4.5,
          hours = emptyList(),
          reviews = null,
          imagesUrls = emptyList())

  @Before
  fun setUp() {
    mockRepository = mock(FavoriteCoffeesRepository::class.java)
    mockViewModel = FavoriteCoffeesViewModel(mockRepository)
    composeTestRule.setContent {
      FavoriteCoffeesButton(coffee = coffee, favoriteCoffeesViewModel = mockViewModel)
    }
  }

  @Test
  fun testLikeButton() {
    // Test if the button is displayed correctly (initially as not liked)
    composeTestRule.onNodeWithTag("likedButton").assertExists()
    // Simulate button click
    composeTestRule.onNodeWithTag("likedButton").performClick()
  }
}
