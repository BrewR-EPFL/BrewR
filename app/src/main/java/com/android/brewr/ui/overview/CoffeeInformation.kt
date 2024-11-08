package com.android.brewr.ui.overview

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import com.android.brewr.model.location.Hours
import com.android.brewr.model.location.Location

@Composable
fun CoffeeInformationScreen(location: Location) {

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
                                .data(location.imageUrl) // Load the image from the URL
                                .apply { crossfade(true) }
                                .build()),
                    contentDescription = "Uploaded Image",
                    modifier = Modifier.fillMaxWidth().height(200.dp).align(Alignment.Center))
              }

              Column(
                  modifier = Modifier.fillMaxWidth(),
                  verticalArrangement =
                      Arrangement.spacedBy(8.dp) // Space between title and buttons
                  ) {
                    Text(
                        text = location.name,
                        fontSize = 16.sp, // Adjust the font size for the title
                        fontWeight = FontWeight.Bold, // Make the title bold
                    )
                    Text(
                        text = "Opening hours: ${location.hours.open} - ${location.hours.close}",
                        fontSize = 14.sp,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "About",
                        fontSize = 14.sp,
                    )
                    Text(
                        text = location.about,
                        fontSize = 14.sp,
                    )
                  }
            }
      })
}

@Preview(showBackground = true)
@Composable
fun CoffeeInformationScreenPreview() {
  val location =
      Location(
          name = "Sample Coffee Shop",
          imageUrl =
              "https://firebasestorage.googleapis.com/v0/b/brewr-epfl.appspot.com/o/images%2Fc00ae460-cf75-474a-8e05-bbcaf1d01709?alt=media&token=37e85904-64b9-4610-83db-bcd2e5b8c332",
          hours = Hours(open = "8:00 AM", close = "5:00 PM"),
          latitude = 46.5152778,
          longitude = 6.6286111,
          about = "A sample coffee shop for preview")
  CoffeeInformationScreen(location)
}
