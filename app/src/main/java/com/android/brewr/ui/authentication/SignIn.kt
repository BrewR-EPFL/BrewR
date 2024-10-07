import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.android.brewr.R
import kotlinx.coroutines.tasks.await

@Composable
fun SignInScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var oneTapClient by remember { mutableStateOf<SignInClient?>(null) }
    var auth by remember { mutableStateOf<FirebaseAuth?>(null) }
    var user by remember { mutableStateOf(Firebase.auth.currentUser) }

    LaunchedEffect(Unit) {
        oneTapClient = Identity.getSignInClient(context)
        auth = Firebase.auth
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            coroutineScope.launch {
                try {
                    val credential = oneTapClient?.getSignInCredentialFromIntent(result.data)
                    val idToken = credential?.googleIdToken
                    when {
                        idToken != null -> {
                            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                            auth?.signInWithCredential(firebaseCredential)
                                ?.addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Log.d("SignInScreen", "signInWithCredential:success")
                                        user = auth?.currentUser
                                        Toast.makeText(context, "Login successful!", Toast.LENGTH_LONG).show()
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
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Logo Image
            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = "App Logo",
                modifier = Modifier.size(250.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Welcome Text
            Text(
                text = "BrewR",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 57.sp,
                    lineHeight = 64.sp
                ),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            if (user == null) {
                // Sign in with Google Button
                Button(
                    onClick = {
                        coroutineScope.launch {
                            try {
                                val signInRequest = BeginSignInRequest.builder()
                                    .setGoogleIdTokenRequestOptions(
                                        BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                                            .setSupported(true)
                                            .setServerClientId(context.getString(R.string.default_web_client_id))
                                            .setFilterByAuthorizedAccounts(false)
                                            .build()
                                    )
                                    .build()

                                val result = oneTapClient?.beginSignIn(signInRequest)?.await()
                                result?.let {
                                    val intentSenderRequest = IntentSenderRequest.Builder(it.pendingIntent.intentSender).build()
                                    launcher.launch(intentSenderRequest)
                                }
                            } catch (e: Exception) {
                                Log.e("SignInScreen", "Error starting sign-in: ", e)
                                Toast.makeText(context, "Error starting sign-in: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .padding(8.dp)
                        .height(48.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.google_logo),
                            contentDescription = "Google Logo",
                            modifier = Modifier
                                .size(30.dp)
                                .padding(end = 8.dp)
                        )
                        Text(
                            text = "Sign in with Google",
                            color = Color.Gray,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                // Sign out Button
                Button(
                    onClick = {
                        Firebase.auth.signOut()
                        user = null
                        Toast.makeText(context, "Signed out successfully", Toast.LENGTH_LONG).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .padding(8.dp)
                        .height(48.dp)
                ) {
                    Text(
                        text = "Sign out",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}