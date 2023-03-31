package com.codingwithmitch.composegooglemaps

import android.util.Log
import com.codingwithmitch.composegooglemaps.data.ApiDataSource
import com.codingwithmitch.composegooglemaps.data.TemperatureUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// disse skal endres til brukerens faktiske lokasjon
var userLat = 57.3
var userLng = 7.0

class LocationForecastViewModel {
    val _dataSource = ApiDataSource()
    private val _temperatureUiState = MutableStateFlow(TemperatureUiState())
    val temperatureUiState = _temperatureUiState.asStateFlow()

    init {
        fetchLocationForecastData()
    }

    fun fetchLocationForecastData() { // Henter data fra APIet
        Log.d("Fetch", "LocationForecast")
        CoroutineScope(Dispatchers.IO).launch {
            val url = "https://api.met.no/weatherapi/locationforecast/2.0/complete?lat=$userLat&lon=$userLng"
            _temperatureUiState.update {
                // setter warningList til å være en MetAlertsResponse
                (it.copy(timeList = _dataSource.fetchLocationForecastData(url).properties.timeseries))
            }
        }
    }
}