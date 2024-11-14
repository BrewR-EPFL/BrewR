package com.android.brewr.model.journey

import android.os.Looper
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import com.android.brewr.model.map.Location
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import junit.framework.TestCase.fail
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class JourneysRepositoryFirestoreTest {

  @Mock private lateinit var mockFirestore: FirebaseFirestore
  @Mock private lateinit var mockCollectionReference: CollectionReference
  @Mock private lateinit var mockDocumentReference: DocumentReference
  @Mock private lateinit var mockQuerySnapshot: QuerySnapshot

  private lateinit var journeysRepository: JourneysRepositoryFirestore

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
    MockitoAnnotations.openMocks(this)
    // Initialize Firebase if necessary
    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }
    mockFirestore = mock(FirebaseFirestore::class.java)
    journeysRepository = JourneysRepositoryFirestore(mockFirestore)

    `when`(mockFirestore.collection(org.mockito.kotlin.any())).thenReturn(mockCollectionReference)

    `when`(mockCollectionReference.document(org.mockito.kotlin.any()))
        .thenReturn(mockDocumentReference)
    `when`(mockCollectionReference.document()).thenReturn(mockDocumentReference)
  }

  @Test
  fun testGetNewUid() {
    `when`(mockDocumentReference.id).thenReturn("1")
    val uid = journeysRepository.getNewUid()
    assert(uid == "1")
  }

  @Test
  fun getJourneys_callsDocuments() {
    // Ensure that mockToDoQuerySnapshot is properly initialized and mocked
    `when`(mockCollectionReference.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))

    // Ensure the QuerySnapshot returns a list of mock DocumentSnapshots
    `when`(mockQuerySnapshot.documents).thenReturn(listOf())

    // Call the method under test
    journeysRepository.getJourneys(
        onSuccess = {
          // Do nothing; we just want to verify that the 'documents' field was accessed
        },
        onFailure = { fail("Failure callback should not be called") })
    // Verify that the 'documents' field was accessed
    verify(org.mockito.kotlin.timeout(100)) { (mockQuerySnapshot).documents }
  }

  @Test
  fun addJourney_shouldCallFirestoreCollection() {
    `when`(mockDocumentReference.set(org.mockito.kotlin.any()))
        .thenReturn(Tasks.forResult(null)) // Simulate success

    // This test verifies that when we add a new ToDo, the Firestore `collection()` method is
    // called.
    journeysRepository.addJourney(journey, onSuccess = {}, onFailure = {})

    shadowOf(Looper.getMainLooper()).idle()

    // Ensure Firestore collection method was called to reference the "ToDos" collection
    verify(mockDocumentReference).set(org.mockito.kotlin.any())
  }

  @Test
  fun updateJourney_shouldCallDocumentReferenceDelete() {
    `when`(mockDocumentReference.set(org.mockito.kotlin.any()))
        .thenReturn(Tasks.forResult(null)) // Simulate success

    journeysRepository.updateJourney(journey, onSuccess = {}, onFailure = {})

    shadowOf(Looper.getMainLooper()).idle() // Ensure all asynchronous operations complete

    verify(mockDocumentReference).set(org.mockito.kotlin.any())
  }

  @Test
  fun deleteJourneyById_shouldCallDocumentReferenceDelete() {
    `when`(mockDocumentReference.delete()).thenReturn(Tasks.forResult(null))

    journeysRepository.deleteJourneyById("1", onSuccess = {}, onFailure = {})

    shadowOf(Looper.getMainLooper()).idle() // Ensure all asynchronous operations complete

    verify(mockDocumentReference).delete()
  }

  @Test
  fun documentToJourneyConvertsDocumentSnapshotToJourney() {
    // Arrange
    val documentSnapshot = mock<DocumentSnapshot>()

    whenever(documentSnapshot.id).thenReturn("uid1")
    whenever(documentSnapshot.getString("imageUrl")).thenReturn("http://image1.url")
    whenever(documentSnapshot.getString("description")).thenReturn("desc1")
    val locationMap =
        mapOf(
            "latitude" to 46.5183076,
            "longitude" to 6.6338096,
            "name" to
                "Coffee page, Rue du Midi, Lausanne, District de Lausanne, Vaud, 1003, Schweiz/Suisse/Svizzera/Svizra")
    whenever(documentSnapshot.get("location")).thenReturn(locationMap)
    whenever(documentSnapshot.getString("coffeeOrigin")).thenReturn("BRAZIL")
    whenever(documentSnapshot.getString("brewingMethod")).thenReturn("FRENCH_PRESS")
    whenever(documentSnapshot.getString("coffeeTaste")).thenReturn("BITTER")
    whenever(documentSnapshot.getString("coffeeRate")).thenReturn("ONE")
    whenever(documentSnapshot.getTimestamp("date")).thenReturn(mock())

    // Access the private method using reflection
    val method =
        JourneysRepositoryFirestore::class
            .java
            .getDeclaredMethod("documentTojourney", DocumentSnapshot::class.java)
    method.isAccessible = true // Make it accessible
    val journey = method.invoke(journeysRepository, documentSnapshot) as Journey

    // Assert
    assertNotNull(journey)
    assertEquals("uid1", journey.uid)
    assertEquals("http://image1.url", journey.imageUrl)
    assertEquals("desc1", journey.description)
    assertEquals(46.5183076, journey.location.latitude ?: 0.0, 0.0001)
    assertEquals(6.6338096, journey.location.longitude ?: 0.0, 0.0001)
    assertEquals(
        "Coffee page, Rue du Midi, Lausanne, District de Lausanne, Vaud, 1003, Schweiz/Suisse/Svizzera/Svizra",
        journey.location.name)
    assertEquals(CoffeeOrigin.BRAZIL, journey.coffeeOrigin)
    assertEquals(BrewingMethod.FRENCH_PRESS, journey.brewingMethod)
    assertEquals(CoffeeTaste.BITTER, journey.coffeeTaste)
    assertEquals(CoffeeRate.ONE, journey.coffeeRate)
    assertNotNull(journey.date)
  }

  @Test
  fun documentToJourneyException() {
    // Arrange
    val documentSnapshot = mock<DocumentSnapshot>()

    // Mock the documentSnapshot to return an invalid value for coffeeOrigin
    // This will cause CoffeeOrigin.valueOf() to throw an exception
    whenever(documentSnapshot.getString("coffeeOrigin")).thenReturn("INVALID_ORIGIN")

    // Mock static Log method using mockStatic from mockito-inline
    val logMock: MockedStatic<Log> = mockStatic(Log::class.java)

    // Use reflection to access the private method
    val method =
        JourneysRepositoryFirestore::class
            .java
            .getDeclaredMethod("documentTojourney", DocumentSnapshot::class.java)
    method.isAccessible = true

    // Create an instance of the repository
    val repository = JourneysRepositoryFirestore(mock())
    val journey = method.invoke(repository, documentSnapshot) as Journey?
    assertNull(journey) // Expecting null due to the exception being thrown

    // Verify that Log.e was called with the correct parameters
    logMock.verify {
      Log.e(
          eq("JourneysRepositoryFirestore"),
          eq("Error converting document to Journey"),
          any<Exception>())
    }

    // Clean up the static mock after use
    logMock.close()
  }

  @Test
  fun initCallsOnSuccess() {
    // Arrange
    val firebaseAuthMock = mock<FirebaseAuth>()
    val firebaseUserMock = mock<FirebaseUser>()

    // Mock Firebase Auth static methods
    val firebaseAuthStaticMock: MockedStatic<FirebaseAuth> = mockStatic(FirebaseAuth::class.java)

    // Mock getting the auth instance and its behavior
    whenever(FirebaseAuth.getInstance()).thenReturn(firebaseAuthMock)
    whenever(firebaseAuthMock.currentUser).thenReturn(firebaseUserMock) // Simulate a signed-in user

    val onSuccess: () -> Unit = mock()

    // Create the repository
    val repository = JourneysRepositoryFirestore(mock())

    // Act
    repository.init(onSuccess)

    // Capture and simulate triggering the AuthStateListener
    argumentCaptor<FirebaseAuth.AuthStateListener>().apply {
      verify(firebaseAuthMock).addAuthStateListener(capture())
      firstValue.onAuthStateChanged(firebaseAuthMock)
    }

    // Assert
    verify(onSuccess).invoke() // Ensure onSuccess is called

    // Clean up static mock
    firebaseAuthStaticMock.close()
  }
}
