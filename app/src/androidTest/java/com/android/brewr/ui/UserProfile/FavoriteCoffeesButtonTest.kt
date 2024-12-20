package com.android.brewr.ui.UserProfile

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.android.brewr.model.coffee.CoffeeShop
import com.android.brewr.model.coffee.FavoriteCoffeeShopsRepository
import com.android.brewr.model.coffee.FavoriteCoffeeShopsViewModel
import com.android.brewr.model.map.Location
import com.android.brewr.ui.explore.FavoriteCoffeesButton
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock

// Test Rule to set up the Compose environment
class FavoriteCoffeesButtonTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var mockViewModel: FavoriteCoffeeShopsViewModel
  private lateinit var mockRepository: FavoriteCoffeeShopsRepository
  // Create a sample Coffee object
  val coffee =
      CoffeeShop(
          id = "1",
          coffeeShopName = "Sample Coffee Shop",
          location = Location(40.7128, 74.0060, "123 Main St"),
          rating = 4.5,
          hours = emptyList(),
          reviews = null,
          imagesUrls = emptyList())

  @Before
  fun setUp() {
    mockRepository = mock(FavoriteCoffeeShopsRepository::class.java)
    mockViewModel = FavoriteCoffeeShopsViewModel(mockRepository)
    composeTestRule.setContent {
      FavoriteCoffeesButton(coffee = coffee, favoriteCoffeeShopsViewModel = mockViewModel)
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
