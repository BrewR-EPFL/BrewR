package com.android.brewr.model.coffee

import com.android.brewr.model.map.Location
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class FavoriteCoffeesViewModelTest {
  private lateinit var favoriteCoffeesViewModel: FavoriteCoffeesViewModel
  private lateinit var mockRepository: FavoriteCoffeesRepository
  private val testDispatcher = StandardTestDispatcher()

  // Create a sample Coffee object
  private val coffee =
      Coffee(
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

  @OptIn(ExperimentalCoroutinesApi::class)
  @Before
  fun setUp() {
    mockRepository = mock(FavoriteCoffeesRepository::class.java)
    favoriteCoffeesViewModel = FavoriteCoffeesViewModel(mockRepository)
    Dispatchers.setMain(testDispatcher)
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @After
  fun tearDown() {
    Dispatchers.resetMain() // Reset the Main dispatcher after the test
  }

  @Test
  fun getCoffeesTest() {
    favoriteCoffeesViewModel.getCoffees()
    verify(mockRepository).getCoffees(any(), any())
  }

  @Test
  fun addCoffeeTest() {
    favoriteCoffeesViewModel.addCoffee(coffee)
    verify(mockRepository).addCoffee(any(), any(), any())
  }

  @Test
  fun deleteCoffeeByIdTest() {
    favoriteCoffeesViewModel.deleteCoffeeById(coffee.id)
    verify(mockRepository).deleteCoffeeById(eq(coffee.id), any(), any())
  }

  @Test
  fun selectCoffeeTest() {
    favoriteCoffeesViewModel.selectCoffee(coffee)
    val selected = favoriteCoffeesViewModel.selectedCoffee.value
    assertEquals(coffee, selected)
  }

  @Test
  fun isLikedButtonTest() = runBlocking {
    val coffee2 =
        Coffee(
            id = "2",
            coffeeShopName = "Sample Coffee Shop",
            location = Location(40.7128, 74.0060, "123 Main St"),
            rating = 4.5,
            hours = emptyList(),
            reviews = null,
            imagesUrls = emptyList())
    whenever(mockRepository.getCoffees(any(), any())).thenAnswer {
      (it.arguments[0] as (List<Coffee>) -> Unit).invoke(listOf(coffee))
    }

    favoriteCoffeesViewModel.getCoffees() // This updates the favoriteCoffees list

    // Act
    val result = favoriteCoffeesViewModel.isCoffeeLiked(coffee2).first()

    // Assert
    assert(!result)
  }
}
