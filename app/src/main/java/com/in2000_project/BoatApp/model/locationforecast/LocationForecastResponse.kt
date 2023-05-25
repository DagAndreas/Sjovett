package com.example.gruppe_16.model.locationforecast

/** We use this data class in locationForecast to display the proper data */
data class LocationForecastResponse(
    val geometry: Geometry,
    val properties: Properties,
    val type: String
)