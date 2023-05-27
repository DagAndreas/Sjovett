package com.in2000_project.BoatApp.viewmodel

import android.util.Log
import com.in2000_project.BoatApp.data.ApiDataSource
import com.in2000_project.BoatApp.data.TemperatureUiState
import com.in2000_project.BoatApp.launch.CheckInternet
import com.in2000_project.BoatApp.launch.InternetPopupState
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

    init {
        // sets the location to 0.0, 0.0 to avoid
        fetchLocationForecastData(0.0, 0.0)
    }

    private fun fetchLocationForecastData(lat: Double, lng: Double) {
        Log.d("Fetch", "LocationForecast")
        CoroutineScope(Dispatchers.IO).launch {
            // Link to the original API: https://api.met.no/weatherapi/locationforecast/2.0/complete?lat=59.9139&lon=10.7522
            val url =
                "https://gw-uio.intark.uh-it.no/in2000/weatherapi/locationforecast/2.0/complete?lat=$lat&lon=$lng"
            _temperatureUiState.update {
                (it.copy(timeList = _dataSource.fetchLocationForecastData(url).properties.timeseries))
            }
        }
    }

    /** Collect new weatherData for the users coordinate */
    fun updateWeatherDataBasedOnCoordinate(
        lat: Double, lng: Double, connection: CheckInternet, internetPopupState: InternetPopupState
    ) {
        if (!connection.checkNetwork()) {
            Log.e("Internet connection", "Not connected!")
            internetPopupState.checkInternetPopup.value = true
        } else {
            internetPopupState.checkInternetPopup.value = false
            fetchLocationForecastData(lat, lng)
        }
    }
}