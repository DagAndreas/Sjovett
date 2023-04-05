package com.example.gruppe_16.model.oceanforecast


data class OceanForecastResponse(
    val geometry: Geometry,
    val properties: Properties,
    val type: String
)