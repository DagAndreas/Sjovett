package com.example.gruppe_16.model.locationforecast

data class LocationForecastResponse(
    val geometry: Geometry,
    val properties: Properties,
    val type: String
)