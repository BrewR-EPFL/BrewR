package com.android.brewr.ui.userProfile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.brewr.ui.navigation.NavigationActions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InformationAboutUsScreen(navigationActions: NavigationActions) {

  Scaffold(
      modifier = Modifier.fillMaxSize().testTag("UserProfileScreen"),
      topBar = {
        TopAppBar(
            title = { Text("About BrewR") },
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
            modifier = Modifier.fillMaxSize().padding(40.dp).padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp)) {
              Text(
                  text =
                      "BrewR is a coffee exploration app created by a team of five EPFL students as part of the Software Enterprise (SwEnt) course. Our goal is to enhance the coffee experience for enthusiasts by combining technology with a love for coffee culture.",
                  modifier = Modifier.testTag("text1"))

              Text(
                  text =
                      "With BrewR, you can:\n" +
                          "Sign in with google and have your own account with coffee records and coffee shops\n" +
                          "Discover Nearby Coffee Shops: Find the best spots around you using GPS and Google Maps API.\n" +
                          "Get Personalized Recommendations: Enjoy tailored suggestions based on community ratings and your preferences.\n" +
                          "Keep a Tasting Journal: Record your coffee experiences, rate your favorites, and share your journey.\n" +
                          "Access Offline Mode: Plan visits even without internet connectivity.",
                  modifier = Modifier.testTag("text2"))

              Text(
                  text =
                      "This project embodies our commitment to leveraging modern software tools and methodologies while showcasing our passion for creating meaningful, user-centric applications.\n" +
                          "Join us on this journey to explore the rich and diverse world of coffee! \n" +
                          "Your coffee Journey, Perfectly Brewed",
                  modifier = Modifier.testTag("text3"))
            }
      })
}
