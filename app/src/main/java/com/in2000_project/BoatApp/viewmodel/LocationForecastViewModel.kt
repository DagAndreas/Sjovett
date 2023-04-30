package com.in2000_project.BoatApp.viewmodel

import android.util.Log
import com.in2000_project.BoatApp.data.ApiDataSource
import com.in2000_project.BoatApp.data.TemperatureUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LocationForecastViewModel {
    private val _dataSource = ApiDataSource()
    private val _temperatureUiState = MutableStateFlow(TemperatureUiState())
    val temperatureUiState = _temperatureUiState.asStateFlow()

    init{
        fetchLocationForecastData(0.0, 0.0)
    }
    private fun fetchLocationForecastData(lat: Double, lng: Double) { // Henter data fra APIet
        Log.d("Fetch", "LocationForecast")
        CoroutineScope(Dispatchers.IO).launch {
            // https://api.met.no/weatherapi/locationforecast/2.0/complete?lat=59.9139&lon=10.7522
            val url = "https://gw-uio.intark.uh-it.no/in2000/weatherapi/locationforecast/2.0/complete?lat=$lat&lon=$lng"
            _temperatureUiState.update {
                (it.copy(timeList = _dataSource.fetchLocationForecastData(url).properties.timeseries))
            }
        }
    }

    fun updateUserCoord(lat: Double, lng: Double){
        fetchLocationForecastData(lat, lng)
    }
}