package com.android.brewr.ui.explore

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import com.android.brewr.model.coffee.Coffee
import com.android.brewr.model.coffee.FavoriteCoffeesViewModel

@Composable
fun FavoriteCoffeesButton(coffee: Coffee, favoriteCoffeesViewModel: FavoriteCoffeesViewModel) {

  // Observe the isInList result dynamically
  val isLiked by favoriteCoffeesViewModel.isCoffeeLiked(coffee).collectAsState()

  IconButton(
      onClick = {
        if (isLiked) {
          favoriteCoffeesViewModel.deleteCoffeeById(coffee.id)
        } else {
          favoriteCoffeesViewModel.addCoffee(coffee)
        }
      }) {
        Icon(
            imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = if (isLiked) "Unlike" else "Like",
            tint = if (isLiked) Color.Red else Color.Gray)
      }
}
