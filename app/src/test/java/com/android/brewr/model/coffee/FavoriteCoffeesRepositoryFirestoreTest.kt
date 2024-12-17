package com.android.brewr.model.coffee

import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.android.brewr.model.journey.JourneysRepositoryFirestore
import com.android.brewr.model.location.Location
import com.android.brewr.model.user.User
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import junit.framework.TestCase.fail
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class CoffeesRepositoryFirestoreTest {

  @Mock private lateinit var mockDb: FirebaseFirestore
  @Mock private lateinit var mockAuth: FirebaseAuth
  @Mock private lateinit var mockUser: FirebaseUser
  @Mock private lateinit var mockCollection: CollectionReference
  @Mock private lateinit var mockUserDocument: DocumentReference
  @Mock private lateinit var mockQuerySnapshot: QuerySnapshot
  @Mock private lateinit var mockUserSnapshot: DocumentSnapshot

  private lateinit var repository: FavoriteCoffeesRepositoryFirestore

  private val coffee1 =
      Coffee(
          "1",
          coffeeShopName = "Café tranquille",
          Location(
              latitude = 48.87847905807652,
              longitude = 2.3562626423266946,
              address = "147 Rue du Faubourg Saint-Denis, 75010 Paris, France"),
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
  private val user =
      User(
          uid = "testUid",
          name = "test@example.com",
          journeys = listOf("journey1"),
          favoriteList = listOf(coffee1))

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)
    // Initialize Firebase if necessary
    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }
    repository = FavoriteCoffeesRepositoryFirestore(mockDb, mockAuth)

    // Arrange
    `when`(mockDb.collection("users")).thenReturn(mockCollection)
    `when`(mockCollection.document(anyString())).thenReturn(mockUserDocument)
    `when`(mockAuth.currentUser).thenReturn(mockUser)
    `when`(mockUser.uid).thenReturn("testUid")
    `when`(mockUser.email).thenReturn("test@example.com")
    `when`(mockDb.collection("users")).thenReturn(mockCollection)
    `when`(mockDb.collection("coffees")).thenReturn(mockCollection)
  }

  @Test
  fun `getCoffees should return empty list if user coffees list is empty`() {
    val userListenerCaptor = argumentCaptor<EventListener<DocumentSnapshot>>()
    doAnswer {
          userListenerCaptor.capture()
          null
        }
        .`when`(mockUserDocument)
        .addSnapshotListener(userListenerCaptor.capture())

    `when`(mockUserSnapshot.get("coffees")).thenReturn(emptyList<String>())

    var result: List<Coffee>? = null
    repository.getCoffees(onSuccess = { result = it }, onFailure = { throw it })

    // Act
    userListenerCaptor.firstValue.onEvent(mockUserSnapshot, null)

    // Assert
    assertTrue(result!!.isEmpty())
  }

  @Test
  fun `getCoffees should return list of coffees if Firestore query succeeds`() {
    // Arrange
    val coffeeIds = listOf("coffee1", "coffee2")
    val mockCoffeeDocument1: DocumentSnapshot = mock()
    val mockCoffeesCollection: CollectionReference = mock()
    val mockQuery: Query = mock() // Mock the Query object

    `when`(mockDb.collection("users")).thenReturn(mockCollection)
    `when`(mockCollection.document(anyString())).thenReturn(mockUserDocument)
    `when`(mockDb.collection("coffees")).thenReturn(mockCoffeesCollection)
    `when`(mockCoffeesCollection.whereIn(eq("id"), eq(coffeeIds)))
        .thenReturn(mockQuery) // Use matchers consistently

    val userListenerCaptor = argumentCaptor<EventListener<DocumentSnapshot>>()
    doAnswer {
          userListenerCaptor.capture()
          null
        }
        .`when`(mockUserDocument)
        .addSnapshotListener(userListenerCaptor.capture())

    `when`(mockUserSnapshot.get("coffees")).thenReturn(coffeeIds)
    // Capture coffee query snapshot listener
    val coffeeListenerCaptor = argumentCaptor<EventListener<QuerySnapshot>>()
    doAnswer {
          coffeeListenerCaptor.firstValue
          null
        }
        .`when`(mockQuery)
        .addSnapshotListener(coffeeListenerCaptor.capture()) // Attach listener to mock Query

    // Stub coffee documents returned by Firestore
    `when`(mockQuerySnapshot.documents).thenReturn(listOf(mockCoffeeDocument1))
    `when`(mockCoffeeDocument1.id).thenReturn("coffee1")
    `when`(mockCoffeeDocument1.getString("coffeeShopName")).thenReturn("coffee1")
    `when`(mockCoffeeDocument1["location"])
        .thenReturn(
            mapOf("latitude" to 37.7749, "longitude" to -122.4194, "address" to "Mock Address"))
    `when`(mockCoffeeDocument1.getDouble("rating")).thenReturn(4.5)
    `when`(mockCoffeeDocument1.get("hours"))
        .thenReturn(listOf(mapOf("day" to "Monday", "open" to "08:00", "close" to "18:00")))
    `when`(mockCoffeeDocument1.get("reviews"))
        .thenReturn(
            listOf(mapOf("authorName" to "John", "review" to "Great coffee!", "rating" to 5.0)))
    `when`(mockCoffeeDocument1.get("imagesUrls")).thenReturn(emptyList<String>())

    var result: List<Coffee>? = null
    repository.getCoffees(onSuccess = { result = it }, onFailure = { throw it })

    // Act
    userListenerCaptor.firstValue.onEvent(mockUserSnapshot, null)
    assertTrue(coffeeListenerCaptor.allValues.isNotEmpty()) // Verify listener was captured
    coffeeListenerCaptor.firstValue.onEvent(mockQuerySnapshot, null)
    // Assert
    assertEquals(1, result!!.size)
  }

  @Test
  fun `test addCoffee success`() {
    // Mock Firestore batch
    val mockBatch = mock(WriteBatch::class.java)
    val fieldValue = FieldValue.arrayUnion(coffee1.id)
    `when`(mockDb.batch()).thenReturn(mockBatch)

    // Mock batch operations
    `when`(mockBatch.update(mockCollection.document(user.uid), "coffees", fieldValue))
        .thenReturn(mockBatch)
    `when`(mockBatch.set(mockCollection.document(eq(coffee1.id)), coffee1)).thenReturn(mockBatch)
    `when`(mockBatch.commit()).thenReturn(Tasks.forResult(null)) // Simulate success

    repository.addCoffee(
        coffee1, onSuccess = {}, onFailure = { fail("Failure callback should not be called") })

    // Verify batch operations were called
    org.mockito.kotlin
        .verify(mockBatch)
        .update(eq(mockCollection.document(user.uid)), eq("coffees"), any())
    org.mockito.kotlin.verify(mockBatch).set(mockCollection.document(coffee1.id), coffee1)
    org.mockito.kotlin.verify(mockBatch).commit()
  }

  @Test
  fun deleteJourneyById_shouldCallDocumentReferenceDeleted() {
    // Mock Firestore batch
    val mockBatch = mock(WriteBatch::class.java)
    `when`(mockDb.batch()).thenReturn(mockBatch)

    // Simulate batch update
    `when`(
            mockBatch.update(
                mockCollection.document(user.uid), "coffees", FieldValue.arrayRemove(coffee1.id)))
        .thenReturn(mockBatch)
    `when`(mockBatch.commit()).thenReturn(Tasks.forResult(null))

    // Call deleteJourneyById
    repository.deleteCoffeeById(
        coffee1.id, onSuccess = {}, onFailure = { fail("Failure callback should not be called") })

    shadowOf(Looper.getMainLooper()).idle() // Ensure all asynchronous operations complete

    // Verify that the update and delete methods were called
    org.mockito.kotlin
        .verify(mockBatch)
        .update(eq(mockCollection.document(user.uid)), eq("coffees"), any())
    org.mockito.kotlin.verify(mockBatch).commit()
  }

  @Test
  fun `test init with user logged in and document exists`() {
    // Arrange
    val firebaseAuthMock = mock<FirebaseAuth>()
    val firebaseUserMock = mock<FirebaseUser>()

    // Mock Firebase Auth static methods
    val firebaseAuthStaticMock: MockedStatic<FirebaseAuth> = mockStatic(FirebaseAuth::class.java)

    // Mock getting the auth instance and its behavior
    whenever(FirebaseAuth.getInstance()).thenReturn(firebaseAuthMock)
    whenever(firebaseAuthMock.currentUser).thenReturn(firebaseUserMock) // Simulate a signed-in
    user

    whenever(firebaseUserMock.uid).thenReturn(user.uid)
    whenever(firebaseUserMock.email).thenReturn(user.uid)

    whenever(mockCollection.document(user.uid).get()).thenReturn(Tasks.forResult(mockUserSnapshot))
    whenever(mockUserSnapshot.exists()).thenReturn(true)
    val onSuccess: () -> Unit = mock()

    // Create the repository
    val repository = JourneysRepositoryFirestore(mockDb, firebaseAuthMock)

    // Act
    repository.init(onSuccess)

    // Capture and simulate triggering the AuthStateListener
    argumentCaptor<FirebaseAuth.AuthStateListener>().apply {
      org.mockito.kotlin.verify(firebaseAuthMock).addAuthStateListener(capture())
      firstValue.onAuthStateChanged(firebaseAuthMock)
    }
    verify(mockDb.collection("users").document(coffee1.id)).get()
    // Clean up static mock
    firebaseAuthStaticMock.close()
  }
}
