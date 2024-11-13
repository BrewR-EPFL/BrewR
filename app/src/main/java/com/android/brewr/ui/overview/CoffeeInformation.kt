package com.android.brewr.ui.overview

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString as buildAnnotatedString1
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.android.brewr.model.coffee.Coffee

@SuppressLint("DefaultLocale")
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
                    .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)) {

              // Image placeholder or uploaded image
              Box(modifier = Modifier.fillMaxWidth().height(200.dp).border(1.dp, Color.Gray)) {
                Image(
                    painter =
                        rememberAsyncImagePainter(
                            ImageRequest.Builder(LocalContext.current)
                                .data(coffee.imagesUrls[0])
                                .apply { crossfade(true) }
                                .build()),
                    contentDescription = "Uploaded Image",
                    modifier =
                        Modifier.fillMaxWidth()
                            .height(200.dp)
                            .align(Alignment.Center)
                            .testTag("coffeeImage"))
              }

              Column(
                  modifier = Modifier.fillMaxWidth(),
                  verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = coffee.coffeeShopName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.testTag("coffeeShopName"))
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(start = 6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)) {
                          Text(
                              text =
                                  buildAnnotatedString1 {
                                    append("Address: ")
                                    addStyle(
                                        SpanStyle(fontWeight = FontWeight.Bold),
                                        0,
                                        "Address: ".length)
                                    append(coffee.location.address)
                                  },
                              fontSize = 16.sp,
                              modifier = Modifier.testTag("coffeeShopAddress"))

                          Text(
                              text =
                                  buildAnnotatedString1 {
                                    append("Opening Hours: ")
                                    addStyle(
                                        SpanStyle(fontWeight = FontWeight.Bold),
                                        0,
                                        "Opening Hours: ".length)
                                    append("${coffee.hours.open} - ${coffee.hours.close}")
                                  },
                              fontSize = 16.sp,
                              modifier = Modifier.testTag("coffeeShopHours"))

                          Row(
                              verticalAlignment = Alignment.CenterVertically,
                              modifier = Modifier.padding(bottom = 8.dp)) {
                                Text(
                                    text =
                                        buildAnnotatedString1 {
                                          append("Rating: ")
                                          addStyle(
                                              SpanStyle(fontWeight = FontWeight.Bold),
                                              0,
                                              "Rating: ".length)
                                          append(String.format("%.1f/5", coffee.rating))
                                        },
                                    fontSize = 16.sp,
                                    modifier = Modifier.testTag("coffeeShopRating"))
                              }
                        }
                  }
            }
      })
}
