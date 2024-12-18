package com.android.brewr.model.map

import android.util.Log
import java.io.IOException
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray

/**
 * Repository implementation for fetching location data using the Nominatim API.
 *
 * This repository fetches location results based on a search query. The Nominatim API is queried
 * using an HTTP GET request, and the results are returned as a list of [Location].
 *
 * @param client The [OkHttpClient] used to execute HTTP requests.
 */
class NominatimLocationRepository(val client: OkHttpClient) : LocationRepository {

  /**
   * Parses the response body from the Nominatim API into a list of [Location].
   *
   * @param body The raw JSON response body as a [String].
   * @return A list of [Location] objects parsed from the response.
   */
  private fun parseBody(body: String): List<Location> {
    val jsonArray = JSONArray(body)

    return List(jsonArray.length()) { i ->
      val jsonObject = jsonArray.getJSONObject(i)
      val lat = jsonObject.getDouble("lat")
      val lon = jsonObject.getDouble("lon")
      val name = jsonObject.getString("display_name")
      Location(lat, lon, name)
    }
  }
  /**
   * Performs a location search using the Nominatim API.
   *
   * The method constructs an HTTP request with the provided query string, sends it asynchronously,
   * and parses the response into a list of [Location] objects. If the request is successful, the
   * results are passed to the [onSuccess] callback. Otherwise, the [onFailure] callback is
   * triggered.
   *
   * @param query The search query for fetching location results.
   * @param onSuccess A callback function invoked with the list of [Location] on success.
   * @param onFailure A callback function invoked with an [Exception] if the request fails.
   */
  override fun search(
      query: String,
      onSuccess: (List<Location>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    // Using HttpUrl.Builder to properly construct the URL with query parameters.
    val url =
        HttpUrl.Builder()
            .scheme("https")
            .host("nominatim.openstreetmap.org")
            .addPathSegment("search")
            .addQueryParameter("q", query)
            .addQueryParameter("format", "json")
            .build()

    // Create the request with a custom User-Agent and optional Referer
    val request =
        Request.Builder()
            .url(url)
            .header(
                "User-Agent", "YourAppName/1.0 (your-email@example.com)") // Set a proper User-Agent
            .header("Referer", "https://yourapp.com") // Optionally add a Referer
            .build()
    client
        .newCall(request)
        .enqueue(
            object : Callback {
              /**
               * Handles request failure.
               *
               * @param call The failed [Call] instance.
               * @param e The [IOException] that caused the failure.
               */
              override fun onFailure(call: Call, e: IOException) {
                Log.e("NominatimLocationRepository", "Failed to execute request", e)
                onFailure(e)
              }
              /**
               * Handles the API response.
               *
               * If the response is successful, it parses the body into a list of [Location]
               * objects. Otherwise, it triggers the failure callback.
               *
               * @param call The [Call] instance.
               * @param response The HTTP [Response] received.
               */
              override fun onResponse(call: Call, response: Response) {
                response.use {
                  if (!response.isSuccessful) {
                    onFailure(Exception("Unexpected code $response"))
                    Log.d("NominatimLocationRepository", "Unexpected code $response")
                    return
                  }

                  val body = response.body?.string()
                  if (body != null) {
                    onSuccess(parseBody(body))
                    Log.d("NominatimLocationRepository", "Body: $body")
                  } else {
                    Log.d("NominatimLocationRepository", "Empty body")
                    onSuccess(emptyList())
                  }
                }
              }
            })
  }
}
