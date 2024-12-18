package com.android.brewr.model.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.OkHttpClient

/**
 * ViewModel for managing location search queries and suggestions.
 *
 * This ViewModel handles user input for location searches, communicates with the provided
 * [LocationRepository], and exposes the search results as a StateFlow for UI observation.
 *
 * @param repository An implementation of [LocationRepository] used to fetch location suggestions.
 */
class LocationViewModel(val repository: LocationRepository) : ViewModel() {
  private val query_ = MutableStateFlow("")
  val query: StateFlow<String> = query_

  private var locationSuggestions_ = MutableStateFlow(emptyList<Location>())
  val locationSuggestions: StateFlow<List<Location>> = locationSuggestions_

  /**
   * Factory to create instances of [LocationViewModel].
   *
   * This companion object provides a [ViewModelProvider.Factory] that creates a new
   * [LocationViewModel] instance with a default [NominatimLocationRepository] implementation using
   * [OkHttpClient].
   */
  companion object {
    val Factory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return LocationViewModel(NominatimLocationRepository(OkHttpClient())) as T
          }
        }
  }

  /**
   * Updates the current query and fetches location suggestions.
   *
   * When a non-empty query is provided, the method performs a search using the repository. The
   * results are updated in the [locationSuggestions] state flow.
   *
   * @param query The search query string entered by the user.
   */
  fun setQuery(query: String) {
    query_.value = query

    if (query.isNotEmpty()) {
      repository.search(query, { locationSuggestions_.value = it }, {})
    }
  }
}
