package com.android.brewr.ui.authentication

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
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
import com.android.brewr.R
import com.android.brewr.ui.navigation.NavigationActions
import com.android.brewr.ui.navigation.Screen
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

@Composable
fun SignInScreen(navigationActions: NavigationActions) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()

  var oneTapClient by remember { mutableStateOf<SignInClient?>(null) }
  var auth by remember { mutableStateOf<FirebaseAuth?>(null) }
  var user by remember { mutableStateOf(Firebase.auth.currentUser) }

  LaunchedEffect(Unit) {
    oneTapClient = Identity.getSignInClient(context)
    auth = Firebase.auth
  }

  val launcher =
      rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
          result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
          try {
            val credential = oneTapClient?.getSignInCredentialFromIntent(result.data)
            val idToken = credential?.googleIdToken
            when {
              idToken != null -> {
                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                auth?.signInWithCredential(firebaseCredential)?.addOnCompleteListener { task ->
                  if (task.isSuccessful) {
                    Log.d("SignInScreen", "signInWithCredential:success")
                    user = auth?.currentUser
                    Toast.makeText(context, "Login successful!", Toast.LENGTH_LONG).show()
                    // Added navigation action to go to the Overview screen after login
                    navigationActions.navigateTo(Screen.OVERVIEW)
                  } else {
                    Log.w("SignInScreen", "signInWithCredential:failure", task.exception)

                    Toast.makeText(context, "Login failed!", Toast.LENGTH_LONG).show()
                  }
                }
              }
              else -> {
                Log.d("SignInScreen", "No ID token!")

                Toast.makeText(context, "Login failed: No ID token!", Toast.LENGTH_LONG).show()
              }
            }
          } catch (e: Exception) {
            Log.e("SignInScreen", "Error getting credential: ", e)

            Toast.makeText(context, "Login error: ${e.message}", Toast.LENGTH_LONG).show()
          }
        }
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
                                    Color(0xFFA17F59) // Light Brown (Caramel Macchiato color)
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
                  .border(2.dp, Color.DarkGray, RoundedCornerShape(30.dp)))

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
              coroutineScope.launch {
                try {
                  val signInRequest =
                      BeginSignInRequest.builder()
                          .setGoogleIdTokenRequestOptions(
                              BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                                  .setSupported(true)
                                  .setServerClientId(
                                      context.getString(R.string.default_web_client_id))
                                  .setFilterByAuthorizedAccounts(false)
                                  .build())
                          .build()

                  val result = oneTapClient?.beginSignIn(signInRequest)?.await()
                  result?.let {
                    val intentSenderRequest =
                        IntentSenderRequest.Builder(it.pendingIntent.intentSender).build()
                    launcher.launch(intentSenderRequest)
                  }
                } catch (e: Exception) {
                  Log.e("SignInScreen", "Error starting sign-in: ", e)
                  // Toast can only be called in the main thread
                  withContext(Dispatchers.Main) {
                    Toast.makeText(
                            context, "Error starting sign-in: ${e.message}", Toast.LENGTH_LONG)
                        .show()
                  }
                }
              }
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
