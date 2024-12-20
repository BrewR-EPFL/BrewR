package com.android.brewr.ui.recommendation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.android.brewr.model.coffee.CoffeeShop
import com.android.brewr.model.coffee.CoffeesViewModel
import com.android.brewr.model.journey.Location
import com.android.brewr.model.recommendation.RecommendationViewModel
import com.android.brewr.ui.navigation.NavigationActions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*

class RecommendScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions
  private lateinit var recommendationViewModel: RecommendationViewModel
  private lateinit var coffeesViewModel: CoffeesViewModel

  private val sampleCoffeeShop =
      CoffeeShop(
          id = "1",
          coffeeShopName = "Sample Coffee Shop",
          location = Location(latitude = 0.0, longitude = 0.0, name = "Sample Location"),
          rating = 4.5,
          hours = emptyList(),
          reviews = emptyList(),
          imagesUrls = emptyList())

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    coffeesViewModel = spy(CoffeesViewModel::class.java)
    recommendationViewModel = mock(RecommendationViewModel::class.java)

    // Mock recommendedCoffees StateFlow with default empty set
    `when`(recommendationViewModel.recommendedCoffees).thenReturn(MutableStateFlow(mutableSetOf()))
  }

  @Test
  fun recommendScreen_displaysEmptyState_whenNoRecommendations() {
    composeTestRule.setContent {
      RecommendScreen(
          recommendationViewModel = recommendationViewModel,
          coffeesViewModel = coffeesViewModel,
          navigationActions = navigationActions)
    }

    composeTestRule.onNodeWithTag("emptyRecommendation").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("emptyJourneyPrompt")
        .assertTextEquals(
            "Discover personalized coffee recommendations by exploring and recording your journeys")
        .assertIsDisplayed()
  }

  @Test
  fun recommendScreen_displaysRecommendations() = runTest {
    // Set up the mock to return a set containing the sample coffee shop
    val recommendationsFlow = MutableStateFlow(mutableSetOf(sampleCoffeeShop))
    `when`(recommendationViewModel.recommendedCoffees).thenReturn(recommendationsFlow)

    composeTestRule.setContent {
      RecommendScreen(
          recommendationViewModel = recommendationViewModel,
          coffeesViewModel = coffeesViewModel,
          navigationActions = navigationActions)
    }

    // Assert that the list is displayed
    composeTestRule.onNodeWithTag("private_List").assertIsDisplayed()

    // No need for explicit verifications since we're using relaxed mocks
  }
}
