package com.android.brewr.ui.overview

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.android.brewr.model.journey.Journey
import com.android.brewr.model.journey.ListJourneysViewModel
import com.android.brewr.ui.navigation.NavigationActions
import com.android.brewr.ui.navigation.Screen
import com.android.brewr.ui.theme.Purple80

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun OverviewScreen(
    listJourneysViewModel: ListJourneysViewModel =
        viewModel(factory = ListJourneysViewModel.Factory),
    navigationActions: NavigationActions
) {
  val context = LocalContext.current

  // State to track whether we're in "Gallery" or "Explore" mode
  var currentSection by remember { mutableStateOf("Gallery") }

  Scaffold(
      modifier = Modifier.testTag("overviewScreen"),
      topBar = {
        Column {
          TopAppBar(
              title = { Text(text = "BrewR", modifier = Modifier.testTag("appTitle")) },
              actions = {
                Row {
                  IconButton(
                      onClick = {
                        Toast.makeText(context, "Feature not yet developed", Toast.LENGTH_SHORT)
                            .show()
                      },
                      modifier = Modifier.testTag("addButton")) {
                        Icon(imageVector = Icons.Outlined.Add, contentDescription = "Add")
                      }
                  Spacer(modifier = Modifier.width(16.dp))
                  IconButton(
                      onClick = { navigationActions.navigateTo(Screen.USERPROFILE) },
                      modifier = Modifier.testTag("accountButton")) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Account")
                      }
                }
              })
          Box(
              modifier =
                  Modifier.fillMaxWidth()
                      .height(1.dp)
                      .background(androidx.compose.ui.graphics.Color.LightGray))
          Spacer(modifier = Modifier.height(8.dp))
          SubNavigationBar(
              currentSection = currentSection,
              onSectionChange = { section -> currentSection = section })
        }
      },
      content = { pd ->
        if (currentSection == "Gallery") {
          GalleryScreen(listJourneysViewModel, pd)
        } else {
          ExploreScreen()
        }
      })
}

@Composable
fun SubNavigationBar(currentSection: String, onSectionChange: (String) -> Unit) {
  Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp, horizontal = 8.dp)) {
    SubNavigationButton(
        text = "Gallery",
        isSelected = currentSection == "Gallery",
        onClick = { onSectionChange("Gallery") })
    Spacer(modifier = Modifier.width(6.dp))
    SubNavigationButton(
        text = "Explore",
        isSelected = currentSection == "Explore",
        onClick = { onSectionChange("Explore") })
  }
}

@Composable
fun SubNavigationButton(text: String, isSelected: Boolean = false, onClick: () -> Unit = {}) {
  Text(
      text = text,
      modifier =
          Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
              .clickable { onClick() }
              .background(
                  if (isSelected) Purple80 else androidx.compose.ui.graphics.Color.Gray,
                  RoundedCornerShape(8.dp))
              .padding(8.dp)
              .testTag(text))
}

@Composable
fun JourneyItem(journey: Journey, onClick: () -> Unit) {
  Card(
      modifier =
          Modifier.testTag("journeyListItem")
              .fillMaxWidth()
              .padding(vertical = 4.dp)
              .clickable(onClick = onClick),
  ) {
    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
      Text(text = journey.coffeeShopName) // Placeholder for coffee shop name
    }
  }
}

@Preview(showBackground = true)
@Composable
fun OverviewScreenPreview() {
  val navController = rememberNavController()
  val navigationActions = NavigationActions(navController)
  OverviewScreen(
      listJourneysViewModel = viewModel(factory = ListJourneysViewModel.Factory),
      navigationActions = navigationActions)
}
