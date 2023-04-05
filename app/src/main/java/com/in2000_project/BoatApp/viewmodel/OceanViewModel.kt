package com.in2000_project.BoatApp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.in2000_project.BoatApp.data.ApiDataSource
import com.example.gruppe_16.model.oceanforecast.OceanForecastResponse
import kotlinx.coroutines.launch

class OceanViewModel(path: String): ViewModel() {
    val dataSource = ApiDataSource()
    var oceanForecastResponse: OceanForecastResponse = getOceanForecastResponse(path)

    fun getOceanForecastResponse(path: String): OceanForecastResponse{
        viewModelScope.launch {
            oceanForecastResponse = dataSource.fetchOceanForecastData(path)
        }
        return oceanForecastResponse
    }
}

