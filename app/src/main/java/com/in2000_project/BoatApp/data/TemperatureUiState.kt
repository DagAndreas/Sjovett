package com.in2000_project.BoatApp.data

import com.example.gruppe_16.model.locationforecast.Timesery

data class TemperatureUiState(
    val coords: List<Double> = emptyList(),
    val timeList: List<Timesery> = emptyList()
)