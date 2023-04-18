package com.in2000_project.BoatApp.data

import com.in2000_project.BoatApp.model.geoCode.City

data class GeoCodeUiState(
    val cityList: List<City> = emptyList()
)