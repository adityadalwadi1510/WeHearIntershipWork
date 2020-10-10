package com.example.wehearintershipwork.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

class CheckInternet {
    // kotlin way declare static method
    companion object {
        //return whether device is connected to internet or not
        fun isOnline(context: Context): Boolean {

            //get connectivity Manager
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            //for the device above MarshMellow version
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //returns Network to the currently active default network otherwise null
                val network = connectivityManager.activeNetwork ?: return false

                val activeNetwork =
                    connectivityManager.getNetworkCapabilities(network) ?: return false
                //return true if device is connected to wifi or cellular
                return when {
                    activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    else -> false
                }
                //for the device below MarshMellow version
            } else {
                val nwInfo = connectivityManager.activeNetworkInfo ?: return false
                return nwInfo.isConnected
            }
        }
    }
}