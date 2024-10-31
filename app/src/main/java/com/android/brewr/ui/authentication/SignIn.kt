package com.android.brewr.ui.authentication

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.android.brewr.R
import com.android.brewr.ui.navigation.NavigationActions
import com.android.brewr.ui.navigation.Screen
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.security.MessageDigest
import java.util.*
import kotlinx.coroutines.*

@Composable
fun SignInScreen(navigationActions: NavigationActions) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  val auth = Firebase.auth
  var user by remember { mutableStateOf(auth.currentUser) }

  // Initialize the Credential Manager
  val credentialManager = CredentialManager.create(context)

  // Launcher for adding a Google account if none are available
  val addAccountLauncher =
      rememberLauncherForActivityResult(
          contract = ActivityResultContracts.StartActivityForResult()) {
            // Retry sign-in after adding account
            doGoogleSignIn(
                credentialManager = credentialManager,
                auth = auth,
                context = context,
                coroutineScope = coroutineScope,
                navigationActions = navigationActions,
                addAccountLauncher = null, // Avoid infinite loop
                userState = mutableStateOf(user) // Pass 'user' state
                )
          }

  Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
    Column(
        modifier =
            Modifier.fillMaxSize()
                .padding(padding)
                .background(
                    brush =
                        Brush.verticalGradient(
                            colors =
                                listOf(
                                    Color.White, // White color
                                    Color(0xFFA17F59) // Light Brown color
                                    ))),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
      // App Logo Image
      Image(
          painter = painterResource(id = R.drawable.app_logo),
          contentDescription = "App Logo",
          modifier =
              Modifier.size(220.dp)
                  .clip(RoundedCornerShape(30.dp))
                  .border(3.dp, Color.DarkGray, RoundedCornerShape(30.dp)))

      Spacer(modifier = Modifier.height(16.dp))

      // Welcome Text
      Text(
          modifier = Modifier.testTag("loginTitle"),
          text = "BrewR",
          style = MaterialTheme.typography.headlineLarge.copy(fontSize = 57.sp, lineHeight = 64.sp),
          fontWeight = FontWeight.Bold,
          textAlign = TextAlign.Center)

      Spacer(modifier = Modifier.height(48.dp))

      if (user == null) {
        // Sign in with Google Button
        Button(
            onClick = {
              doGoogleSignIn(
                  credentialManager = credentialManager,
                  auth = auth,
                  context = context,
                  coroutineScope = coroutineScope,
                  navigationActions = navigationActions,
                  addAccountLauncher = addAccountLauncher,
                  userState = mutableStateOf(user) // Pass 'user' state
                  )
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            shape = RoundedCornerShape(50),
            modifier =
                Modifier.padding(8.dp)
                    .height(48.dp)
                    .padding(horizontal = 40.dp)
                    .testTag("loginButton")) {
              Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.Center,
                  modifier = Modifier.fillMaxWidth()) {
                    Image(
                        painter = painterResource(id = R.drawable.google_logo),
                        contentDescription = "Google Logo",
                        modifier = Modifier.size(30.dp).padding(end = 8.dp))
                    Text(
                        text = "Sign in with Google",
                        color = Color.Gray,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium)
                  }
            }
      }
    }
  }
}

/**
 * Helper function to initiate Google Sign-In.
 *
 * @param credentialManager The CredentialManager instance to handle credentials.
 * @param auth The FirebaseAuth instance for authentication.
 * @param context The context in which the function is called.
 * @param coroutineScope The CoroutineScope for launching coroutines.
 * @param navigationActions The NavigationActions instance for navigation.
 * @param addAccountLauncher The ActivityResultLauncher for adding a Google account.
 * @param userState The MutableState to hold the current FirebaseUser.
 */
