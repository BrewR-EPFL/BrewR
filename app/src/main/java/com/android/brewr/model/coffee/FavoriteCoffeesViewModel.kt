package com.android.brewr.model.coffee

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.*

open class FavoriteCoffeesViewModel(private val repository: FavoriteCoffeesRepository) :
    ViewModel() {
  private val favoriteCoffees_ = MutableStateFlow<List<Coffee>>(emptyList())
  open val favoriteCoffees: StateFlow<List<Coffee>> = favoriteCoffees_.asStateFlow()

  private val selectedCoffee_ = MutableStateFlow<Coffee?>(null)
  open val selectedCoffee: StateFlow<Coffee?> = selectedCoffee_.asStateFlow()

  init {
    repository.init { getCoffees() }
  }

  // create factory
  companion object {
    val Factory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FavoriteCoffeesViewModel::class.java)) {
              return FavoriteCoffeesViewModel(
                  FavoriteCoffeesRepositoryFirestore(
                      Firebase.firestore, FirebaseAuth.getInstance()))
                  as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
          }
        }
  }

  /** Gets all Coffee documents. */
  fun getCoffees() {
    repository.getCoffees(onSuccess = { favoriteCoffees_.value = it }, onFailure = {})
  }

  /**
   * Adds a Coffee document.
   *
   * @param coffee The Coffee document to be added.
   */
  fun addCoffee(coffee: Coffee) {
    repository.addCoffee(coffee = coffee, onSuccess = { getCoffees() }, onFailure = {})
  }

  /**
   * Deletes a Coffee document by its ID.
   *
   * @param id The ID of the Coffee document to be deleted.
   */
  fun deleteCoffeeById(id: String) {
    repository.deleteCoffeeById(id = id, onSuccess = { getCoffees() }, onFailure = {})
  }

  /**
   * Selects a Coffee document.
   *
   * @param coffee The Coffee document to be selected.
   */
  fun selectCoffee(coffee: Coffee) {
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
  fun isCoffeeLiked(coffee: Coffee): StateFlow<Boolean> {
    return favoriteCoffees
        .map { coffeeList -> coffeeList.any { it.id == coffee.id } }
        .stateIn(scope = viewModelScope, started = SharingStarted.Eagerly, initialValue = false)
  }
}
