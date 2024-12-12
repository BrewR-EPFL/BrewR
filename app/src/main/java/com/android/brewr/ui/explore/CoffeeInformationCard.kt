package com.android.brewr.ui.explore

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString as buildAnnotatedString1
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.android.brewr.model.coffee.CoffeeShop
import java.time.LocalDate

@SuppressLint("DefaultLocale")
@Composable
fun CoffeeInformationCardScreen(coffeeShop: CoffeeShop, onClick: () -> Unit) {
  Column(modifier = Modifier.fillMaxWidth()) {
    Image(
        painter =
            rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current)
                    .data(coffeeShop.imagesUrls[0])
                    .apply(
                        block =
                            fun ImageRequest.Builder.() {
                              crossfade(true)
                            })
                    .build()),
        contentDescription = "Selected Image",
        contentScale = ContentScale.Crop,
        modifier =
            Modifier.testTag("coffeeImage:${coffeeShop.id}")
                .fillMaxWidth()
                .heightIn(min = 180.dp, max = 300.dp)
                .clickable(onClick = onClick))
    Text(
        text = coffeeShop.coffeeShopName,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.testTag("coffeeShopName:${coffeeShop.id}"))
    Column(
        modifier = Modifier.fillMaxWidth().padding(start = 6.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Text(
              text =
                  buildAnnotatedString1 {
                    append("Address: ")
                    addStyle(SpanStyle(fontWeight = FontWeight.Bold), 0, "Address: ".length)
                    append(coffeeShop.location.address)
                  },
              fontSize = 16.sp,
              modifier = Modifier.testTag("coffeeShopAddress:${coffeeShop.id}"))

          Text(
              text =
                  buildAnnotatedString1 {
                    append("Opening Hours: ")
                    addStyle(
                        SpanStyle(fontWeight = FontWeight.Bold),
                        start = 0,
                        end = "Opening Hours: ".length)
                    val todayIndex = LocalDate.now().dayOfWeek.value - 1

                    if (todayIndex in coffeeShop.hours.indices) {
                      val todayHours = coffeeShop.hours[todayIndex]
                      append("${todayHours.open} - ${todayHours.close}")
                    } else {
                      append("Hours not available")
                    }
                  },
              fontSize = 16.sp,
              modifier = Modifier.testTag("coffeeShopHours:${coffeeShop.id}"))

          Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(bottom = 8.dp)) {
                Text(
                    text =
                        buildAnnotatedString1 {
                          append("Rating: ")
                          addStyle(SpanStyle(fontWeight = FontWeight.Bold), 0, "Rating: ".length)
                          append(String.format("%.1f/5", coffeeShop.rating))
                        },
                    fontSize = 16.sp,
                    modifier = Modifier.testTag("coffeeShopRating:${coffeeShop.id}"))
              }
        }
  }
}
