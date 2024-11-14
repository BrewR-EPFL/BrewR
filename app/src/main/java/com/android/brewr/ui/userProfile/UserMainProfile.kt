package com.android.brewr.ui.userProfile

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.brewr.ui.navigation.NavigationActions
import com.android.brewr.ui.navigation.Route
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserMainProfileScreen(navigationActions: NavigationActions) {
  val context = LocalContext.current
  var showDialog by remember { mutableStateOf(false) }

  Scaffold(
      modifier = Modifier.fillMaxSize().testTag("UserProfileScreen"),
      topBar = {
        TopAppBar(
            title = {},
            modifier = Modifier.testTag("topBar"),
            navigationIcon = {
              IconButton(
                  onClick = { navigationActions.goBack() }, Modifier.testTag("goBackButton")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back")
                  }
            })
      },
      content = { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp).padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
              Text(
                  "Username",
                  style = MaterialTheme.typography.headlineMedium,
                  modifier = Modifier.testTag("Username"))
              Text("Username@gmail.com", modifier = Modifier.testTag("User Email"))
              Spacer(Modifier.height(40.dp))

              // Top part with horizontally aligned icon buttons
              Row(
                  modifier = Modifier.fillMaxWidth().testTag("horizontally aligned icon buttons"),
                  horizontalArrangement = Arrangement.SpaceAround,
                  verticalAlignment = Alignment.CenterVertically,
              ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  IconButton(
                      onClick = {
                        Toast.makeText(context, "Feature not yet developed", Toast.LENGTH_SHORT)
                            .show()
                      },
                      Modifier.testTag("Preference button")) {
                        Icon(
                            imageVector = Icons.Default.Favorite, contentDescription = "Preference")
                      }
                  Text("Preference")
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  IconButton(
                      onClick = {
                        Toast.makeText(context, "Feature not yet developed", Toast.LENGTH_SHORT)
                            .show()
                      },
                      Modifier.testTag("Notification button")) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notification")
                      }
                  Text("Notification")
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  IconButton(
                      onClick = {
                        Toast.makeText(context, "Feature not yet developed", Toast.LENGTH_SHORT)
                            .show()
                      },
                      Modifier.testTag("TBD button")) {
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = "TBD")
                      }
                  Text("TBD")
                }
              }

              Spacer(Modifier.height(20.dp))
              ProfileMenuItem(
                  icon = Icons.Default.MoreVert,
                  label = "anything",
                  onClick = {
                    Toast.makeText(context, "Feature not yet developed", Toast.LENGTH_SHORT).show()
                  },
                  testTag = "anything")

              ProfileMenuItem(
                  icon = Icons.Default.Clear,
                  label = "sign out",
                  onClick = { showDialog = true },
                  testTag = "sign out")
              if (showDialog) {
                AlertDialog(
                    modifier = Modifier.testTag("Alter dialog"),
                    onDismissRequest = { showDialog = false },
                    title = { Text("Confirmation logout") },
                    text = { Text("Are you sure that you want to log out?") },
                    confirmButton = {
                      Button(
                          modifier = Modifier.testTag("button Yes"),
                          onClick = {
                            // sign out logic
                            Firebase.auth.signOut()
                            //                    user=null
                            Toast.makeText(context, "Signed out successfully", Toast.LENGTH_LONG)
                                .show()
                            showDialog = false
                            navigationActions.navigateTo(Route.AUTH)
                          }) {
                            Text("Yes")
                          }
                    },
                    dismissButton = {
                      Button(
                          modifier = Modifier.testTag("button No"),
                          onClick = { showDialog = false }) {
                            Text("NO")
                          }
                    })
              }
            }
      })
}

@Composable
fun ProfileMenuItem(icon: ImageVector, label: String, onClick: () -> Unit, testTag: String) {
  Row(
      modifier =
          Modifier.fillMaxWidth()
              .clickable { onClick() }
              .padding(vertical = 12.dp)
              .testTag(testTag),
      verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = label)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, fontSize = 16.sp)
      }
}
