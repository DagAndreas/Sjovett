package com.codingwithmitch.composegooglemaps.data

import com.example.gruppe_16.model.locationforecast.Timesery
import com.example.gruppe_16.model.locationforecast.Units

data class TemperatureUiState(
    val timeList: List<Timesery> = emptyList()
)