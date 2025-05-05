package com.tvsm.iqubeindia.presentation.ui.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class NetworkMonitor @Inject constructor(
    @ApplicationContext private val context: Context,

    ) {

    enum class NetworkState {
        Available, Lost;

        fun isAvailable() = this == Available
    }

    private val TAG = "NetworkMonitor"
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val networkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .addTransportType(NetworkCapabilities.TRANSPORT_BLUETOOTH)
        .build()

    private fun checkForInternet(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            val isConnected = when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
            Log.d("InternetCheck", "Is Connected: $isConnected")
            return isConnected
        } else {
            @Suppress("DEPRECATION") val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION") val isConnected = networkInfo.isConnected
            Log.d("InternetCheck", "Is Connected: $isConnected")
            return isConnected
        }
    }



    private suspend fun isInternetAvailable(): Boolean {
        if (!checkForInternet()) {
            Log.d(TAG, "Internet not available")
            return false
        }

        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Checking internet connection by hitting URL...")
                val urlConnection =
                    URL("https://www.google.com").openConnection() as HttpsURLConnection
                urlConnection.setRequestProperty("User-Agent", "Android")
                urlConnection.setRequestProperty("Connection", "close")
                urlConnection.connectTimeout = 1000
                val responseCode = urlConnection.responseCode
                Log.d(TAG, "Response code received: $responseCode")
                val isConnected = responseCode == 200
                if (isConnected) {
                    Log.d(TAG, "Internet is connected")
                } else {
                    Log.d(TAG, "Internet is disconnected")
                }
                isConnected
            } catch (e: Exception) {
                Log.e(TAG, "Error checking internet connection", e)
                false
            }
        }
    }

    val networkState: Flow<Pair<NetworkState, Boolean>> = callbackFlow {
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                Log.d("NetworkMonitor", "onAvailable: Network available")
                launch { send(Pair(NetworkState.Available, isInternetAvailable())) }
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                Log.d("NetworkMonitor", "onLost: Network lost")
                launch { send(Pair(NetworkState.Lost, false)) }
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                super.onCapabilitiesChanged(network, networkCapabilities)
                val unmetered =
                    networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
                Log.d("NetworkMonitor", "onCapabilitiesChanged: unmetered:$unmetered")
                launch { send(Pair(NetworkState.Available, isInternetAvailable())) }
            }
        }

        try {
            connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
        } catch (e: Exception) {
            Log.e("NetworkMonitor", "Error registering network callback", e)
        }


        awaitClose {
            Log.d("NetworkMonitor", "awaitClose: Closing network state flow")
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }.distinctUntilChanged()
}