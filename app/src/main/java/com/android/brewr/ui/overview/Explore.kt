package com.android.brewr.ui.overview

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.brewr.model.coffee.Coffee
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(coffees: List<Coffee>) {
  val sheetState = rememberModalBottomSheetState()
  val coroutineScope = rememberCoroutineScope()
  var showBottomSheet by remember { mutableStateOf(false) }

  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    MapScreen(coffees.map { it.location })

    if (showBottomSheet) {
      ModalBottomSheet(onDismissRequest = { showBottomSheet = false }, sheetState = sheetState) {
        Column(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.9f)) {
          CoffeeInformationScreen(coffees[0])
        }
      }
    }

    IconButton(
        onClick = { showBottomSheet = true },
        modifier = Modifier.align(Alignment.BottomStart).padding(20.dp).testTag("menuButton")) {
          Box(
              modifier =
                  Modifier.size(56.dp)
                      .clip(CircleShape)
                      .border(1.dp, Color.Black, CircleShape)
                      .padding(8.dp)) {
                Icon(
                    imageVector = Icons.Rounded.Menu,
                    contentDescription = "Menu",
                    tint = Color.Black,
                    modifier = Modifier.size(36.dp))
              }
        }

    LaunchedEffect(Unit) { coroutineScope.launch { sheetState.show() } }
  }
}
