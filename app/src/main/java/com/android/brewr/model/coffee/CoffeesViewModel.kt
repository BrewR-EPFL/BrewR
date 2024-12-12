package com.android.brewr.model.coffee

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

open class CoffeesViewModel : ViewModel() {
  private val coffees_ = MutableStateFlow<List<CoffeeShop>>(emptyList())
  val coffees: StateFlow<List<CoffeeShop>> = coffees_.asStateFlow()

  // Selected coffee, i.e the coffee for the detail view
  private val selectedCoffee_Shop_ = MutableStateFlow<CoffeeShop?>(null)
  open val selectedCoffeeShop: StateFlow<CoffeeShop?> = selectedCoffee_Shop_.asStateFlow()

  // create factory
  companion object {
    val Factory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CoffeesViewModel::class.java)) {
              return CoffeesViewModel() as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
          }
        }
  }

  // Function to update selected coffee
  fun selectCoffee(coffeeShop: CoffeeShop) {
    viewModelScope.launch { selectedCoffee_Shop_.value = coffeeShop }
  }

  // Function to add coffees
  fun addCoffees(coffeesList: List<CoffeeShop>) {
    viewModelScope.launch { coffees_.value = coffeesList }
  }

  /**
   * Clears the current list of coffees.
   *
   * This method sets the coffees state to an empty list, effectively removing all coffee entries.
   * It can be used to reset the coffee data or simulate scenarios where no coffees are available.
   * This operation is performed within a coroutine scope to ensure thread safety.
   */
  fun clearCoffees() {
    viewModelScope.launch { coffees_.value = emptyList() }
  }
}
