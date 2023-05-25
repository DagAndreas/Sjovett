package com.in2000_project.BoatApp.data

import com.in2000_project.BoatApp.model.geoCode.City

/** Holds the state of the list that is used in Stormvarsel */
data class GeoCodeUiState(
    val cityList: List<City> = emptyList()
)