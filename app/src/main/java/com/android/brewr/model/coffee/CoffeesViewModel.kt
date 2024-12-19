package com.android.brewr.model.coffee

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel class for managing a list of coffees and the currently selected coffee.
 *
 * This ViewModel uses [MutableStateFlow] to hold and manage the state of coffee data. It supports
 * operations such as adding coffees, selecting a specific coffee, and clearing the current list of
 * coffees. The ViewModel ensures state updates occur within a coroutine scope to maintain thread
 * safety.
 */
open class CoffeesViewModel : ViewModel() {
  private val coffees_ = MutableStateFlow<List<CoffeeShop>>(emptyList())
  val coffees: StateFlow<List<CoffeeShop>> = coffees_.asStateFlow()

  // Selected coffee, i.e the coffee for the detail view
  private val selectedCoffee_Shop_ = MutableStateFlow<CoffeeShop?>(null)
  open val selectedCoffeeShop: StateFlow<CoffeeShop?> = selectedCoffee_Shop_.asStateFlow()

  /**
   * Companion object providing a factory for creating instances of [CoffeesViewModel].
   *
   * This factory is useful when a ViewModel needs to be created programmatically or injected into a
   * lifecycle owner (e.g., in Android's ViewModelProvider).
   */
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

  /**
   * Updates the currently selected coffee.
   *
   * @param coffeeShop The [CoffeeShop] object to be set as the currently selected coffee. Observers
   *   of [selectedCoffeeShop] will receive the updated value.
   */
  fun selectCoffee(coffeeShop: CoffeeShop) {
    viewModelScope.launch { selectedCoffee_Shop_.value = coffeeShop }
  }

  /**
   * Updates the list of coffees.
   *
   * @param coffeesList A list of [CoffeeShop] objects to add to the state. Observers of [coffees]
   *   will receive the updated list of coffees.
   */
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
