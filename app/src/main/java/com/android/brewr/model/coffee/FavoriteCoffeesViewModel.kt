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
  val favoriteCoffees: StateFlow<List<Coffee>> = favoriteCoffees_.asStateFlow()

  // Selected journey, i.e the journey for the detail view
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
   * @param coffee The Journey document to be added.
   */
  fun addCoffee(coffee: Coffee) {
    repository.addCoffee(coffee = coffee, onSuccess = { getCoffees() }, onFailure = {})
  }

  /**
   * Deletes a Journey document by its ID.
   *
   * @param id The ID of the Journey document to be deleted.
   */
  fun deleteCoffeeById(id: String) {
    repository.deleteCoffeeById(id = id, onSuccess = { getCoffees() }, onFailure = {})
  }

  /**
   * Selects a Coffee document.
   *
   * @param coffee The Journey document to be selected.
   */
  fun selectCoffee(coffee: Coffee) {
    selectedCoffee_.value = coffee
  }

  fun isCoffeeLiked(coffee: Coffee): StateFlow<Boolean> {
    return favoriteCoffees
        .map { coffeeList -> coffeeList.any { it.id == coffee.id } }
        .stateIn(scope = viewModelScope, started = SharingStarted.Eagerly, initialValue = false)
  }
}
