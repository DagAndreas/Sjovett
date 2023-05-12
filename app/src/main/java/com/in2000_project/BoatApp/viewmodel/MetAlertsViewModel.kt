package com.in2000_project.BoatApp.viewmodel

import com.in2000_project.BoatApp.data.ApiDataSource
import com.in2000_project.BoatApp.data.StormWarningUiState
import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MetAlertsViewModel: ViewModel() {
    private val _dataSource = ApiDataSource()
    private val _stormWarningUiState = MutableStateFlow(StormWarningUiState())
    val stormWarningUiState = _stormWarningUiState.asStateFlow()

    init {
        fetchMetAlerts()
    }

    private fun fetchMetAlerts() { // Henter data fra APIet
        Log.d("Fetch", "MetAlerts")
        Log.d("API_request", "attempting fetchMetAlerts.launch")
        CoroutineScope(Dispatchers.IO).launch {
            // https://api.met.no/weatherapi/metalerts/1.1/.json
            val url = "https://gw-uio.intark.uh-it.no/in2000/weatherapi/metalerts/1.1/.json"
            _stormWarningUiState.update {
                // setter warningList til å være en MetAlertsResponse
                (it.copy(warningList = _dataSource.fetchMetAlertsData(url).features))
            }
        }
        Log.d("API_request", "fetchMetAlerts.launch success")
    }
}

