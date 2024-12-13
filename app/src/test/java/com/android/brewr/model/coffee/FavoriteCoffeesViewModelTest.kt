package com.android.brewr.model.coffee

import com.android.brewr.model.location.Location
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
  val coffee =
      Coffee(
          id = "1",
          coffeeShopName = "Sample Coffee Shop",
          location = Location(40.7128, 74.0060, "123 Main St"),
          rating = 4.5,
          hours = emptyList(),
          reviews = null,
          imagesUrls = emptyList())

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
