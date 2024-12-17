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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.android.brewr.R
import com.android.brewr.model.user.UserViewModel
import com.android.brewr.ui.navigation.NavigationActions
import com.android.brewr.ui.navigation.Screen
import com.android.brewr.utils.isConnectedToInternet
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
fun SignInScreen(userViewModel: UserViewModel, navigationActions: NavigationActions) {
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
            doGoogleSignIn(
                credentialManager = credentialManager,
                auth = auth,
                context = context,
                coroutineScope = coroutineScope,
                userViewModel = userViewModel,
                navigationActions = navigationActions,
                addAccountLauncher = null,
                userState = mutableStateOf(user))
          }

  Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
    Column(
        modifier = Modifier.fillMaxSize().padding(padding).background(color = Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
      // App Logo Image
      Image(
          painter = painterResource(id = R.drawable.app_logo),
          contentDescription = "App Logo",
          modifier = Modifier.size(500.dp))

      Spacer(modifier = Modifier.height(16.dp))

      if (user == null) {
        // Sign in with Google Button
        Button(
            onClick = {
              doGoogleSignIn(
                  credentialManager = credentialManager,
                  auth = auth,
                  context = context,
                  coroutineScope = coroutineScope,
                  userViewModel = userViewModel,
                  navigationActions = navigationActions,
                  addAccountLauncher = addAccountLauncher,
                  userState = mutableStateOf(user))
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
                  modifier =
                      Modifier.fillMaxWidth()
                          .height(40.dp * 1.5f)
                          .border(1.dp, Color.DarkGray, RoundedCornerShape(50))) {
                    Image(
                        painter = painterResource(id = R.drawable.google_logo),
                        contentDescription = "Google Logo",
                        modifier = Modifier.size(30.dp).padding(end = 8.dp))
                    Text(
                        text = "Sign in with Google",
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium)
                  }
            }
      }
    }
  }
}

// Helper function to initiate Google Sign-In
fun doGoogleSignIn(
    credentialManager: CredentialManager,
    auth: FirebaseAuth,
    context: Context,
    coroutineScope: CoroutineScope,
    userViewModel: UserViewModel,
    navigationActions: NavigationActions,
    addAccountLauncher: ActivityResultLauncher<Intent>?,
    userState: MutableState<FirebaseUser?>
) {
  val googleSignInRequest =
      GetCredentialRequest.Builder().addCredentialOption(getGoogleIdOption(context)).build()
  if (!isConnectedToInternet(context)) {
    Toast.makeText(
            context,
            "Log in unavailable, Please try again when the internet is back!",
            Toast.LENGTH_LONG)
        .show()
    return
  }
  coroutineScope.launch {
    try {
      val result = credentialManager.getCredential(context = context, request = googleSignInRequest)
      handleSignInResult(
          result = result,
          auth = auth,
          context = context,
          userViewModel = userViewModel,
          navigationActions = navigationActions,
          userState = userState)
    } catch (e: NoCredentialException) {
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

// Helper function to build Google ID Option
fun getGoogleIdOption(context: Context): GetGoogleIdOption {
  val rawNonce = UUID.randomUUID().toString()
  val digest = MessageDigest.getInstance("SHA-256").digest(rawNonce.toByteArray())
  val hashedNonce = digest.fold("") { str, it -> str + "%02x".format(it) }

  return GetGoogleIdOption.Builder()
      .setServerClientId(context.getString(R.string.default_web_client_id))
      .setFilterByAuthorizedAccounts(false)
      .setAutoSelectEnabled(true)
      .setNonce(hashedNonce)
      .build()
}

// Helper function to handle sign-in result
suspend fun handleSignInResult(
    result: GetCredentialResponse,
    auth: FirebaseAuth,
    context: Context,
    userViewModel: UserViewModel,
    navigationActions: NavigationActions,
    userState: MutableState<FirebaseUser?>
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
              userState.value = auth.currentUser
              Toast.makeText(context, "Login successful!", Toast.LENGTH_LONG).show()
              userViewModel.updateUserInfo()
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

// Helper function to get intent for adding a Google account
fun getAddGoogleAccountIntent(): Intent {
  val intent = Intent(Settings.ACTION_ADD_ACCOUNT)
  intent.putExtra(Settings.EXTRA_ACCOUNT_TYPES, arrayOf("com.google"))
  return intent
}
