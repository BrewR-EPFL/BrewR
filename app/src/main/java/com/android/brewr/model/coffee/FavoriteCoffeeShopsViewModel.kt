package com.android.brewr.model.coffee

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.*

open class FavoriteCoffeeShopsViewModel(private val repository: FavoriteCoffeeShopsRepository) :
    ViewModel() {
  private val favoriteCoffees_ = MutableStateFlow<List<CoffeeShop>>(emptyList())
  open val favoriteCoffees: StateFlow<List<CoffeeShop>> = favoriteCoffees_.asStateFlow()

  private val selectedCoffee_ = MutableStateFlow<CoffeeShop?>(null)
  open val selectedCoffee: StateFlow<CoffeeShop?> = selectedCoffee_.asStateFlow()

  init {
    repository.init { getCoffeeShops() }
  }

  // create factory
  companion object {
    val Factory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FavoriteCoffeeShopsViewModel::class.java)) {
              return FavoriteCoffeeShopsViewModel(
                  FavoriteCoffeeShopsRepositoryFirestore(
                      Firebase.firestore, FirebaseAuth.getInstance()))
                  as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
          }
        }
  }

  /** Gets all Coffee documents. */
  fun getCoffeeShops() {
    repository.getCoffeeShops(onSuccess = { favoriteCoffees_.value = it }, onFailure = {})
  }

  /**
   * Adds a Coffee document.
   *
   * @param coffee The Coffee document to be added.
   */
  fun addCoffeeShop(coffee: CoffeeShop) {
    repository.addCoffeeShop(coffee = coffee, onSuccess = { getCoffeeShops() }, onFailure = {})
  }

  /**
   * Deletes a Coffee document by its ID.
   *
   * @param id The ID of the Coffee document to be deleted.
   */
  fun deleteCoffeeShopById(id: String) {
    repository.deleteCoffeeShopById(id = id, onSuccess = { getCoffeeShops() }, onFailure = {})
  }

  /**
   * Selects a Coffee document.
   *
   * @param coffee The Coffee document to be selected.
   */
  fun selectCoffeeShop(coffee: CoffeeShop) {
    selectedCoffee_.value = coffee
  }

  /**
   * Checks if a specific coffee is liked by the user.
   *
   * This function observes the list of favorite coffees and maps it to a `StateFlow` of `Boolean`,
   * indicating whether the given coffee is in the list of favorites. The result is eagerly started
   * and provides a default value of `false` until the favorite coffees list is updated.
   *
   * @param coffee The `Coffee` object to check.
   * @return A `StateFlow<Boolean>` that emits `true` if the coffee is liked, or `false` otherwise.
   */
  fun isCoffeeShopLiked(coffee: CoffeeShop): StateFlow<Boolean> {
    return favoriteCoffees
        .map { coffeeList -> coffeeList.any { it.id == coffee.id } }
        .stateIn(scope = viewModelScope, started = SharingStarted.Eagerly, initialValue = false)
  }
}
