package com.android.brewr.model.journey

import android.os.Looper
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import com.android.brewr.model.map.Location
import com.android.brewr.model.user.User
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
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
  @Mock private lateinit var mockFirebaseAuth: FirebaseAuth
  @Mock private lateinit var mockFirebaseUser: FirebaseUser
  @Mock private lateinit var mockUserCollectionReference: CollectionReference
  @Mock private lateinit var mockJourneyCollectionReference: CollectionReference
  @Mock private lateinit var mockUserDocumentReference: DocumentReference
  @Mock private lateinit var mockJourneyDocumentReference: DocumentReference
  @Mock private lateinit var mockUserDocumentSnapshot: DocumentSnapshot
  @Mock private lateinit var mockJourneysQuerySnapshot: QuerySnapshot

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
  private val user = User(uid = "testUid", name = "test@example.com", journeys = listOf("journey1"))

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)
    // Initialize Firebase if necessary
    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }
    mockFirestore = mock(FirebaseFirestore::class.java)
    mockFirebaseAuth = mock(FirebaseAuth::class.java)
    journeysRepository = JourneysRepositoryFirestore(mockFirestore, mockFirebaseAuth)

    mockFirebaseUser = mock(FirebaseUser::class.java)
    `when`(mockFirebaseAuth.currentUser).thenReturn(mockFirebaseUser)
    `when`(mockFirebaseUser.uid).thenReturn("testUid")
    `when`(mockFirebaseUser.email).thenReturn("test@example.com")

    `when`(mockFirestore.collection("users")).thenReturn(mockUserCollectionReference)
    `when`(mockFirestore.collection("journeys")).thenReturn(mockJourneyCollectionReference)

    `when`(mockUserCollectionReference.document()).thenReturn(mockUserDocumentReference)
    `when`(mockUserCollectionReference.document(org.mockito.kotlin.any()))
        .thenReturn(mockUserDocumentReference)

    `when`(mockJourneyCollectionReference.document()).thenReturn(mockJourneyDocumentReference)
    `when`(mockJourneyCollectionReference.document(org.mockito.kotlin.any()))
        .thenReturn(mockJourneyDocumentReference)

    `when`(mockUserDocumentReference.get()).thenReturn(Tasks.forResult(mockUserDocumentSnapshot))
  }

  @Test
  fun testGetNewUid() {
    `when`(mockJourneyDocumentReference.id).thenReturn("1")
    val uid = journeysRepository.getNewUid()
    assert(uid == "1")
  }

  @Test
  fun `test getJourneys success`() {
    val mockUserTask: Task<DocumentSnapshot> = mock(Task::class.java) as Task<DocumentSnapshot>
    val mockJourneysTask: Task<QuerySnapshot> = mock(Task::class.java) as Task<QuerySnapshot>
    // Arrange

    `when`(mockUserDocumentReference.get()).thenReturn(mockUserTask)
    `when`(mockUserTask.addOnSuccessListener(any())).thenAnswer { invocation ->
      val listener = invocation.getArgument<OnSuccessListener<DocumentSnapshot>>(0)
      `when`(mockUserDocumentSnapshot.get("journeys")).thenReturn(listOf("id1", "id2"))
      listener.onSuccess(mockUserDocumentSnapshot)
      mockUserTask // Chain the task
    }
    `when`(mockJourneyCollectionReference.whereIn(anyString(), anyList()))
        .thenReturn(mockJourneyCollectionReference)
    `when`(mockJourneyCollectionReference.get()).thenReturn(mockJourneysTask)
    `when`(mockJourneysTask.addOnSuccessListener(any())).thenAnswer { invocation ->
      val listener = invocation.getArgument<OnSuccessListener<QuerySnapshot>>(0)
      `when`(mockJourneysQuerySnapshot.documents)
          .thenReturn(
              listOf(mock(DocumentSnapshot::class.java), mock(DocumentSnapshot::class.java)))
      listener.onSuccess(mockJourneysQuerySnapshot)
      mockJourneysTask // Chain the task
    }

    val successCaptor = argumentCaptor<List<Journey>>()

    // Act
    journeysRepository.getJourneys(onSuccess = { successCaptor.capture() }, onFailure = {})
  }

  @Test
  fun `test getJourneys success with no journeys`() {
    val mockUserTask: Task<DocumentSnapshot> = mock(Task::class.java) as Task<DocumentSnapshot>
    // Arrange
    whenever(mockUserDocumentReference.get()).thenReturn(mockUserTask)

    whenever(mockUserTask.addOnSuccessListener(any())).thenAnswer { invocation ->
      val listener = invocation.getArgument<OnSuccessListener<DocumentSnapshot>>(0)
      whenever(mockUserDocumentSnapshot.get("journeys")).thenReturn(null) // No journeys field
      listener.onSuccess(mockUserDocumentSnapshot)
      mockUserTask
    }
    val successCaptor = argumentCaptor<List<Journey>>()
    // Act
    journeysRepository.getJourneys(onSuccess = { successCaptor.capture() }, onFailure = {})
  }

  @Test
  fun addJourney_shouldCommitBatchSuccessfully() {
    // Mock Firestore batch
    val mockBatch = mock(WriteBatch::class.java)
    val fieldValue = FieldValue.arrayUnion(journey.uid)
    `when`(mockFirestore.batch()).thenReturn(mockBatch)

    // Mock batch operations
    `when`(mockBatch.update(mockUserCollectionReference.document(user.uid), "journeys", fieldValue))
        .thenReturn(mockBatch)
    `when`(mockBatch.set(mockJourneyCollectionReference.document("journey1"), journey))
        .thenReturn(mockBatch)
    `when`(mockBatch.commit()).thenReturn(Tasks.forResult(null)) // Simulate success

    journeysRepository.addJourney(
        journey, onSuccess = {}, onFailure = { fail("Failure callback should not be called") })

    // Verify batch operations were called
    verify(mockBatch)
        .update(eq(mockUserCollectionReference.document(user.uid)), eq("journeys"), any())
    verify(mockBatch).set(mockJourneyCollectionReference.document("journey1"), journey)
    verify(mockBatch).commit()
  }

  @Test
  fun updateJourney_shouldCallDocumentReferenceDelete() {
    `when`(mockJourneyDocumentReference.set(org.mockito.kotlin.any()))
        .thenReturn(Tasks.forResult(null)) // Simulate success

    journeysRepository.updateJourney(journey, onSuccess = {}, onFailure = {})

    shadowOf(Looper.getMainLooper()).idle() // Ensure all asynchronous operations complete

    verify(mockJourneyDocumentReference).set(org.mockito.kotlin.any())
  }

  @Test
  fun deleteJourneyById_shouldCallDocumentReferenceDeleted() {
    // Mock Firestore batch
    val mockBatch = mock(WriteBatch::class.java)
    `when`(mockFirestore.batch()).thenReturn(mockBatch)

    // Simulate batch update
    `when`(
            mockBatch.update(
                mockUserCollectionReference.document(user.uid),
                "journeys",
                FieldValue.arrayRemove("journey1")))
        .thenReturn(mockBatch)
    `when`(mockBatch.delete(mockJourneyCollectionReference.document("journey1")))
        .thenReturn(mockBatch)
    `when`(mockBatch.commit()).thenReturn(Tasks.forResult(null))

    // Call deleteJourneyById
    journeysRepository.deleteJourneyById(
        "journey1", onSuccess = {}, onFailure = { fail("Failure callback should not be called") })

    shadowOf(Looper.getMainLooper()).idle() // Ensure all asynchronous operations complete

    // Verify that the update and delete methods were called
    verify(mockBatch)
        .update(eq(mockUserCollectionReference.document(user.uid)), eq("journeys"), any())
    verify(mockBatch).delete(mockJourneyCollectionReference.document("journey1"))
    verify(mockBatch).commit()
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
    val repository = JourneysRepositoryFirestore(mock(), mockFirebaseAuth)
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
    whenever(firebaseAuthMock.currentUser).thenReturn(firebaseUserMock) // Simulate a signed-in
    user

    whenever(firebaseUserMock.uid).thenReturn(user.uid)
    whenever(firebaseUserMock.email).thenReturn(user.uid)

    whenever(mockUserCollectionReference.document(user.uid).get())
        .thenReturn(Tasks.forResult(mockUserDocumentSnapshot))
    whenever(mockUserDocumentSnapshot.exists()).thenReturn(true)
    val onSuccess: () -> Unit = mock()

    // Create the repository
    val repository = JourneysRepositoryFirestore(mockFirestore, firebaseAuthMock)

    // Act
    repository.init(onSuccess)

    // Capture and simulate triggering the AuthStateListener
    argumentCaptor<FirebaseAuth.AuthStateListener>().apply {
      verify(firebaseAuthMock).addAuthStateListener(capture())
      firstValue.onAuthStateChanged(firebaseAuthMock)
    }
    // Clean up static mock
    firebaseAuthStaticMock.close()
  }

  @Test
  fun `test retrieveJourneysOfAllOtherUsers success with multiple users`() {
    // Arrange
    val userIds = listOf("user1", "user2")
    val mockDocuments =
        userIds.map { userId ->
          val mockDocument: DocumentSnapshot = mock()
          whenever(mockDocument.id).thenReturn(userId)
          mockDocument
        }

    // Mock Firestore interactions
    whenever(mockFirebaseAuth.currentUser).thenReturn(mockFirebaseUser)
    whenever(mockFirebaseUser.uid).thenReturn("currentUserId")

    val mockUserTask: Task<QuerySnapshot> = mock()
    whenever(mockFirestore.collection("users")).thenReturn(mockUserCollectionReference)
    whenever(mockUserCollectionReference.get()).thenReturn(mockUserTask)

    // Mock QuerySnapshot behavior
    `when`(mockUserTask.addOnSuccessListener(any())).thenAnswer { invocation ->
      val successListener = invocation.getArgument<OnSuccessListener<QuerySnapshot>>(0)
      whenever(mockJourneysQuerySnapshot.documents).thenReturn(mockDocuments)
      successListener.onSuccess(mockJourneysQuerySnapshot)
      mockUserTask // Chain the task
    }
    // Act
    journeysRepository.retrieveJourneysOfAllOtherUsers(onSuccess = {}, onFailure = {})
  }
}
