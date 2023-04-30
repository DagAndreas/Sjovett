package com.in2000_project.BoatApp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.in2000_project.BoatApp.data.ApiDataSource
import com.example.gruppe_16.model.locationforecast.LocationForecastResponse
import com.example.gruppe_16.model.metalerts.MetAlertsResponse
import com.in2000_project.BoatApp.model.oceanforecast.OceanForecastResponse
import com.in2000_project.BoatApp.model.seaOrLand.SeaOrLandResponse
import kotlinx.coroutines.launch

// TODO: Hele greia er ikke i bruk? Den er grå og vi får warning -> Kanskje flytte hver funksjon inn i viewmodelet de brukes og slette denne filen?
class ApiViewModel: ViewModel() {
    val dataSource = ApiDataSource()
    var metAlertsResponse: MetAlertsResponse
    var oceanForecastResponse: OceanForecastResponse
    var locationForecastResponse: LocationForecastResponse
    var seaOrLandResponse: SeaOrLandResponse

    init {
        // TODO: Er det nødvendig å gjøre disse kallene?
        metAlertsResponse = getMetAlertsResponse("https://gw-uio.intark.uh-it.no/in2000/weatherapi/metalerts/1.1/.json")
        oceanForecastResponse = getOceanForecastResponse("https://gw-uio.intark.uh-it.no/in2000/weatherapi/oceanforecast/2.0/complete?lat=60.10&lon=5")
        locationForecastResponse = getLocationForecastResponse("https://gw-uio.intark.uh-it.no/in2000/weatherapi/locationforecast/2.0/compact?lat=60.10&lon=9.58")
        seaOrLandResponse = SeaOrLandResponse(0.0, 0.0, true)
    }

    fun getMetAlertsResponse(path: String): MetAlertsResponse {
        Log.d("API_request", "attempting getMetAlertsResponse.launch")
        viewModelScope.launch {
            metAlertsResponse = dataSource.fetchMetAlertsData(path)
        }
        Log.d("API_request", "getMetAlertsResponse.launch success")
        return metAlertsResponse
    }

    fun getOceanForecastResponse(path: String): OceanForecastResponse {
        Log.d("API_request", "attempting getOceanForecastResponse.launch")
        viewModelScope.launch {
            oceanForecastResponse = dataSource.fetchOceanForecastData(path)
        }
        Log.d("API_request", "getOceanForecastResponse.launch success")
        return oceanForecastResponse
    }

    fun getLocationForecastResponse(path: String): LocationForecastResponse {
        Log.d("API_request", "attempting getLocationForecastResponse.launch")
        viewModelScope.launch {
            locationForecastResponse = dataSource.fetchLocationForecastData(path)
        }
        Log.d("API_request", "getLocationForecastResponse.launch success")
        return locationForecastResponse
    }

    fun getSeaOrLandResponse(path: String): SeaOrLandResponse { // TODO: Slett om den ikke skal brukes
        Log.d("API_request", "attempting getSeaOrLandResponse.launch")
        viewModelScope.launch {
            seaOrLandResponse = dataSource.fetchSeaOrLandResponse(path)
        }
        Log.d("API_request", "getSeaOrLandResponse.launch success")
        return seaOrLandResponse
    }
}
