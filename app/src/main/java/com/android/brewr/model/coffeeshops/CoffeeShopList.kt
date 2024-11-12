package com.android.brewr.model.coffeeshops

import android.content.Context
import com.google.gson.Gson
import java.io.InputStreamReader

data class CoffeeShop(
    val id: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val rating: Double
)

class CoffeeShopList(private val context: Context) {

  // Loads and sorts the coffee shops by rating in descending order
  fun getCoffeeShopsByHighRate(): List<CoffeeShop> {
    val inputStream = context.assets.open("coffee_shops.json")
    val reader = InputStreamReader(inputStream)
    val coffeeShopListType = object : com.google.gson.reflect.TypeToken<List<CoffeeShop>>() {}.type
    val coffeeShops: List<CoffeeShop> = Gson().fromJson(reader, coffeeShopListType)

    return coffeeShops.sortedByDescending { it.rating }
  }
}
