package com.android.brewr.ui.recommendation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.android.brewr.model.coffee.CoffeesViewModel
import com.android.brewr.model.recommendation.RecommendationViewModel
import com.android.brewr.ui.navigation.NavigationActions
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*

class RecommendScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions
  private lateinit var recommendationViewModel: RecommendationViewModel
  private lateinit var coffeesViewModel: CoffeesViewModel

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
}
