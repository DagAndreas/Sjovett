package com.in2000_project.BoatApp.model.oceanforecast

data class OceanForecastResponse(
    val geometry: Geometry,
    val properties: Properties,
    val type: String
)