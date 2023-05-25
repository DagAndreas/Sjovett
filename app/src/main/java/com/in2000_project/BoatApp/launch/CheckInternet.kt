package com.in2000_project.BoatApp.launch

import android.net.ConnectivityManager
import android.util.Log
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import kotlinx.coroutines.*

/** Is used to check id the device is connected to the Internet */
class CheckInternet(private val cm: ConnectivityManager) {
    fun checkNetwork(): Boolean {
        val currentNetwork = cm.activeNetwork
        Log.d("Internet connection", currentNetwork.toString())
        return currentNetwork != null
    }

    private suspend fun waitUntilNetworkAvailable(): Unit = withContext(Dispatchers.IO) {
        while (!checkNetwork()) {
            Log.d("CurrentNetwork", "Looking for a network connection")
            delay(100)
        }
        Log.d("CurrentNetwork", "Found a connection!")
    }
    @OptIn(DelicateCoroutinesApi::class)
    fun waitForNetwork(
        setTrue: ()->Unit
    ) {
        GlobalScope.launch {
            waitUntilNetworkAvailable()
            setTrue()
        }
    }

}