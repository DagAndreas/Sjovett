package com.in2000_project.BoatApp

import android.net.ConnectivityManager
import android.util.Log
import androidx.compose.runtime.Composable
import kotlinx.coroutines.*

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

    //TODO: Her trenger vi en popup som sier til brukeren at de ikke er koblet til nett
    @Composable
    fun Popup(){

    }
}