package com.codingwithmitch.composegooglemaps.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codingwithmitch.composegooglemaps.data.ApiDataSource
import com.example.gruppe_16.model.locationforecast.LocationForecastResponse
import com.example.gruppe_16.model.metalerts.MetAlertsResponse
import com.example.gruppe_16.model.oceanforecast.OceanForecastResponse
import kotlinx.coroutines.launch

class ApiViewModel: ViewModel() {
    val dataSource = ApiDataSource();
    var metAlertsResponse: MetAlertsResponse
    var oceanForecastResponse: OceanForecastResponse
    var locationForecastResponse: LocationForecastResponse

    init {
        metAlertsResponse = getMetAlertsResponse("https://api.met.no/weatherapi/metalerts/1.1/.json")
        oceanForecastResponse = getOceanForecastResponse("https://api.met.no/weatherapi/oceanforecast/2.0/complete?lat=60.10&lon=5")
        locationForecastResponse = getLocationForecastResponse("https://api.met.no/weatherapi/locationforecast/2.0/compact?lat=60.10&lon=9.58")
    }
    fun getMetAlertsResponse(path: String): MetAlertsResponse{
        viewModelScope.launch {
            metAlertsResponse = dataSource.fetchMetAlertsData(path)
        }
        return metAlertsResponse
    }

    fun getOceanForecastResponse(path: String): OceanForecastResponse{
        viewModelScope.launch {
            oceanForecastResponse = dataSource.fetchOceanForecastData(path)
        }
        return oceanForecastResponse
    }

    fun getLocationForecastResponse(path: String): LocationForecastResponse{
        viewModelScope.launch {
            locationForecastResponse = dataSource.fetchLocationForecastData(path)
        }
        return locationForecastResponse
    }

}