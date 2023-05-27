package com.in2000_project.BoatApp.data

import com.example.gruppe_16.model.locationforecast.Timesery

/** Holds the state of temperatures. Used in Stormvarsel */
data class TemperatureUiState(
    val coords: List<Double> = emptyList(), val timeList: List<Timesery> = emptyList()
)