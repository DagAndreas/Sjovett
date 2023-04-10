package com.in2000_project.BoatApp.model.oceanforecast

data class Details(
    val sea_surface_wave_from_direction: Double,
    val sea_surface_wave_height: Double,
    val sea_water_speed: Double,
    val sea_water_temperature: Double,
    val sea_water_to_direction: Double
)