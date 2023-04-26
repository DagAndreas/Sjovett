package com.in2000_project.BoatApp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.in2000_project.BoatApp.data.ApiDataSource
import com.in2000_project.BoatApp.model.oceanforecast.OceanForecastResponse
import kotlinx.coroutines.launch

const val oceanURL = "https://gw-uio.intark.uh-it.no/in2000/weatherapi/oceanforecast/2.0/complete"

class OceanViewModel(urlPath: String): ViewModel() {
    val _dataSource = ApiDataSource()
    var path: String = urlPath
    var oceanForecastResponseObject: OceanForecastResponse = getOceanForecastResponse()
    var antallGangerHentet = 0

    fun getOceanForecastResponse(): OceanForecastResponse{
        viewModelScope.launch {
            oceanForecastResponseObject = _dataSource.fetchOceanForecastData(path)
            antallGangerHentet++
            Log.i("OceanViewModel", "hentet vanndata $antallGangerHentet ganger")
        }
        return oceanForecastResponseObject
    }
    fun setPath(pos: LatLng){
        path = "$oceanURL?lat=${pos.latitude}&lon=${pos.longitude}"
    }

}

