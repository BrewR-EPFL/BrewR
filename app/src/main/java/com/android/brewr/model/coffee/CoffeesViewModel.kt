package com.android.brewr.model.coffee

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

open class CoffeesViewModel : ViewModel() {
  private val coffees_ = MutableStateFlow<List<Coffee>>(emptyList())
  val coffees: StateFlow<List<Coffee>> = coffees_.asStateFlow()

  // Selected coffee, i.e the coffee for the detail view
  private val selectedCoffee_ = MutableStateFlow<Coffee?>(null)
  open val selectedCoffee: StateFlow<Coffee?> = selectedCoffee_.asStateFlow()

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
  fun selectCoffee(coffee: Coffee) {
    viewModelScope.launch { selectedCoffee_.value = coffee }
  }

  // Function to add coffees
  fun addCoffees(coffeesList: List<Coffee>) {
    viewModelScope.launch { coffees_.value = coffeesList }
  }
}
