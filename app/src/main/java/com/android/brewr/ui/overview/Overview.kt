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
import com.android.brewr.ui.navigation.NavigationActions
import com.android.brewr.ui.theme.Purple80

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun OverviewScreen(navigationActions: NavigationActions) {
  Scaffold(
      modifier = Modifier.testTag("overviewScreen"),
      topBar = {
        TopAppBar(
            title = { Text(text = "BrewR", modifier = Modifier.testTag("appTitle")) },
            actions = {
              Row {
                IconButton(onClick = {}, modifier = Modifier.testTag("addButton")) {
                  Icon(imageVector = Icons.Outlined.Add, contentDescription = "Add")
                }
                Spacer(modifier = Modifier.width(16.dp))
                IconButton(onClick = {}, modifier = Modifier.testTag("accountButton")) {
                  Icon(imageVector = Icons.Default.AccountCircle, contentDescription = "Account")
                }
              }
            })
      },
      content = { pd ->
        Column(modifier = Modifier.padding(pd)) {
          SubNavigationBar()
          Spacer(modifier = Modifier.height(16.dp))
        }
      })
}

@Composable
fun SubNavigationBar() {
  Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
    SubNavigationButton("Gallery") {}
    Spacer(modifier = Modifier.width(10.dp))
    SubNavigationButton("Explore") {}
  }
}

@Composable
fun SubNavigationButton(text: String, onClick: () -> Unit = {}) {

  Text(
      text = text,
      modifier =
          Modifier.padding(8.dp)
              .clickable { onClick() }
              .background(Purple80, RoundedCornerShape(8.dp))
              .padding(12.dp)
              .testTag(text))
}
