package com.android.brewr.ui.authentication

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
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
import com.android.brewr.R
import com.android.brewr.model.user.UserViewModel
import com.android.brewr.ui.navigation.NavigationActions
import com.android.brewr.ui.navigation.Screen
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*

@Composable
fun SignInScreen(userViewModel: UserViewModel, navigationActions: NavigationActions) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  val auth = Firebase.auth
  val isLoading = remember { mutableStateOf(false) }

  val launcher =
      rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result
        ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
          val account = task.getResult(Exception::class.java)
          val credential = GoogleAuthProvider.getCredential(account.idToken, null)

          coroutineScope.launch {
            auth.signInWithCredential(credential).addOnCompleteListener { taskResult ->
              if (taskResult.isSuccessful) {
                Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
                userViewModel.updateUserInfo()
                navigationActions.navigateTo(Screen.OVERVIEW)
              } else {
                Toast.makeText(context, "Login failed.", Toast.LENGTH_LONG).show()
              }
              isLoading.value = false
            }
          }
        } catch (e: Exception) {
          Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
          isLoading.value = false
        }
      }

  // Fullscreen White Background
  Scaffold(
      modifier =
          Modifier.fillMaxSize().background(Color.White), // Ensures the entire Scaffold is white
      containerColor = Color.White // Sets scaffold background color
      ) { padding ->
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .padding(padding)
                    .background(Color.White), // Ensures Column also has white background
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
              // App Logo
              Image(
                  painter = painterResource(id = R.drawable.app_logo),
                  contentDescription = "App Logo",
                  modifier = Modifier.size(500.dp))

              Spacer(modifier = Modifier.height(16.dp))

              // Google Sign-In Button
              if (!isLoading.value) {
                Button(
                    onClick = {
                      isLoading.value = true
                      val gso =
                          GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                              .requestIdToken(context.getString(R.string.default_web_client_id))
                              .requestEmail()
                              .build()
                      val googleSignInClient = GoogleSignIn.getClient(context, gso)
                      launcher.launch(googleSignInClient.signInIntent)
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
              } else {
                CircularProgressIndicator(modifier = Modifier.testTag("loadingSpinner"))
              }
            }
      }
}
