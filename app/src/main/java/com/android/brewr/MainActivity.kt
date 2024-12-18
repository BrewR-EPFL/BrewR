package com.android.brewr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.android.brewr.model.coffee.CoffeesViewModel
import com.android.brewr.model.journey.ListJourneysViewModel
import com.android.brewr.model.user.UserViewModel
import com.android.brewr.resources.C
import com.android.brewr.ui.authentication.SignInScreen
import com.android.brewr.ui.explore.CoffeeInformationScreen
import com.android.brewr.ui.navigation.NavigationActions
import com.android.brewr.ui.navigation.Route
import com.android.brewr.ui.navigation.Screen
import com.android.brewr.ui.overview.AddJourneyScreen
import com.android.brewr.ui.overview.EditJourneyScreen
import com.android.brewr.ui.overview.JourneyRecordScreen
import com.android.brewr.ui.overview.OverviewScreen
import com.android.brewr.ui.theme.BrewRAppTheme
import com.android.brewr.ui.userProfile.InformationAboutUsScreen
import com.android.brewr.ui.userProfile.UserMainProfileScreen
import com.android.brewr.ui.userProfile.UserPrivateListScreen
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.firestore.ktx.persistentCacheSettings

class MainActivity : ComponentActivity() {
  private lateinit var auth: FirebaseAuth

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    FirebaseApp.initializeApp(this)

    // Configure Firestore settings for offline persistence
    val settings = firestoreSettings {
      setLocalCacheSettings(persistentCacheSettings {}) // Enable persistent disk cache
    }

    Firebase.firestore.firestoreSettings = settings

    auth = FirebaseAuth.getInstance()
    auth.currentUser?.let { auth.signOut() }
    setContent {
      BrewRAppTheme {
        Surface(
            modifier = Modifier.fillMaxSize().semantics { testTag = C.Tag.main_screen_container },
            color = MaterialTheme.colorScheme.background) {
              BrewRApp()
            }
      }
    }
  }
}

@Composable
fun BrewRApp() {
  val navController = rememberNavController()
  val navigationActions = NavigationActions(navController)

  val listJourneysViewModel: ListJourneysViewModel =
      viewModel(factory = ListJourneysViewModel.Factory)
  val userViewModel: UserViewModel = viewModel(factory = UserViewModel.Factory)
  val coffeesViewModel: CoffeesViewModel = viewModel(factory = CoffeesViewModel.Factory)
  val privateCoffeesViewModel: CoffeesViewModel = viewModel(factory = CoffeesViewModel.Factory)

  NavHost(navController, Route.AUTH) {
    navigation(
        startDestination = Screen.AUTH,
        route = Route.AUTH,
    ) {
      composable(Screen.AUTH) { SignInScreen(userViewModel, navigationActions) }
    }
    navigation(
        startDestination = Screen.OVERVIEW,
        route = Route.OVERVIEW,
    ) {
      composable(Screen.OVERVIEW) {
        OverviewScreen(listJourneysViewModel, coffeesViewModel, navigationActions)
      }
      composable(Screen.USERPROFILE) { UserMainProfileScreen(userViewModel, navigationActions) }
      composable(Screen.JOURNEY_RECORD) {
        JourneyRecordScreen(listJourneysViewModel, navigationActions)
      }
    }

    navigation(
        startDestination = Screen.ADD_JOURNEY,
        route = Route.ADD_JOURNEY,
    ) {
      composable(Screen.ADD_JOURNEY) { AddJourneyScreen(listJourneysViewModel, navigationActions) }
      composable(Screen.EDIT_JOURNEY) {
        EditJourneyScreen(listJourneysViewModel, navigationActions)
      }
    }

    navigation(
        startDestination = Screen.USERPROFILE,
        route = Route.USER_PROFILE,
    ) {
      composable(Screen.USER_PRIVATE_LIST) {
        UserPrivateListScreen(navigationActions, privateCoffeesViewModel)
      }
      composable(Screen.USER_PRIVATE_LIST_INFOS) {
        CoffeeInformationScreen(privateCoffeesViewModel, onBack = { navigationActions.goBack() })
      }
      composable(Screen.INFO_ABOUT_US) { InformationAboutUsScreen(navigationActions) }
    }
  }
}
