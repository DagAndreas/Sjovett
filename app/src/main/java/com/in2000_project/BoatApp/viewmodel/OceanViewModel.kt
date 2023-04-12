package com.in2000_project.BoatApp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.in2000_project.BoatApp.data.ApiDataSource
import com.in2000_project.BoatApp.model.oceanforecast.OceanForecastResponse
import kotlinx.coroutines.launch

class OceanViewModel(urlPath: String): ViewModel() {
    val _dataSource = ApiDataSource()
    var path: String = urlPath
    var oceanForecastResponseObject: OceanForecastResponse = getOceanForecastResponse()

    fun getOceanForecastResponse(): OceanForecastResponse{
        viewModelScope.launch {
            oceanForecastResponseObject = _dataSource.fetchOceanForecastData(path)
        }
        return oceanForecastResponseObject
    }

}

