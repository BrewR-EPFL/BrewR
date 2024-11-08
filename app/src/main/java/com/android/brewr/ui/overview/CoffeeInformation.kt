package com.android.brewr.ui.overview

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.android.brewr.model.coffee.Coffee
import com.android.brewr.model.coffee.Hours
import com.android.brewr.model.location.Location

@Composable
fun CoffeeInformationScreen(coffee: Coffee) {

  Scaffold(
      modifier = Modifier.testTag("coffeeInformationScreen"),
      content = { paddingValues ->
        Column(
            modifier =
                Modifier.fillMaxWidth()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()), // Add padding to the whole Column
            verticalArrangement = Arrangement.spacedBy(16.dp)) {

              // Image placeholder or uploaded image
              Box(modifier = Modifier.fillMaxWidth().height(200.dp).border(1.dp, Color.Gray)) {
                Image(
                    painter =
                        rememberAsyncImagePainter(
                            ImageRequest.Builder(LocalContext.current)
                                .data(coffee.imageUrl) // Load the image from the URL
                                .apply { crossfade(true) }
                                .build()),
                    contentDescription = "Uploaded Image",
                    modifier = Modifier.fillMaxWidth().height(200.dp).align(Alignment.Center))
              }

              Column(
                  modifier = Modifier.fillMaxWidth(),
                  verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = coffee.location.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "Opening hours: ${coffee.hours.open} - ${coffee.hours.close}",
                        fontSize = 14.sp,
                    )
                    Column {
                      Text(
                          text = "About",
                          fontSize = 14.sp,
                          fontWeight = FontWeight.Bold,
                      )
                      Text(
                          text = coffee.about,
                          fontSize = 14.sp,
                      )
                    }
                  }
            }
      })
}

@Preview(showBackground = true)
@Composable
fun CoffeeInformationScreenPreview() {
  val coffee =
      Coffee(
          Location(
              latitude = 48.87847905807652,
              longitude = 2.3562626423266946,
              name = "Caƒé tranquille"),
          imageUrl =
              "https://firebasestorage.googleapis.com/v0/b/brewr-epfl.appspot.com/o/images%2F2023-09-29.jpg?alt=media&token=eaaa9dbf-f402-4d12-b5ac-7c5589231a35",
          hours = com.android.brewr.model.coffee.Hours(open = "8:00 AM", close = "5:00 PM"),
          about = "Best coffee in the 10th arrondissement of Paris")
  CoffeeInformationScreen(coffee)
}