private fun doGoogleSignIn(
    credentialManager: CredentialManager,
    auth: FirebaseAuth,
    context: Context,
    coroutineScope: CoroutineScope,
    navigationActions: NavigationActions,
    addAccountLauncher: ActivityResultLauncher<Intent>?,
    userState: MutableState<FirebaseUser?> // Pass the 'user' state variable
) {
  val googleSignInRequest =
      GetCredentialRequest.Builder().addCredentialOption(getGoogleIdOption(context)).build()

  coroutineScope.launch {
    try {
      val result = credentialManager.getCredential(context = context, request = googleSignInRequest)
      handleSignInResult(
          result = result,
          auth = auth,
          context = context,
          navigationActions = navigationActions,
          userState = userState // Pass 'user' state
          )
    } catch (e: NoCredentialException) {
      // No Google accounts available, prompt to add one
      addAccountLauncher?.launch(getAddGoogleAccountIntent())
    } catch (e: GetCredentialException) {
      e.printStackTrace()
      withContext(Dispatchers.Main) {
        Toast.makeText(context, "Error during sign-in: ${e.localizedMessage}", Toast.LENGTH_LONG)
            .show()
      }
    }
  }
}

/**
 * Generates a `GetGoogleIdOption` with a nonce to improve security.
 *
 * @param context The context in which the function is called.
 * @return A configured `GetGoogleIdOption` instance.
 */
private fun getGoogleIdOption(context: Context): GetGoogleIdOption {
  val rawNonce = UUID.randomUUID().toString()
  val bytes = rawNonce.toByteArray()
  val md = MessageDigest.getInstance("SHA-256")
  val digest = md.digest(bytes)
  val hashedNonce = digest.fold("") { str, it -> str + "%02x".format(it) }

  return GetGoogleIdOption.Builder()
      .setServerClientId(context.getString(R.string.default_web_client_id))
      .setFilterByAuthorizedAccounts(false)
      .setAutoSelectEnabled(true)
      .setNonce(hashedNonce)
      .build()
}

/**
 * Handles the result of the sign-in process.
 *
 * @param result The response containing the credential.
 * @param auth The FirebaseAuth instance for authentication.
 * @param context The context in which the function is called.
 * @param navigationActions The NavigationActions instance for navigation.
 * @param userState The MutableState to hold the current FirebaseUser.
 */
private suspend fun handleSignInResult(
    result: GetCredentialResponse,
    auth: FirebaseAuth,
    context: Context,
    navigationActions: NavigationActions,
    userState: MutableState<FirebaseUser?> // Receive 'user' state variable
) {
  when (val credential = result.credential) {
    is CustomCredential -> {
      if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
        try {
          val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
          val googleIdToken = googleIdTokenCredential.idToken

          val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
          auth.signInWithCredential(firebaseCredential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
              // Update the 'user' state variable
              userState.value = auth.currentUser
              Toast.makeText(context, "Login successful!", Toast.LENGTH_LONG).show()
              navigationActions.navigateTo(Screen.OVERVIEW)
            } else {
              Toast.makeText(context, "Login failed!", Toast.LENGTH_LONG).show()
            }
          }
        } catch (e: Exception) {
          Log.e("SignInScreen", "Error parsing Google ID token", e)
          withContext(Dispatchers.Main) {
            Toast.makeText(context, "Error parsing Google ID token", Toast.LENGTH_LONG).show()
          }
        }
      }
    }
    else -> {
      Log.e("SignInScreen", "Unexpected credential type")
      withContext(Dispatchers.Main) {
        Toast.makeText(context, "Unexpected credential type", Toast.LENGTH_LONG).show()
      }
    }
  }
}

/**
 * Creates an Intent to add a Google account.
 *
 * @return An Intent to launch the add account activity.
 */
private fun getAddGoogleAccountIntent(): Intent {
  val intent = Intent(Settings.ACTION_ADD_ACCOUNT)
  intent.putExtra(Settings.EXTRA_ACCOUNT_TYPES, arrayOf("com.google"))
  return intent
}
