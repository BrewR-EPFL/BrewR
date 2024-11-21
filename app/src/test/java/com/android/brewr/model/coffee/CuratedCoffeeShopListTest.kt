package com.android.brewr.model.coffee

import android.content.Context
import com.android.brewr.model.location.Location
import com.android.brewr.utils.fetchNearbyCoffeeShops
import com.google.android.gms.maps.model.LatLng
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CuratedCoffeeShopListTest {
  private lateinit var context: Context
  private val currentLocation = LatLng(37.7749, -122.4194)
  private val testScope = TestScope()
  private val testDispatcher = StandardTestDispatcher()

  @Before
  fun setUp() {
    context = mockk(relaxed = true)
    mockkStatic(::fetchNearbyCoffeeShops)
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain()
    unmockkAll()
  }

  @Test
  fun `fetchAndSortCoffeeShopsByRating sorts coffee shops by rating in descending order`() {
    // Set up test dispatcher
    Dispatchers.setMain(testDispatcher)

    // Create test data
    val unsortedCoffeeShops =
        listOf(
            createCoffeeShop("1", "Coffee Shop 1", 3.5),
            createCoffeeShop("2", "Coffee Shop 2", 4.5),
            createCoffeeShop("3", "Coffee Shop 3", 2.5),
            createCoffeeShop("4", "Coffee Shop 4", 5.0))

    // Mock fetchNearbyCoffeeShops to immediately call onSuccess with our test data
    every {
      fetchNearbyCoffeeShops(
          scope = any(),
          context = any(),
          currentLocation = any(),
          radius = any(),
          onSuccess = any())
    } answers { lastArg<(List<Coffee>) -> Unit>().invoke(unsortedCoffeeShops) }

    // Collect results
    var result = listOf<Coffee>()
    fetchAndSortCoffeeShopsByRating(
        scope = testScope, context = context, currentLocation = currentLocation) {
          result = it
        }

    // Advance coroutines
    testScope.advanceUntilIdle()

    // Verify results
    assertEquals(4, result.size)
    assertEquals(5.0, result[0].rating, 0.01)
    assertEquals(4.5, result[1].rating, 0.01)
    assertEquals(3.5, result[2].rating, 0.01)
    assertEquals(2.5, result[3].rating, 0.01)
  }

  @Test
  fun `fetchAndSortCoffeeShopsByRating handles empty list`() {
    // Set up test dispatcher
    Dispatchers.setMain(testDispatcher)

    // Mock fetchNearbyCoffeeShops to return empty list
    every {
      fetchNearbyCoffeeShops(
          scope = any(),
          context = any(),
          currentLocation = any(),
          radius = any(),
          onSuccess = any())
    } answers { lastArg<(List<Coffee>) -> Unit>().invoke(emptyList()) }

    // Collect results
    var result = listOf<Coffee>()
    fetchAndSortCoffeeShopsByRating(
        scope = testScope, context = context, currentLocation = currentLocation) {
          result = it
        }

    // Advance coroutines
    testScope.advanceUntilIdle()

    // Verify results
    assertEquals(0, result.size)
  }

  @Test
  fun `fetchAndSortCoffeeShopsByRating uses custom radius`() {
    // Set up test dispatcher
    Dispatchers.setMain(testDispatcher)

    val customRadius = 5000.0

    // Mock and capture parameters
    val radiusSlot = slot<Double>()
    every {
      fetchNearbyCoffeeShops(
          scope = any(),
          context = any(),
          currentLocation = any(),
          radius = capture(radiusSlot),
          onSuccess = any())
    } answers { lastArg<(List<Coffee>) -> Unit>().invoke(emptyList()) }

    // Call function with custom radius
    fetchAndSortCoffeeShopsByRating(
        scope = testScope,
        context = context,
        currentLocation = currentLocation,
        radius = customRadius) {}

    // Verify custom radius was passed through
    assertEquals(customRadius, radiusSlot.captured, 0.01)
  }

  private fun createCoffeeShop(id: String, name: String, rating: Double): Coffee {
    return Coffee(
        id = id,
        coffeeShopName = name,
        location = Location(0.0, 0.0, "Test Address"),
        rating = rating,
        hours = listOf(Hours("Monday", "9:00 AM", "5:00 PM")),
        reviews = emptyList(),
        imagesUrls = emptyList())
  }
}
