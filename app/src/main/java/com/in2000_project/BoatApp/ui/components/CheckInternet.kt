package com.in2000_project.BoatApp.ui.components

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.in2000_project.BoatApp.R
import kotlinx.coroutines.*

class CheckInternet(private val cm: ConnectivityManager) {
    @RequiresApi(Build.VERSION_CODES.M)
    fun checkNetwork(): Boolean {
        val currentNetwork = cm.activeNetwork
        Log.d("Internet connection", currentNetwork.toString())
        return currentNetwork != null
    }
    @RequiresApi(Build.VERSION_CODES.M)
    private suspend fun waitUntilNetworkAvailable(): Unit = withContext(Dispatchers.IO) {
        while (!checkNetwork()) {
            Log.d("CurrentNetwork", "Looking for a network connection")
            delay(100)
        }
        Log.d("CurrentNetwork", "Found a connection!")
    }
    @RequiresApi(Build.VERSION_CODES.M)
    fun waitForNetwork(
        setTrue: ()->Unit
    ) {
        GlobalScope.launch {
            waitUntilNetworkAvailable()
            // The network is available, you can continue with your app logic here
            setTrue()
        }
    }

    //TODO: Her trenger vi en popup som sier til brukeren at de ikke er koblet til nett
    @Composable
    fun Popup(){

    }
}