package com.android.brewr.ui.UserProfile

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.android.brewr.model.coffee.CoffeeShop
import com.android.brewr.model.coffee.CoffeesViewModel
import com.android.brewr.model.coffee.FavoriteCoffeeShopsViewModel
import com.android.brewr.model.coffee.Hours
import com.android.brewr.model.coffee.Review
import com.android.brewr.model.journey.Location
import com.android.brewr.ui.navigation.NavigationActions
import com.android.brewr.ui.userProfile.UserPrivateListScreen
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify
import org.mockito.kotlin.whenever

class UserPrivateListScreenTest {

  @get:Rule val composeTestRule = createComposeRule()
  private lateinit var navigationActions: NavigationActions
  private lateinit var coffeesViewModel: CoffeesViewModel
  private val favoriteCoffeeShopsViewModel: FavoriteCoffeeShopsViewModel = mock()

  // Create a sample Coffee object
  private val mockCoffee =
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

  private val favoriteCoffeesFlow = MutableStateFlow(listOf(mockCoffee))

  @Before
  fun setUp() {
    // Mock NavController
    navigationActions = mock(NavigationActions::class.java)
    coffeesViewModel = spy(CoffeesViewModel::class.java)
    whenever(favoriteCoffeeShopsViewModel.favoriteCoffees).thenReturn(favoriteCoffeesFlow)
  }

  @Test
  fun userPrivateListScreen_displaysComponents() {
    composeTestRule.setContent {
      UserPrivateListScreen(navigationActions, coffeesViewModel, favoriteCoffeeShopsViewModel)
    }
    composeTestRule.onNodeWithTag("UserPrivateListScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("topBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("privateList").assertIsDisplayed()
    composeTestRule.onNodeWithTag("goBackButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("coffeeImage:1").performClick()
  }

  @Test
  fun userPrivateListScreen_goBackButton() {
    composeTestRule.setContent { UserPrivateListScreen(navigationActions, coffeesViewModel) }
    composeTestRule.onNodeWithTag("goBackButton").performClick()
    verify(navigationActions).goBack()
  }
}
