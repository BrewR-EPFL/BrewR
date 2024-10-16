package com.android.brewr.ui.overview

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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
  val journeys = listJourneysViewModel.journeys.collectAsState()

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
          SubNavigationBar()
        }
      },
      content = { pd ->
        Column(modifier = Modifier.padding(pd)) { Spacer(modifier = Modifier.height(16.dp)) }
        if (journeys.value.isNotEmpty()) {
          LazyColumn(
              contentPadding = PaddingValues(vertical = 8.dp),
              modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(pd)) {
                items(journeys.value.size) { index ->
                  JourneyItem(journey = journeys.value[index]) {
                    listJourneysViewModel.selectJourney(journeys.value[index])
                  }
                }
              }
        } else {
          Box(modifier = Modifier.fillMaxSize().padding(pd), contentAlignment = Alignment.Center) {
            Text(
                modifier = Modifier.testTag("emptyJourneyPrompt"),
                text = "You have no Journey yet.")
          }
        }
      })
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
      Text(text = journey.coffeeShopName) // Change with image later
    }
  }
}

@Composable
fun SubNavigationBar() {
  Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp, horizontal = 8.dp)) {
    SubNavigationButton("Gallery") {}
    Spacer(modifier = Modifier.width(6.dp))
    SubNavigationButton("Explore") {}
  }
}

@Composable
fun SubNavigationButton(text: String, onClick: () -> Unit = {}) {
  Text(
      text = text,
      modifier =
          Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
              .clickable { onClick() }
              .background(Purple80, RoundedCornerShape(8.dp))
              .padding(8.dp)
              .testTag(text))
}
