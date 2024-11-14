package com.gems.toplan.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network

object NetworkUtils {
    fun observeNetworkChanges(context: Context, onAvailable: () -> Unit) {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                onAvailable()
            }
        }
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }
}
