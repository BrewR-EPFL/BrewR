package com.android.brewr.ui.overview

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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
                      onClick = { navigationActions.navigateTo(Screen.ADD_JOURNEY) },
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
          GalleryScreen(listJourneysViewModel, pd, navigationActions)
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
