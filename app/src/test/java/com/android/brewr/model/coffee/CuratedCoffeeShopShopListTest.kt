package com.android.brewr.model.coffee

import android.content.Context
import com.android.brewr.model.map.Location
import com.android.brewr.utils.fetchNearbyCoffeeShops
import io.mockk.*
import java.time.LocalTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CuratedCoffeeShopShopListTest {
  private lateinit var context: Context
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
  fun `sortCoffeeShopsByRating sorts coffee shops by rating in descending order`() {
    // Set up test dispatcher
    Dispatchers.setMain(testDispatcher)

    // Create test data
    val unsortedCoffeeShops =
        listOf(
            createCoffeeShop("1", "Coffee Shop 1", 3.5),
            createCoffeeShop("2", "Coffee Shop 2", 4.5),
            createCoffeeShop("3", "Coffee Shop 3", 2.5),
            createCoffeeShop("4", "Coffee Shop 4", 5.0))

    // Sort coffee shops
    val sortedCoffeeShops = sortCoffeeShopsByRating(unsortedCoffeeShops)

    // Verify results
    assertEquals(4, sortedCoffeeShops.size)
    assertEquals(5.0, sortedCoffeeShops[0].rating, 0.01)
    assertEquals(4.5, sortedCoffeeShops[1].rating, 0.01)
    assertEquals(3.5, sortedCoffeeShops[2].rating, 0.01)
    assertEquals(2.5, sortedCoffeeShops[3].rating, 0.01)
  }

  @Test
  fun `sortCoffeeShopsByRating handles empty list`() {
    // Create an empty list
    val emptyCoffeeShopShops = emptyList<CoffeeShop>()

    // Sort the empty list
    val result = sortCoffeeShopsByRating(emptyCoffeeShopShops)

    // Verify the result is also an empty list
    assertEquals(0, result.size)
  }

  @Test
  fun `filterOpenCoffeeShops filters coffee shops by current open hours`() {
    // Set current time for testing
    val testTime = LocalTime.of(10, 0) // 10:00 AM

    mockkStatic(LocalTime::class)
    every { LocalTime.now() } answers
        {
          println("Mocked LocalTime.now(): $testTime")
          testTime
        }

    val coffeeShops =
        listOf(
            createCoffeeShopWithHours(
                "1", "Coffee Shop 1", listOf(Hours("Monday", "9:00 AM", "11:00 AM"))),
            createCoffeeShopWithHours(
                "2", "Coffee Shop 2", listOf(Hours("Monday", "11:00 AM", "12:00 PM"))),
            createCoffeeShopWithHours(
                "3", "Coffee Shop 3", listOf(Hours("Monday", "8:00 AM", "10:30 AM"))))

    val result = filterOpenCoffeeShops(coffeeShops)

    assertEquals(2, result.size)
    assertEquals("Coffee Shop 1", result[0].coffeeShopName)
    assertEquals("Coffee Shop 3", result[1].coffeeShopName)
  }

  @Test
  fun `filterOpenCoffeeShops excludes coffee shops with invalid hours`() {
    // Set current time for testing
    val testTime = LocalTime.of(10, 0) // 10:00 AM

    mockkStatic(LocalTime::class)
    every { LocalTime.now() } returns testTime

    val coffeeShops =
        listOf(
            createCoffeeShopWithHours(
                "1", "Coffee Shop 1", listOf(Hours("Monday", "9:00 AM", "Invalid Time"))),
            createCoffeeShopWithHours(
                "2", "Coffee Shop 2", listOf(Hours("Monday", "Invalid Time", "12:00 PM"))),
            createCoffeeShopWithHours(
                "3", "Coffee Shop 3", listOf(Hours("Monday", "9:00 AM", "11:00 AM"))))

    val result = filterOpenCoffeeShops(coffeeShops)
    println("Filtered Result: ${result.size}")
    result.forEach { println("Valid Coffee Shop: ${it.coffeeShopName}") }
    assertEquals(1, result.size)
    assertEquals("Coffee Shop 3", result[0].coffeeShopName)
  }

  private fun createCoffeeShop(id: String, name: String, rating: Double): CoffeeShop {
    return CoffeeShop(
        id = id,
        coffeeShopName = name,
        location = Location(0.0, 0.0, "Test Address"),
        rating = rating,
        hours = listOf(Hours("Monday", "9:00 AM", "5:00 PM")),
        reviews = emptyList(),
        imagesUrls = emptyList())
  }

  private fun createCoffeeShopWithHours(id: String, name: String, hours: List<Hours>): CoffeeShop {
    return CoffeeShop(
        id = id,
        coffeeShopName = name,
        location = Location(0.0, 0.0, "Test Address"),
        rating = 4.0,
        hours = hours,
        reviews = emptyList(),
        imagesUrls = emptyList())
  }
}
