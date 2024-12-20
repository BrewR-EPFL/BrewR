package com.android.brewr.ui.recommendation

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.brewr.model.coffee.CoffeesViewModel
import com.android.brewr.model.recommendation.RecommendationViewModel
import com.android.brewr.ui.explore.CoffeeList
import com.android.brewr.ui.navigation.NavigationActions
import com.android.brewr.ui.navigation.Screen
import com.android.brewr.ui.theme.CoffeeBrown

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun RecommendScreen(
    recommendationViewModel: RecommendationViewModel,
    coffeesViewModel: CoffeesViewModel,
    navigationActions: NavigationActions
) {
  val recommendCoffeeShop = recommendationViewModel.recommendedCoffees.collectAsState().value
  var showPrivateCoffeeInfos by remember { mutableStateOf(false) }

  Scaffold(
      floatingActionButton = {
        FloatingActionButton(
            onClick = {
              recommendationViewModel.addRecommends()
              Log.d("recommendation before coffeelist", recommendCoffeeShop.toString())
            },
            containerColor = CoffeeBrown, // Replace CoffeeBrown with your actual color
            contentColor = Color.White, // Icon color
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp)) {
              Icon(imageVector = Icons.Default.Refresh, contentDescription = "Add Recommendation")
            }
      },
      content = { paddingValues ->
        if (recommendCoffeeShop.isNotEmpty()) {
          Column(
              modifier =
                  Modifier.fillMaxSize()
                      .testTag("privateList")
                      .padding(16.dp)
                      .padding(paddingValues),
              verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (!showPrivateCoffeeInfos) {
                  CoffeeList(recommendCoffeeShop.toList()) {
                    coffeesViewModel.selectCoffee(it)
                    showPrivateCoffeeInfos = true
                  }
                } else {
                  navigationActions.navigateTo(Screen.USER_PRIVATE_LIST_INFOS)
                }
              }
        } else {
          Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                modifier = Modifier.testTag("emptyJourneyPrompt").padding(horizontal = 16.dp),
                text =
                    "Discover personalized coffee recommendations by exploring and recording your journeys")
          }
        }
      })
}
