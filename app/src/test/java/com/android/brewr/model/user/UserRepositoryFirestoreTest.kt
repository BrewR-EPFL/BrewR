import android.net.Uri
import com.android.brewr.model.user.UserRepositoryFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class UserRepositoryFirestoreTest {

  @MockK private lateinit var mockFirestore: FirebaseFirestore

  @MockK private lateinit var mockFirebaseAuthLoggedIn: FirebaseAuth

  @RelaxedMockK private lateinit var mockFirebaseUser: FirebaseUser

  @MockK private lateinit var mockFirebaseAuthNotLoggedIn: FirebaseAuth

  private lateinit var userRepositoryLoggedIn: UserRepositoryFirestore
  private lateinit var userRepositoryNotLoggedIn: UserRepositoryFirestore

  @Before
  fun setUp() {
    MockKAnnotations.init(this)

    every { mockFirebaseAuthLoggedIn.currentUser } returns mockFirebaseUser
    every { mockFirebaseAuthNotLoggedIn.currentUser } returns null

    userRepositoryLoggedIn = UserRepositoryFirestore(mockFirestore, mockFirebaseAuthLoggedIn)
    userRepositoryNotLoggedIn = UserRepositoryFirestore(mockFirestore, mockFirebaseAuthNotLoggedIn)
  }

  @Test
  fun `getUserGmail() returns Gmail when user is logged in`() {
    val expectedEmail = "test@gmail.com"
    every { mockFirebaseUser.email } returns expectedEmail

    var result: String? = null
    userRepositoryLoggedIn.getUserGmail(onSuccess = { result = it }, onFailure = {})

    assertEquals(result, expectedEmail)
  }

  @Test
  fun `getUserGmail() fails when user is not logged in`() {
    val onFailure: (Exception) -> Unit = mockk(relaxed = true)
    userRepositoryNotLoggedIn.getUserGmail(onSuccess = {}, onFailure = onFailure)

    verify { onFailure.invoke(any()) }
  }

  @Test
  fun `getUsername() succeeds when user is logged in`() {
    val onSuccess: (String?) -> Unit = mockk(relaxed = true)

    userRepositoryLoggedIn.getUsername(onSuccess = onSuccess, onFailure = {})

    verify { onSuccess(any()) }
  }

  @Test
  fun `getUserName() fails when user is not logged in`() {
    val onFailure: (Exception) -> Unit = mockk(relaxed = true)
    userRepositoryNotLoggedIn.getUsername(onSuccess = {}, onFailure = onFailure)

    verify { onFailure(any()) }
  }

  @Test
  fun `getProfilePicture returns photo URL when user is logged in`() {
    val expectedUri = Uri.parse("http://example.com/profile.jpg")
    every { mockFirebaseUser.photoUrl } returns expectedUri

    val onSuccess: (Uri?) -> Unit = mockk(relaxed = true)
    userRepositoryLoggedIn.getProfilePicture(onSuccess = onSuccess, onFailure = {})

    verify { onSuccess(any()) }
  }

  @Test
  fun `getProfilePicture fails when user is not logged in`() {
    val onFailure: (Exception) -> Unit = mockk(relaxed = true)
    userRepositoryNotLoggedIn.getProfilePicture(onSuccess = {}, onFailure = onFailure)

    verify { onFailure(any()) }
  }
}
