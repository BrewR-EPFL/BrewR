package com.android.brewr.ui.explore

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import com.android.brewr.model.coffee.Coffee
import com.android.brewr.model.coffee.FavoriteCoffeesViewModel

/**
 * A composable button to like or unlike a coffee shop.
 *
 * This function renders a button that allows the user to add or remove a coffee shop from their list of favorites.
 * It dynamically observes whether the coffee shop is already in the user's favorites and updates the button's icon
 * accordingly. When clicked, the button either adds or removes the coffee from the favorites list using the provided
 * `FavoriteCoffeesViewModel` based on the current state of the coffee.
 *
 * @param coffee The `Coffee` object representing the coffee shop to be liked or unliked.
 * @param favoriteCoffeesViewModel The `FavoriteCoffeesViewModel` used to manage the favorite coffee shops.
 */
@Composable
fun FavoriteCoffeesButton(coffee: Coffee, favoriteCoffeesViewModel: FavoriteCoffeesViewModel) {

  // Observe the isInList result dynamically
  val isLiked by favoriteCoffeesViewModel.isCoffeeLiked(coffee).collectAsState()

  IconButton(
      modifier = Modifier.testTag("likedButton"),
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
