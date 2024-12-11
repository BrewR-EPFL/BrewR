package com.android.brewr.model.coffee

interface FavoriteCoffeesRepository {

  fun init(onSuccess: () -> Unit)

  fun getCoffees(onSuccess: (List<Coffee>) -> Unit, onFailure: (Exception) -> Unit)

  fun addCoffee(coffee: Coffee, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  fun deleteCoffeeById(id: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
}
