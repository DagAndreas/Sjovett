package com.in2000_project.BoatApp.viewmodel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.in2000_project.BoatApp.data.ApiDataSource
import com.in2000_project.BoatApp.data.TemperatureUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// disse skal endres til brukerens faktiske lokasjon
// latitude = north-south
var userLat = 59.911 // disse skal endres til brukerens faktiske lokasjon
// longitude = east-west
var userLng = 10.757

// url: https://api.met.no/weatherapi/locationforecast/2.0/complete?lat=57.3&lon=7.0

class LocationForecastViewModel {
    val _dataSource = ApiDataSource()
    private val _temperatureUiState = MutableStateFlow(TemperatureUiState())
    val temperatureUiState = _temperatureUiState.asStateFlow()
    init {
        fetchLocationForecastData(0.0,0.0)
    }

    fun fetchLocationForecastData(lat: Double, lng: Double) { // Henter data fra APIet
        Log.d("Fetch", "LocationForecast")
        CoroutineScope(Dispatchers.IO).launch {
            //val url = "https://api.met.no/weatherapi/locationforecast/2.0/complete?lat=59.9139&lon=10.7522"
            val url = "https://gw-uio.intark.uh-it.no/in2000/weatherapi/locationforecast/2.0/complete?lat=$lat&lon=$lng"
            _temperatureUiState.update {
                // setter warningList til å være en MetAlertsResponse
                (it.copy(timeList = _dataSource.fetchLocationForecastData(url).properties.timeseries))
            }
        }
    }
}