package com.in2000_project.BoatApp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.in2000_project.BoatApp.data.ApiDataSource
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
        Log.d("API_request", "attempting getMetAlertsResponse.launch")
        viewModelScope.launch {
            metAlertsResponse = dataSource.fetchMetAlertsData(path)
        }

        Log.d("API_request", "getMetAlertsResponse.launch success")
        return metAlertsResponse
    }

    fun getOceanForecastResponse(path: String): OceanForecastResponse{
        Log.d("API_request", "attempting getOceanForecastResponse.launch")
        viewModelScope.launch {
            oceanForecastResponse = dataSource.fetchOceanForecastData(path)
        }
        Log.d("API_request", "getOceanForecastResponse.launch success")
        return oceanForecastResponse
    }

    fun getLocationForecastResponse(path: String): LocationForecastResponse{
        Log.d("API_request", "attempting getLocationForecastResponse.launch")
        viewModelScope.launch {
            locationForecastResponse = dataSource.fetchLocationForecastData(path)
        }
        Log.d("API_request", "getLocationForecastResponse.launch success")
        return locationForecastResponse
    }

}