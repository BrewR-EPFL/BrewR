package com.android.brewr.model.coffee

import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.android.brewr.model.location.Location
import com.android.brewr.model.user.User
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
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
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class CoffeesRepositoryFirestoreTest {

  private val mockDb: FirebaseFirestore = mock()
  private val mockAuth: FirebaseAuth = mock()
  private val mockUser: FirebaseUser = mock()
  private val mockCollection: CollectionReference = mock()
  private val mockUserDocument: DocumentReference = mock()
  private val mockQuerySnapshot: QuerySnapshot = mock()
  private val mockUserSnapshot: DocumentSnapshot = mock()
  private val repository = FavoriteCoffeesRepositoryFirestore(mockDb, mockAuth)

  private val coffee1 =
      Coffee(
          id = "1",
          coffeeShopName = "Sample Coffee Shop",
          location = Location(40.7128, 74.0060, "123 Main St"),
          rating = 4.5,
          hours = emptyList(),
          reviews = null,
          imagesUrls = emptyList())
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
    // Mock FirebaseAuth
    val authStateListenerCaptor = argumentCaptor<FirebaseAuth.AuthStateListener>()
    doNothing().`when`(mockAuth).addAuthStateListener(authStateListenerCaptor.capture())

    // Mock FirebaseAuth.currentUser
    `when`(mockAuth.currentUser).thenReturn(mockUser)

    // Mock Firestore document retrieval
    val mockTask: Task<DocumentSnapshot> = mock(Task::class.java) as Task<DocumentSnapshot>
    val mockDocument: DocumentSnapshot = mock(DocumentSnapshot::class.java)

    `when`(mockDb.collection("users").document(user.uid).get()).thenReturn(mockTask)
    `when`(mockTask.addOnSuccessListener(any())).thenAnswer { invocation ->
      (invocation.arguments[0] as OnSuccessListener<DocumentSnapshot>).onSuccess(mockDocument)
      mockTask
    }
    `when`(mockDocument.exists()).thenReturn(true)

    // Call init
    var successCalled = false
    repository.init { successCalled = true }

    // Simulate auth state change
    authStateListenerCaptor.firstValue.onAuthStateChanged(mockAuth)

    // Verify behavior
    //        assertTrue(successCalled)
    //        verify(mockDb.collection(anyString()).document(anyString())).get()
  }
}
