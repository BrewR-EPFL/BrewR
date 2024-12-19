package com.android.brewr.ui.explore

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.android.brewr.model.coffee.CoffeesViewModel
import com.android.brewr.model.coffee.Review
import com.android.brewr.model.journey.ListJourneysViewModel
import com.android.brewr.ui.theme.CoffeeBrown
import com.android.brewr.ui.theme.LightBrown

/**
 * A composable screen displaying detailed information about a selected coffee shop.
 *
 * This screen displays:
 * - The coffee shop's name, image, address, rating, and operating hours.
 * - A list of user reviews with sorting options (Best or Worst).
 *
 * @param coffeesViewModel The ViewModel providing the selected coffee data.
 * @param onBack A callback invoked when the back button is pressed.
 */
@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoffeeInformationScreen(
    coffeesViewModel: CoffeesViewModel = viewModel(factory = ListJourneysViewModel.Factory),
    onBack: () -> Unit
) {
  val coffee = coffeesViewModel.selectedCoffeeShop.collectAsState().value ?: return
  var reviewSort by remember { mutableStateOf("Best") }

  Scaffold(
      modifier = Modifier.testTag("coffeeInformationScreen"),
      topBar = {
        TopAppBar(
            title = {
              Text(
                  coffee.coffeeShopName,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.testTag("coffeeShopName"))
            },
            navigationIcon = {
              IconButton(onClick = onBack, modifier = Modifier.testTag("backButton")) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back")
              }
            })
      },
      content = { paddingValues ->
        Column(
            modifier =
                Modifier.fillMaxWidth()
                    .padding(paddingValues)
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)) {
              // Images
              coffee.imagesUrls.take(1).forEach { imageUrl ->
                Image(
                    painter =
                        rememberAsyncImagePainter(
                            ImageRequest.Builder(LocalContext.current)
                                .data(imageUrl)
                                .crossfade(true)
                                .build()),
                    contentDescription = "Coffee Shop Image",
                    contentScale = ContentScale.Crop,
                    modifier =
                        Modifier.fillMaxWidth()
                            .heightIn(min = 150.dp, max = 300.dp)
                            .testTag("coffeeImage"))
              }

              // Location
              Column(
                  verticalArrangement = Arrangement.spacedBy(4.dp),
                  modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Address: ", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    Text(coffee.location.name, modifier = Modifier.testTag("coffeeShopAddress"))
                  }

              // Rating
              Text(
                  text =
                      buildAnnotatedString {
                        append("Rating: ")
                        addStyle(SpanStyle(fontWeight = FontWeight.Bold), 0, "Rating: ".length)
                        append(String.format("%.1f/5", coffee.rating))
                      },
                  fontSize = 16.sp,
                  modifier = Modifier.testTag("coffeeShopRating"))

              // Hours
              Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = "Operating Hours:", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                coffee.hours.forEach { hour ->
                  Text(
                      text = "${hour.day}: ${hour.open} - ${hour.close}",
                      modifier = Modifier.testTag("coffeeShopHour${hour.day}"))
                }
              }

              // Reviews (First 3 Only)
              if (!coffee.reviews.isNullOrEmpty()) {
                ReviewField(
                    coffee.reviews,
                    reviewSort,
                    listOf("Best", "Worst"),
                    onSortMethodChange = { reviewSort = it })
              } else {
                Text(text = "No reviews available.")
              }
            }
      })
}

/**
 * A composable function displaying a sorted list of reviews for a coffee shop.
 *
 * This function allows sorting reviews based on the selected method (e.g., Best or Worst). It
 * displays up to three reviews in a column layout.
 *
 * @param reviews The list of [Review] objects to display.
 * @param reviewSort The currently selected sorting method ("Best" or "Worst").
 * @param reviewSorts A list of available sorting methods.
 * @param onSortMethodChange A callback invoked when the sorting method is changed.
 */
@SuppressLint("DefaultLocale")
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ReviewField(
    reviews: List<Review>,
    reviewSort: String,
    reviewSorts: List<String>,
    onSortMethodChange: (String) -> Unit
) {
  Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
    FlowRow {
      reviewSorts.forEach { sorting ->
        // Determine if this method is the currently selected one
        val isSelected = reviewSort == sorting

        // Use Button or OutlinedButton based on selection
        if (isSelected) {
          Button(
              onClick = { onSortMethodChange(sorting) },
              shape = RoundedCornerShape(16.dp),
              modifier = Modifier.padding(4.dp).testTag("button${sorting}"),
              contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
              colors =
                  ButtonDefaults.buttonColors(
                      containerColor = LightBrown, contentColor = CoffeeBrown)) {
                Text("$sorting reviews", modifier = Modifier.padding(4.dp))
              }
        } else {
          OutlinedButton(
              onClick = { onSortMethodChange(sorting) },
              shape = RoundedCornerShape(16.dp),
              modifier = Modifier.padding(4.dp).testTag("button${sorting}"),
              contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
              colors =
                  ButtonDefaults.outlinedButtonColors(
                      contentColor = Color(0xFF000000),
                  )) {
                Text("$sorting reviews", modifier = Modifier.padding(4.dp))
              }
        }
      }
    }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      // Display Reviews
      reviews
          .sortedByDescending { if (reviewSort == "Best") it.rating else -it.rating }
          .take(3)
          .forEach { review ->
            Text(
                "- ${review.authorName}: \"${review.review}\" (${String.format("%.1f/5", review.rating)})",
                fontSize = 14.sp,
                modifier = Modifier.testTag("review${review.authorName}"))
          }
    }
  }
}
