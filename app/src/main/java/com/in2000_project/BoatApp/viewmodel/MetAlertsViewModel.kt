package com.in2000_project.BoatApp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.in2000_project.BoatApp.data.ApiDataSource
import com.in2000_project.BoatApp.data.StormvarselUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MetAlertsViewModel : ViewModel() {
    private val _dataSource = ApiDataSource()
    private val _stormvarselUiState = MutableStateFlow(StormvarselUiState())
    val stormvarselUiState = _stormvarselUiState.asStateFlow()

    init {
        fetchMetAlerts()
    }

    private fun fetchMetAlerts() {
        Log.d("API_request", "attempting fetchMetAlerts.launch")
        CoroutineScope(Dispatchers.IO).launch {
            // Link to the original API: https://api.met.no/weatherapi/metalerts/1.1/.json
            val url = "https://gw-uio.intark.uh-it.no/in2000/weatherapi/metalerts/1.1/.json"
            _stormvarselUiState.update {
                (it.copy(warningList = _dataSource.fetchMetAlertsData(url).features))
            }
        }
        Log.d("API_request", "fetchMetAlerts.launch success")
    }
}

