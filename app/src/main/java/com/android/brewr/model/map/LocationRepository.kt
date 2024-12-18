package com.android.brewr.model.map

/**
 * A functional interface for handling location searches.
 *
 * This interface allows searching for locations based on a query string. Results are returned
 * asynchronously through callback functions for success or failure.
 */
fun interface LocationRepository {

  /**
   * Searches for locations based on the provided query.
   *
   * @param query The search query string used to find locations.
   * @param onSuccess A callback invoked when the search is successful, providing a list of
   *   [Location] objects that match the query.
   * @param onFailure A callback invoked when the search fails, providing an [Exception] with
   *   details of the failure.
   */
  fun search(query: String, onSuccess: (List<Location>) -> Unit, onFailure: (Exception) -> Unit)
}
