package com.android.brewr.model.journey

import com.android.brewr.model.map.Location
import com.google.firebase.Timestamp
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.eq

class ListJourneysViewModelTest {
  private lateinit var journeysRepository: JourneysRepository
  private lateinit var listJourneysViewModel: ListJourneysViewModel

  private val journey =
      Journey(
          uid = "journey1",
          imageUrl = "https://example.com/image.jpg",
          description = "A wonderful coffee journey.",
          location =
              Location(
                  46.5183076,
                  6.6338096,
                  "Coffee page, Rue du Midi, Lausanne, District de Lausanne, Vaud, 1003, Schweiz/Suisse/Svizzera/Svizra"),
          coffeeOrigin = CoffeeOrigin.BRAZIL,
          brewingMethod = BrewingMethod.POUR_OVER,
          coffeeTaste = CoffeeTaste.NUTTY,
          coffeeRate = CoffeeRate.ONE,
          date = Timestamp.now())

  @Before
  fun setUp() {
    journeysRepository = mock(JourneysRepository::class.java)
    listJourneysViewModel = ListJourneysViewModel(journeysRepository)
  }

  @Test
  fun getNewUid() {
    `when`(journeysRepository.getNewUid()).thenReturn("uid")
    assertThat(listJourneysViewModel.getNewUid(), `is`("uid"))
  }

  @Test
  fun getJourneysCallsRepository() {
    listJourneysViewModel.getJourneys()
    verify(journeysRepository).getJourneysOfCurrentUser(any(), any())
  }

  @Test
  fun addJourneyCallsRepository() {
    listJourneysViewModel.addJourney(journey)
    verify(journeysRepository).addJourney(eq(journey), any(), any())
  }

  @Test
  fun updateJourneyCallsRepository() {
    listJourneysViewModel.updateJourney(journey)
    verify(journeysRepository).updateJourney(eq(journey), any(), any())
  }

  @Test
  fun deleteJourneyCallsRepository() {
    listJourneysViewModel.deleteJourneyById(journey.uid)
    verify(journeysRepository).deleteJourneyById(eq(journey.uid), any(), any())
  }

  @Test
  fun selectJourney() {
    listJourneysViewModel.selectJourney(journey)
    val selected = listJourneysViewModel.selectedJourney.value
    assertEquals(journey, selected)
  }
}
