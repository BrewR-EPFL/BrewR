package com.android.brewr.model.coffee

import com.android.brewr.model.coffee.Coffee

class CoffeeShopList {
    // Takes a list of Coffee objects and returns them sorted by rating in descending order
    fun getCoffeeShopsByHighRate(coffeeShops: List<Coffee>): List<Coffee> {
        return coffeeShops.sortedByDescending { it.rating }
    }
}
