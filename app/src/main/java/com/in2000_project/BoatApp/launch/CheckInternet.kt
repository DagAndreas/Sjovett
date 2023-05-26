package com.in2000_project.BoatApp.launch

import android.net.ConnectivityManager
import android.util.Log

/** Is used to check id the device is connected to the Internet */
class CheckInternet(private val cm: ConnectivityManager) {
    fun checkNetwork(): Boolean {
        val currentNetwork = cm.activeNetwork
        Log.d("Internet connection", currentNetwork.toString())
        return currentNetwork != null
    }
}