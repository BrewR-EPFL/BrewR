package com.android.brewr.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

/**
 * Checks whether the device is connected to the internet.
 *
 * This function utilizes the `ConnectivityManager` to assess the current active network and verify
 * if it has internet capability. It supports modern network APIs with `NetworkCapabilities`.
 *
 * @param context The context used to access the connectivity service.
 * @return `true` if the device is connected to the internet, `false` otherwise.
 * @see ConnectivityManager
 * @see NetworkCapabilities
 */
fun isConnectedToInternet(context: Context): Boolean {
  val connectivityManager =
      context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
  val activeNetwork = connectivityManager.activeNetwork ?: return false
  val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
  return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}
