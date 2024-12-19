package com.android.brewr.model.coffee

/**
 * Interface for managing favorite coffee data.
 *
 * This interface defines the operations that can be performed on the user's favorite coffee data,
 * such as initialization, fetching, adding, and deleting coffee entries. It abstracts the data
 * source and provides a contract for implementations to interact with favorite coffee storage.
 */
interface FavoriteCoffeeShopsRepository {

  /**
   * Initializes the repository.
   *
   * This method sets up any necessary resources or configurations required by the repository to
   * interact with the underlying data source. It should be called before performing other
   * operations on the repository.
   *
   * @param onSuccess Callback invoked when initialization is successful.
   */
  fun init(onSuccess: () -> Unit)

  /**
   * Retrieves the list of favorite coffees.
   *
   * Fetches all coffee entries marked as favorites from the data source. The results are provided
   * through the `onSuccess` callback. In case of an error, the `onFailure` callback is invoked with
   * the exception.
   *
   * @param onSuccess Callback invoked with the list of favorite coffees when the operation is
   *   successful.
   * @param onFailure Callback invoked with an exception if the operation fails.
   */
  fun getCoffeeShops(onSuccess: (List<CoffeeShop>) -> Unit, onFailure: (Exception) -> Unit)

  /**
   * Adds a coffee to the list of favorites.
   *
   * Stores a new coffee entry in the favorite coffees collection. On success, the `onSuccess`
   * callback is invoked. If an error occurs, the `onFailure` callback is invoked with the
   * exception.
   *
   * @param coffee The `Coffee` object to be added to the favorites.
   * @param onSuccess Callback invoked when the operation is successful.
   * @param onFailure Callback invoked with an exception if the operation fails.
   */
  fun addCoffeeShop(coffee: CoffeeShop, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  /**
   * Deletes a coffee from the list of favorites by its ID.
   *
   * Removes the specified coffee entry from the favorite coffees collection. On success, the
   * `onSuccess` callback is invoked. If an error occurs, the `onFailure` callback is invoked with
   * the exception.
   *
   * @param id The unique identifier of the coffee to be deleted.
   * @param onSuccess Callback invoked when the operation is successful.
   * @param onFailure Callback invoked with an exception if the operation fails.
   */
  fun deleteCoffeeShopById(id: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
}
